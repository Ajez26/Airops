# 📋 Documento de Requerimientos — Tactical Airsoft App

## Concepto general

App táctica para airsoft y paintball, estilo HUD militar futurista, enfocada en partidas organizadas donde un administrador gestiona la experiencia completa.

---

## Conectividad

- **Wi-Fi Aware (NAN)** como protocolo principal — sin internet, sin hardware extra
- Fallback a hotspot local si el dispositivo no soporta NAN

---

## Usuarios y acceso

- Login con Google/Apple
- Roles: **Organizador** y **Jugador**
- Perfil público por jugador

---

## Partidas

- El organizador crea, los jugadores se unen con código
- Equipos dinámicos según el modo de juego
- Estado de jugadores: vivo / muerto

---

## Modos de juego

- Team Deathmatch
- Dominación
- Battle Royale
- Modos personalizados (configurables por el organizador)

---

## Durante la partida

- Tracking GPS en tiempo real en mapa
- Mapa base (OpenStreetMap) + capas personalizadas por el organizador
- Chat de texto
- Órdenes jerárquicas (comandante → soldado)
- Objetivos en tiempo real

---

## Perfil y progresión

- Historial de partidas
- Estadísticas personales (km recorridos, partidas ganadas, etc.)
- Sistema de logros y badges

---

## Panel web (organizadores)

- Crear y gestionar partidas
- Ver mapa en tiempo real
- Estadísticas de partida
- Gestión de equipos y jugadores

---

## Modelo de negocio

- Suscripción mensual/anual
- Tiers por cantidad de jugadores por partida

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Android (MVP) | Kotlin nativo |
| iOS (futuro) | Swift nativo o React Native |
| Backend | Node.js + PostgreSQL |
| Auth | Firebase Auth (Google/Apple) |
| Mapas | Mapbox (offline + capas custom) |
| Tiempo real | WebSockets / Supabase Realtime |
| Conectividad offline | Wi-Fi Aware API (Android) |
| Panel web | React + Tailwind |

---

## Fases de desarrollo

### Fase 1 — MVP Android
Conectividad NAN + tracking básico + mapa + vivo/muerto

### Fase 2 — Partida completa
Modos de juego + chat + órdenes + objetivos

### Fase 3 — Plataforma
Cuentas + estadísticas + logros + panel web

### Fase 4 — Expansión
iOS + modelo de suscripción + tiers
