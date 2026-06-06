#!/usr/bin/env node
/**
 * Database seed — creates demo data for development
 * Usage: node src/db/seed.js
 */

require('dotenv').config();
const { v4: uuidv4 } = require('uuid');
const pool = require('./pool');

async function seed() {
  console.log('🌱 Seeding database...');

  try {
    // Demo organizer
    const organizerId = uuidv4();
    await pool.query(`
      INSERT INTO users (id, firebase_uid, display_name, email, role)
      VALUES ($1, 'demo-organizer-uid', 'Demo Organizer', 'organizer@airops.demo', 'organizer')
      ON CONFLICT (firebase_uid) DO NOTHING
    `, [organizerId]);

    // Demo players
    const playerIds = [uuidv4(), uuidv4(), uuidv4(), uuidv4()];
    const playerNames = ['Alpha-1', 'Alpha-2', 'Bravo-1', 'Bravo-2'];
    
    for (let i = 0; i < playerIds.length; i++) {
      await pool.query(`
        INSERT INTO users (id, firebase_uid, display_name, email, role)
        VALUES ($1, $2, $3, $4, 'player')
        ON CONFLICT (firebase_uid) DO NOTHING
      `, [playerIds[i], `demo-player-uid-${i}`, playerNames[i], `player${i}@airops.demo`]);
    }

    // Demo match
    const matchId = uuidv4();
    await pool.query(`
      INSERT INTO matches (id, code, name, organizer_id, game_mode, max_players, status, game_config)
      VALUES ($1, 'DEMO01', 'Demo Match', $2, 'team_deathmatch', 20, 'lobby', $3)
      ON CONFLICT DO NOTHING
    `, [matchId, organizerId, JSON.stringify({
      duration_minutes: 30,
      respawn_enabled: true,
      respawn_seconds: 30,
      friendly_fire: false
    })]);

    // Teams
    const teamAlphaId = uuidv4();
    const teamBravoId = uuidv4();
    await pool.query(`
      INSERT INTO teams (id, match_id, name, color) VALUES
      ($1, $2, 'Alpha', '#FF4444'),
      ($3, $4, 'Bravo', '#4444FF')
      ON CONFLICT DO NOTHING
    `, [teamAlphaId, matchId, teamBravoId, matchId]);

    console.log('✅ Seed complete');
    console.log(`   Match code: DEMO01`);
    console.log(`   Organizer: organizer@airops.demo`);
  } catch (err) {
    console.error('❌ Seed failed:', err.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

seed();
