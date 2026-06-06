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

module.exports = router;
