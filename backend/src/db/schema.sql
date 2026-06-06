-- Airops Database Schema
-- PostgreSQL

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  firebase_uid VARCHAR(128) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  avatar_url TEXT,
  role VARCHAR(20) NOT NULL DEFAULT 'player' CHECK (role IN ('player', 'organizer', 'admin')),
  total_matches INTEGER DEFAULT 0,
  total_wins INTEGER DEFAULT 0,
  total_km_walked DECIMAL(10,2) DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Matches
CREATE TABLE IF NOT EXISTS matches (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  code VARCHAR(8) UNIQUE NOT NULL,
  name VARCHAR(100) NOT NULL,
  organizer_id UUID REFERENCES users(id),
  game_mode VARCHAR(30) NOT NULL CHECK (game_mode IN ('team_deathmatch', 'domination', 'battle_royale', 'custom')),
  status VARCHAR(20) NOT NULL DEFAULT 'lobby' CHECK (status IN ('lobby', 'active', 'paused', 'finished')),
  max_players INTEGER NOT NULL DEFAULT 20,
  custom_map_layer JSONB,
  game_config JSONB,
  started_at TIMESTAMP,
  finished_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Teams
CREATE TABLE IF NOT EXISTS teams (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  match_id UUID REFERENCES matches(id) ON DELETE CASCADE,
  name VARCHAR(50) NOT NULL,
  color VARCHAR(7) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Match Players (join table)
CREATE TABLE IF NOT EXISTS match_players (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  match_id UUID REFERENCES matches(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id),
  team_id UUID REFERENCES teams(id),
  role VARCHAR(20) DEFAULT 'soldier' CHECK (role IN ('commander', 'soldier')),
  status VARCHAR(20) DEFAULT 'alive' CHECK (status IN ('alive', 'dead', 'spectating')),
  kills INTEGER DEFAULT 0,
  deaths INTEGER DEFAULT 0,
  lat DECIMAL(10,8),
  lng DECIMAL(11,8),
  last_location_at TIMESTAMP,
  joined_at TIMESTAMP DEFAULT NOW(),
  UNIQUE (match_id, user_id)
);

-- Objectives (for domination / custom modes)
CREATE TABLE IF NOT EXISTS objectives (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  match_id UUID REFERENCES matches(id) ON DELETE CASCADE,
  name VARCHAR(50) NOT NULL,
  lat DECIMAL(10,8) NOT NULL,
  lng DECIMAL(11,8) NOT NULL,
  controlled_by UUID REFERENCES teams(id),
  captured_at TIMESTAMP
);

-- Match Events log
CREATE TABLE IF NOT EXISTS match_events (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  match_id UUID REFERENCES matches(id) ON DELETE CASCADE,
  event_type VARCHAR(50) NOT NULL,
  payload JSONB,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Achievements
CREATE TABLE IF NOT EXISTS achievements (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  key VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  icon_url TEXT
);

CREATE TABLE IF NOT EXISTS user_achievements (
  user_id UUID REFERENCES users(id),
  achievement_id UUID REFERENCES achievements(id),
  earned_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (user_id, achievement_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_match_players_match ON match_players(match_id);
CREATE INDEX IF NOT EXISTS idx_match_players_user ON match_players(user_id);
CREATE INDEX IF NOT EXISTS idx_matches_code ON matches(code);
CREATE INDEX IF NOT EXISTS idx_matches_status ON matches(status);
