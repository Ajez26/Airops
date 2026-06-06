#!/usr/bin/env node
/**
 * Database migration script
 * Usage: node src/db/migrate.js
 */

require('dotenv').config();
const fs = require('fs');
const path = require('path');
const pool = require('./pool');

async function migrate() {
  console.log('🔧 Running migrations...');
  
  try {
    const schemaSQL = fs.readFileSync(path.join(__dirname, 'schema.sql'), 'utf8');
    await pool.query(schemaSQL);
    console.log('✅ Schema applied successfully');
    
    // Migrations table for tracking
    await pool.query(`
      CREATE TABLE IF NOT EXISTS _migrations (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL,
        applied_at TIMESTAMPTZ DEFAULT NOW()
      )
    `);
    
    // Idempotent column additions for v2 schema upgrade
    const alterations = [
      `ALTER TABLE users ADD COLUMN IF NOT EXISTS wins INTEGER DEFAULT 0`,
      `ALTER TABLE users ADD COLUMN IF NOT EXISTS total_kills INTEGER DEFAULT 0`,
      `ALTER TABLE users ADD COLUMN IF NOT EXISTS total_deaths INTEGER DEFAULT 0`,
    ];
    for (const sql of alterations) {
      await pool.query(sql);
    }
    
    // Update game_mode constraint to allow all modes
    try {
      await pool.query(`ALTER TABLE matches DROP CONSTRAINT IF EXISTS matches_game_mode_check`);
      await pool.query(`ALTER TABLE matches ADD CONSTRAINT matches_game_mode_check
        CHECK (game_mode IN ('team_deathmatch','domination','free_for_all','capture_the_flag','vip','assault','battle_royale','custom'))`);
    } catch (e) { /* constraint update not critical */ }

    console.log('✅ Migrations complete');
  } catch (err) {
    console.error('❌ Migration failed:', err.message);
    process.exit(1);
  } finally {
    await pool.end();
  }
}

migrate();
