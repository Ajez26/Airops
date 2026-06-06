const express = require('express');
const router = express.Router();
const { verifyToken } = require('../middleware/auth');

// GET /api/players/:id — Public player profile
router.get('/:id', async (req, res) => {
  const { db } = req.app.locals;
  try {
    const result = await db.query(
      `SELECT id, display_name, avatar_url, total_matches, total_wins, total_km_walked, created_at
       FROM users WHERE id = $1`, [req.params.id]
    );
    if (!result.rows.length) return res.status(404).json({ error: 'Player not found' });
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: 'Failed to get player' });
  }
});

module.exports = router;
