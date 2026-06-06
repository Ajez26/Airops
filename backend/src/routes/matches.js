const express = require('express');
const router = express.Router();
const { v4: uuidv4 } = require('uuid');
const { verifyToken } = require('../middleware/auth');

// Generate short match code
function generateCode() {
  return Math.random().toString(36).substring(2, 8).toUpperCase();
}

// POST /api/matches — Create match (organizer only)
router.post('/', verifyToken, async (req, res) => {
  const { name, game_mode, max_players, game_config } = req.body;
  const { db } = req.app.locals;

  try {
    const code = generateCode();
    const result = await db.query(
      `INSERT INTO matches (id, code, name, organizer_id, game_mode, max_players, game_config)
       VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING *`,
      [uuidv4(), code, name, req.user.id, game_mode, max_players || 20, game_config || {}]
    );

    // Auto-create two teams for team modes
    if (['team_deathmatch', 'domination'].includes(game_mode)) {
      const match = result.rows[0];
      await db.query(
        `INSERT INTO teams (id, match_id, name, color) VALUES ($1,$2,$3,$4),($5,$6,$7,$8)`,
        [uuidv4(), match.id, 'Alpha', '#FF4444', uuidv4(), match.id, 'Bravo', '#4444FF']
      );
    }

    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to create match' });
  }
});

// POST /api/matches/join — Join match with code
router.post('/join', verifyToken, async (req, res) => {
  const { code } = req.body;
  const { db } = req.app.locals;

  try {
    const match = await db.query(
      `SELECT * FROM matches WHERE code = $1 AND status = 'lobby'`, [code.toUpperCase()]
    );
    if (!match.rows.length) return res.status(404).json({ error: 'Match not found or already started' });

    // Assign to team with fewer players
    const teams = await db.query(`SELECT t.*, COUNT(mp.id) as player_count 
      FROM teams t LEFT JOIN match_players mp ON t.id = mp.team_id 
      WHERE t.match_id = $1 GROUP BY t.id ORDER BY player_count ASC`, [match.rows[0].id]);

    const teamId = teams.rows[0]?.id || null;

    await db.query(
      `INSERT INTO match_players (id, match_id, user_id, team_id) VALUES ($1,$2,$3,$4) ON CONFLICT DO NOTHING`,
      [uuidv4(), match.rows[0].id, req.user.id, teamId]
    );

    res.json({ match: match.rows[0], team_id: teamId });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to join match' });
  }
});

// GET /api/matches/:id — Match state
router.get('/:id', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const match = await db.query(`SELECT * FROM matches WHERE id = $1`, [req.params.id]);
    const players = await db.query(
      `SELECT mp.*, u.display_name, u.avatar_url FROM match_players mp
       JOIN users u ON u.id = mp.user_id WHERE mp.match_id = $1`, [req.params.id]
    );
    const teams = await db.query(`SELECT * FROM teams WHERE match_id = $1`, [req.params.id]);

    res.json({ ...match.rows[0], players: players.rows, teams: teams.rows });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get match' });
  }
});

// POST /api/matches/:id/start — Start match (organizer only)
router.post('/:id/start', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const result = await db.query(
      `UPDATE matches SET status = 'active', started_at = NOW() 
       WHERE id = $1 AND organizer_id = $2 RETURNING *`,
      [req.params.id, req.user.id]
    );
    if (!result.rows.length) return res.status(403).json({ error: 'Not authorized or match not found' });
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Failed to start match' });
  }
});

// PATCH /api/matches/:id/player — Update player status/location
router.patch('/:id/player', verifyToken, async (req, res) => {
  const { status, lat, lng } = req.body;
  const { db } = req.app.locals;
  try {
    const fields = [];
    const values = [];
    let i = 1;
    if (status) { fields.push(`status = $${i++}`); values.push(status); }
    if (lat !== undefined) { fields.push(`lat = $${i++}`); values.push(lat); }
    if (lng !== undefined) { fields.push(`lng = $${i++}`); values.push(lng); }
    if (lat !== undefined || lng !== undefined) { fields.push(`last_location_at = NOW()`); }

    values.push(req.params.id, req.user.id);
    await db.query(
      `UPDATE match_players SET ${fields.join(', ')} WHERE match_id = $${i} AND user_id = $${i+1}`,
      values
    );
    res.json({ ok: true });
  } catch (err) {
    res.status(500).json({ error: 'Failed to update player' });
  }
});

// POST /api/matches/:id/kill — Report a kill
router.post('/:id/kill', verifyToken, async (req, res) => {
  const { target_user_id } = req.body;
  const { db } = req.app.locals;
  try {
    // Increment killer's kill count
    await db.query(
      `UPDATE match_players SET kills = kills + 1 WHERE match_id = $1 AND user_id = $2`,
      [req.params.id, req.user.id]
    );
    // Mark target as dead and increment deaths
    if (target_user_id) {
      await db.query(
        `UPDATE match_players SET deaths = deaths + 1, status = 'dead' WHERE match_id = $1 AND user_id = $2`,
        [req.params.id, target_user_id]
      );
    }
    res.json({ ok: true });
  } catch (err) {
    res.status(500).json({ error: 'Failed to report kill' });
  }
});

// POST /api/matches/:id/end — End match (organizer only)
router.post('/:id/end', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const result = await db.query(
      `UPDATE matches SET status = 'finished', finished_at = NOW()
       WHERE id = $1 AND organizer_id = $2 RETURNING *`,
      [req.params.id, req.user.id]
    );
    if (!result.rows.length)
      return res.status(403).json({ error: 'Not authorized or match not found' });

    // Aggregate kills/deaths into users.total_kills, total_deaths, total_matches, wins
    try {
      const players = await db.query(
        `SELECT mp.user_id, mp.kills, mp.deaths, mp.team_id FROM match_players mp WHERE mp.match_id = $1`,
        [req.params.id]
      );

      // Determine winning team by total kills
      const teamKills = {};
      players.rows.forEach(p => {
        if (p.team_id) teamKills[p.team_id] = (teamKills[p.team_id] || 0) + p.kills;
      });
      const winTeamId = Object.keys(teamKills).sort((a,b) => teamKills[b] - teamKills[a])[0];

      for (const p of players.rows) {
        const isWinner = p.team_id && p.team_id === winTeamId;
        await db.query(
          `UPDATE users SET
             total_kills = total_kills + $1,
             total_deaths = total_deaths + $2,
             total_matches = total_matches + 1,
             wins = wins + $3
           WHERE id = $4`,
          [p.kills || 0, p.deaths || 0, isWinner ? 1 : 0, p.user_id]
        );
      }
    } catch (aggErr) {
      console.error('Aggregation error (non-fatal):', aggErr.message);
    }

    res.json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to end match' });
  }
});

module.exports = router;
