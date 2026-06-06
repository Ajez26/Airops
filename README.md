# Airops 🎯

**Tactical Airsoft & Paintball App** — HUD militar futurista para partidas organizadas.

> App táctica para airsoft y paintball con tracking GPS en tiempo real, conectividad offline via Wi-Fi Aware (NAN), y modos de juego competitivos.

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Android (MVP) | Kotlin nativo |
| iOS (futuro) | Swift / React Native |
| Backend | Node.js + PostgreSQL |
| Auth | Firebase Auth (Google/Apple) |
| Mapas | Mapbox (offline + capas custom) |
| Tiempo real | WebSockets / Supabase Realtime |
| Conectividad offline | Wi-Fi Aware API (Android) |
| Panel web | React + Tailwind |

---

## Fases

- **Fase 1 — MVP Android**: Conectividad NAN + tracking básico + mapa + vivo/muerto
- **Fase 2 — Partida completa**: Modos de juego + chat + órdenes + objetivos
- **Fase 3 — Plataforma**: Cuentas + estadísticas + logros + panel web
- **Fase 4 — Expansión**: iOS + modelo de suscripción + tiers

---

## Estructura del proyecto

```
airops/
├── android/          # App Android (Kotlin) — MVP
├── backend/          # Node.js + PostgreSQL API
├── web/              # Panel web organizadores (React + Tailwind)
├── docs/             # Documentación técnica y requerimientos
└── shared/           # Tipos y contratos compartidos
```

---

## Modos de juego

- Team Deathmatch
- Dominación
- Battle Royale
- Modos personalizados

## Conectividad

- **Wi-Fi Aware (NAN)** como protocolo principal — sin internet, sin hardware extra
- Fallback a hotspot local si el dispositivo no soporta NAN

---

## Roles

- **Organizador**: crea partidas, gestiona equipos, ve mapa en tiempo real, panel web
- **Jugador**: se une con código, tracking GPS, chat, órdenes jerárquicas

---

## Desarrollo

Ver `docs/REQUIREMENTS.md` para el documento completo de requerimientos.
