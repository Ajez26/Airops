import { useEffect, useRef, useCallback, useState } from 'react';

export function useWebSocket(matchId, token) {
  const ws = useRef(null);
  const [connected, setConnected] = useState(false);
  const [players, setPlayers] = useState({});
  const [events, setEvents] = useState([]);
  const [chat, setChat] = useState([]);

  const connect = useCallback(() => {
    if (!matchId || !token) return;
    
    const wsUrl = process.env.REACT_APP_WS_URL || 'ws://localhost:3000';
    ws.current = new WebSocket(`${wsUrl}?token=${token}`);

    ws.current.onopen = () => {
      setConnected(true);
      ws.current.send(JSON.stringify({ type: 'join_match', matchId }));
    };

    ws.current.onmessage = (e) => {
      try {
        const msg = JSON.parse(e.data);
        
        switch (msg.type) {
          case 'location_broadcast':
            setPlayers(prev => ({
              ...prev,
              [msg.playerId]: { ...prev[msg.playerId], lat: msg.lat, lng: msg.lng, teamId: msg.teamId, lastSeen: msg.timestamp }
            }));
            break;
          
          case 'player_status':
            setPlayers(prev => ({
              ...prev,
              [msg.playerId]: { ...prev[msg.playerId], status: msg.status }
            }));
            setEvents(prev => [{ type: 'eliminate', ...msg }, ...prev].slice(0, 50));
            break;

          case 'event':
            setEvents(prev => [msg, ...prev].slice(0, 50));
            break;

          case 'chat_broadcast':
            setChat(prev => [...prev, msg].slice(-100));
            break;
        }
      } catch (_) {}
    };

    ws.current.onclose = () => {
      setConnected(false);
      // Reconnect after 2s
      setTimeout(connect, 2000);
    };

    ws.current.onerror = () => {
      ws.current?.close();
    };
  }, [matchId, token]);

  useEffect(() => {
    connect();
    return () => ws.current?.close();
  }, [connect]);

  const sendCommand = useCallback((type, payload) => {
    if (ws.current?.readyState === WebSocket.OPEN) {
      ws.current.send(JSON.stringify({ type, ...payload }));
    }
  }, []);

  return { connected, players, events, chat, sendCommand };
}
