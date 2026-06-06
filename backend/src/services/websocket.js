/**
 * WebSocket service — handles real-time GPS tracking and game events
 * Message types:
 *   CLIENT → SERVER: location_update, player_eliminated, chat_message, objective_capture, order
 *   SERVER → CLIENT: game_state, location_broadcast, player_status, chat_broadcast, event
 */

const clients = new Map(); // matchId → Set of ws connections

function setupWebSocket(wss) {
  wss.on('connection', (ws, req) => {
    let matchId = null;
    let playerId = null;

    ws.on('message', (data) => {
      try {
        const msg = JSON.parse(data);

        switch (msg.type) {
          case 'join_match':
            matchId = msg.matchId;
            playerId = msg.playerId;
            if (!clients.has(matchId)) clients.set(matchId, new Set());
            clients.get(matchId).add(ws);
            ws.send(JSON.stringify({ type: 'joined', matchId }));
            console.log(`Player ${playerId} joined match ${matchId}`);
            break;

          case 'location_update':
            // Broadcast player location to all in same match
            broadcast(matchId, {
              type: 'location_broadcast',
              playerId: msg.playerId,
              teamId: msg.teamId,
              lat: msg.lat,
              lng: msg.lng,
              timestamp: Date.now()
            }, ws);
            break;

          case 'player_eliminated':
            broadcast(matchId, {
              type: 'player_status',
              playerId: msg.playerId,
              status: 'dead',
              killedBy: msg.killedBy,
              timestamp: Date.now()
            });
            break;

          case 'chat_message':
            broadcast(matchId, {
              type: 'chat_broadcast',
              playerId: msg.playerId,
              teamId: msg.teamId,
              message: msg.message,
              scope: msg.scope, // 'team' | 'all'
              timestamp: Date.now()
            });
            break;

          case 'order':
            // Commander → soldiers (team only)
            broadcast(matchId, {
              type: 'order',
              fromPlayerId: msg.playerId,
              teamId: msg.teamId,
              orderType: msg.orderType,
              payload: msg.payload,
              timestamp: Date.now()
            });
            break;

          case 'objective_capture':
            broadcast(matchId, {
              type: 'event',
              event: 'objective_captured',
              objectiveId: msg.objectiveId,
              teamId: msg.teamId,
              timestamp: Date.now()
            });
            break;
        }
      } catch (e) {
        console.error('WS parse error:', e.message);
      }
    });

    ws.on('close', () => {
      if (matchId && clients.has(matchId)) {
        clients.get(matchId).delete(ws);
        if (clients.get(matchId).size === 0) clients.delete(matchId);
      }
    });
  });
}

function broadcast(matchId, message, exclude = null) {
  if (!clients.has(matchId)) return;
  const payload = JSON.stringify(message);
  clients.get(matchId).forEach((client) => {
    if (client !== exclude && client.readyState === 1) {
      client.send(payload);
    }
  });
}

module.exports = { setupWebSocket, broadcast };
