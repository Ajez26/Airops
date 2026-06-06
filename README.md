# AIROPS 🎯

**Tactical field system for airsoft & paintball** — real-time GPS tracking, team coordination, and mission management with a military HUD aesthetic.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Android | Kotlin + Jetpack Compose + Hilt + Retrofit |
| Backend | Node.js + Express + PostgreSQL + WebSocket |
| Web | React + Tailwind CSS + Firebase Auth |
| Infra | Docker Compose |

## Color Palette (HUD)

| Color | Hex | Usage |
|-------|-----|-------|
| Green | `#00FF41` | Primary actions, live status |
| Amber | `#FFB300` | Warnings, scores, codes |
| Red | `#FF4444` | Kills, deaths, danger |
| Background | `#0A0E0F` | Base dark |
| Surface | `#111518` | Cards/panels |

---

## Game Modes

- `team_deathmatch` — Most kills wins
- `domination` — Control objectives
- `free_for_all` — Every operator for themselves
- `capture_the_flag` — Capture enemy flag
- `vip` — Escort/eliminate the VIP
- `assault` — Attackers vs defenders

---

## Quick Start (Docker)

```bash
cp backend/.env.example backend/.env
# Edit backend/.env with your Firebase service account

docker-compose up --build
```

- Backend: http://localhost:3000
- Web: http://localhost:3001
- Postgres: localhost:5432

---

## Android Setup

1. Install Android Studio Arctic Fox+
2. Open `android/` folder
3. Create `android/local.properties`:
   ```
   sdk.dir=/path/to/Android/Sdk
   GOOGLE_MAPS_API_KEY=your_key
   ```
4. Add `google-services.json` to `android/app/`
5. Build & run on API 26+ device or emulator

---

## Architecture

```
Airops/
├── android/          # Kotlin/Compose Android app
│   └── app/src/main/java/com/airops/
│       ├── data/     # Repositories + Retrofit
│       ├── domain/   # GameState, Match, User models
│       ├── services/ # LocationTrackingService (GPS foreground)
│       ├── network/  # WifiAwareManager (offline NAN)
│       └── ui/       # Screens + ViewModels + Theme
├── backend/          # Node.js API + WebSocket
│   └── src/
│       ├── db/       # Schema, migrations, pool
│       ├── middleware/  # Firebase token verification
│       ├── routes/   # auth, matches, players, stats
│       └── services/ # WebSocket game events
├── web/              # React operator dashboard
│   └── src/
│       ├── components/ # Layout, TacticalMap
│       ├── hooks/    # useAuth (Firebase), useWebSocket
│       ├── pages/    # Dashboard, NewMatch, MatchControl, Stats
│       └── utils/    # api.js (axios)
└── docker-compose.yml
```

---

## API Reference

### Auth
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Firebase token → user session |

### Matches
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/matches` | Create match |
| POST | `/api/matches/join` | Join with 6-char code |
| GET | `/api/matches/:id` | Match state |
| POST | `/api/matches/:id/start` | Start match |
| POST | `/api/matches/:id/end` | End match + aggregate stats |
| POST | `/api/matches/:id/kill` | Report kill |
| PATCH | `/api/matches/:id/player` | Update location/status |

### Players
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/players/me` | My profile |
| GET | `/api/players/:id` | Public profile |

### Stats
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/stats/me` | Personal stats + achievements |
| GET | `/api/stats/leaderboard` | Global top 50 |
| GET | `/api/stats/match/:id` | Match stats |

---

## WebSocket Events

Connect to `ws://host:3000` with header `Authorization: Bearer <token>`.

| Event | Direction | Payload |
|-------|-----------|---------|
| `location_update` | Client→Server | `{matchId, lat, lng}` |
| `player_update` | Server→Client | `{playerId, lat, lng, status}` |
| `match_state` | Server→Client | Full `GameState` snapshot |
| `kill_feed` | Server→Client | `{killer, victim, weapon}` |

---

*Built with OpenClaw AI agent.*
