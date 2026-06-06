const express = require('express');
const router = express.Router();
const { v4: uuidv4 } = require('uuid');
const admin = require('firebase-admin');

// POST /api/auth/register — Register or login after Firebase auth
router.post('/register', async (req, res) => {
  const { firebase_token, display_name, avatar_url } = req.body;
  const { db } = req.app.locals;

  try {
    const decoded = await admin.auth().verifyIdToken(firebase_token);

    // Upsert user
    const result = await db.query(
      `INSERT INTO users (id, firebase_uid, email, display_name, avatar_url)
       VALUES ($1, $2, $3, $4, $5)
       ON CONFLICT (firebase_uid) DO UPDATE SET display_name = EXCLUDED.display_name, avatar_url = EXCLUDED.avatar_url
       RETURNING *`,
      [uuidv4(), decoded.uid, decoded.email, display_name || decoded.name, avatar_url || decoded.picture]
    );

    res.json({ user: result.rows[0] });
  } catch (err) {
    console.error(err);
    res.status(401).json({ error: 'Invalid Firebase token' });
  }
});

module.exports = router;
