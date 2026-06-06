const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/auth');

// GET /api/stats/me — My stats
router.get('/me', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const user = await db.query('SELECT * FROM users WHERE id = $1', [req.user.id]);
    const achievements = await db.query(
      `SELECT a.* FROM achievements a
       JOIN user_achievements ua ON ua.achievement_id = a.id
       WHERE ua.user_id = $1`, [req.user.id]
    );
    const recentMatches = await db.query(
      `SELECT m.name, m.game_mode, m.finished_at, mp.kills, mp.deaths, mp.status
       FROM match_players mp JOIN matches m ON m.id = mp.match_id
       WHERE mp.user_id = $1 ORDER BY m.finished_at DESC LIMIT 20`, [req.user.id]
    );

    res.json({
      ...user.rows[0],
      achievements: achievements.rows,
      recent_matches: recentMatches.rows
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get stats' });
  }
});

// GET /api/stats/leaderboard — Global leaderboard (top 50 by K/D)
router.get('/leaderboard', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const result = await db.query(
      `SELECT
         u.id,
         u.display_name,
         u.avatar_url,
         u.total_kills,
         u.total_deaths,
         GREATEST(u.wins, u.total_wins) AS wins,
         u.total_matches,
         CASE WHEN u.total_deaths = 0 THEN u.total_kills
              ELSE ROUND(u.total_kills::numeric / u.total_deaths, 2)
         END AS kd_ratio
       FROM users u
       WHERE u.total_matches > 0
       ORDER BY kd_ratio DESC, u.total_kills DESC
       LIMIT 50`
    );
    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to get leaderboard' });
  }
});

// GET /api/stats/match/:id — Stats for a specific match
router.get('/match/:id', verifyToken, async (req, res) => {
  const { db } = req.app.locals;
  try {
    const players = await db.query(
      `SELECT mp.*, u.display_name, u.avatar_url
       FROM match_players mp
       JOIN users u ON u.id = mp.user_id
       WHERE mp.match_id = $1
       ORDER BY mp.kills DESC`, [req.params.id]
    );
    res.json({ players: players.rows });
  } catch (err) {
    res.status(500).json({ error: 'Failed to get match stats' });
  }
});

module.exports = router;
