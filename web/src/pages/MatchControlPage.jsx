import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import TacticalMap from '../components/TacticalMap';
import { useWebSocket } from '../hooks/useWebSocket';
import { getMatch, startMatch, endMatch } from '../utils/api';
import { useAuth } from '../hooks/useAuth';

function EventFeed({ events }) {
  const typeStyles = {
    player_status: 'text-hud-red',
    event: 'text-hud-amber',
    chat_broadcast: 'text-hud-text',
  };

  return (
    <div className="space-y-1 overflow-y-auto max-h-48">
      {events.length === 0 && (
        <div className="text-xs text-hud-text/30 text-center py-4 tracking-widest">AWAITING EVENTS...</div>
      )}
      {events.map((e, i) => (
        <div key={i} className={`text-xs font-mono ${typeStyles[e.type] || 'text-hud-text'}`}>
          <span className="text-hud-text/30 mr-2">
            {new Date(e.timestamp).toISOString().slice(11, 19)}
          </span>
          {e.type === 'player_status' && `PLAYER ${e.playerId?.slice(0,6)} → ${e.status?.toUpperCase()}`}
          {e.type === 'event' && `EVENT: ${e.event?.toUpperCase().replace('_', ' ')}`}
          {e.type === 'chat_broadcast' && `[CHAT] ${e.message}`}
        </div>
      ))}
    </div>
  );
}

function PlayerRow({ player, teamName, teamColor }) {
  return (
    <div className="flex items-center gap-3 py-2 border-b border-hud-border/30">
      <div
        className="w-2.5 h-2.5 rounded-full flex-shrink-0"
        style={{ background: teamColor, boxShadow: `0 0 4px ${teamColor}` }}
      />
      <div className="flex-1 text-xs font-mono text-hud-text truncate">
        {player.display_name || 'UNKNOWN'}
      </div>
      <div className="text-xs text-hud-text/40">{teamName || '—'}</div>
      <div className={`text-xs tracking-widest ${player.status === 'dead' ? 'text-hud-red' : 'text-hud-green'}`}>
        {player.status === 'dead' ? '☠' : '◉'}
      </div>
    </div>
  );
}

export default function MatchControlPage() {
  const { id } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();
  const [match, setMatch] = useState(null);
  const [teams, setTeams] = useState([]);
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [elapsed, setElapsed] = useState(0);

  const { connected, players: wsPlayers, events, chat, sendCommand } = useWebSocket(id, token);

  useEffect(() => {
    getMatch(id)
      .then(res => {
        setMatch(res.data);
        setTeams(res.data.teams || []);
        setPlayers(res.data.players || []);
      })
      .catch(() => navigate('/'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  // Timer
  useEffect(() => {
    if (match?.status !== 'active') return;
    const start = match.started_at ? new Date(match.started_at).getTime() : Date.now();
    const interval = setInterval(() => {
      setElapsed(Math.floor((Date.now() - start) / 1000));
    }, 1000);
    return () => clearInterval(interval);
  }, [match]);

  const formatTime = (s) => {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
  };

  const handleStart = async () => {
    try {
      await startMatch(id);
      setMatch(m => ({ ...m, status: 'active', started_at: new Date().toISOString() }));
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to start');
    }
  };

  const handleEnd = async () => {
    if (!window.confirm('End match?')) return;
    try {
      await endMatch(id);
      setMatch(m => ({ ...m, status: 'finished' }));
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to end');
    }
  };

  const teamColorMap = {};
  teams.forEach(t => { teamColorMap[t.id] = t.color; });

  if (loading) return <Layout><div className="text-center text-hud-text tracking-widest">LOADING...</div></Layout>;

  const aliveCount = Object.values(wsPlayers).filter(p => p.status !== 'dead').length || players.filter(p => p.status !== 'dead').length;
  const teamAlpha = teams.find(t => t.name === 'Alpha');
  const teamBravo = teams.find(t => t.name === 'Bravo');

  return (
    <Layout>
      {/* Match header */}
      <div className="flex flex-wrap items-start justify-between gap-4 mb-6">
        <div>
          <div className="text-xs text-hud-text tracking-widest mb-1">ACTIVE MISSION</div>
          <h1 className="text-2xl font-display font-bold tracking-wider text-hud-green hud-glow-green">
            {match?.name?.toUpperCase() || 'UNKNOWN'}
          </h1>
          <div className="flex items-center gap-3 mt-2">
            <span className="text-xs text-hud-text/60 tracking-widest">
              MODE: {match?.game_mode?.replace('_', ' ').toUpperCase()}
            </span>
            <span className="text-xs text-hud-text/60 tracking-widest">
              CODE: <span className="text-hud-amber">{match?.code}</span>
            </span>
            <div className="flex items-center gap-1.5">
              <div className={`status-dot ${connected ? 'active' : 'offline'}`} />
              <span className="text-xs text-hud-text/40 tracking-widest">
                {connected ? 'LIVE' : 'CONNECTING...'}
              </span>
            </div>
          </div>
        </div>

        {/* Timer + controls */}
        <div className="flex items-center gap-3">
          {match?.status === 'active' && (
            <div className="text-3xl font-display font-bold text-hud-amber hud-glow-amber tracking-widest">
              {formatTime(elapsed)}
            </div>
          )}

          {match?.status === 'lobby' && (
            <button
              onClick={handleStart}
              className="px-6 py-2.5 border border-hud-green bg-hud-green/10 hover:bg-hud-green/20
                         text-hud-green text-xs font-mono tracking-widest transition-all"
            >
              ▶ START MATCH
            </button>
          )}

          {match?.status === 'active' && (
            <button
              onClick={handleEnd}
              className="px-6 py-2.5 border border-hud-red bg-hud-red/10 hover:bg-hud-red/20
                         text-hud-red text-xs font-mono tracking-widest transition-all"
            >
              ■ END MATCH
            </button>
          )}
        </div>
      </div>

      {/* Lobby info */}
      {match?.status === 'lobby' && (
        <div className="hud-panel p-4 mb-6 text-center">
          <div className="text-xs text-hud-text tracking-widest mb-2">WAITING FOR PLAYERS</div>
          <div className="text-4xl font-display font-bold text-hud-amber hud-glow-amber tracking-[0.3em]">
            {match.code}
          </div>
          <div className="text-xs text-hud-text/50 mt-2">Share this code to join</div>
        </div>
      )}

      {/* Main grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Map — 2/3 */}
        <div className="lg:col-span-2">
          <div className="hud-panel overflow-hidden" style={{ height: '460px' }}>
            <TacticalMap
              players={wsPlayers}
              teams={teams}
              objectives={[]}
            />
          </div>
        </div>

        {/* Sidebar — 1/3 */}
        <div className="space-y-4">
          {/* Score */}
          <div className="hud-panel p-4">
            <div className="text-xs text-hud-text tracking-widest mb-3">SCORE</div>
            <div className="grid grid-cols-2 gap-3">
              <div className="text-center">
                <div className="text-xs mb-1 tracking-widest" style={{ color: teamAlpha?.color || '#FF4444' }}>ALPHA</div>
                <div className="text-3xl font-display font-bold" style={{ color: teamAlpha?.color || '#FF4444' }}>
                  {match?.scores?.alpha || 0}
                </div>
              </div>
              <div className="text-center">
                <div className="text-xs mb-1 tracking-widest" style={{ color: teamBravo?.color || '#4444FF' }}>BRAVO</div>
                <div className="text-3xl font-display font-bold" style={{ color: teamBravo?.color || '#4444FF' }}>
                  {match?.scores?.bravo || 0}
                </div>
              </div>
            </div>
          </div>

          {/* Players */}
          <div className="hud-panel p-4">
            <div className="flex items-center justify-between mb-3">
              <div className="text-xs text-hud-text tracking-widest">OPERATORS</div>
              <div className="text-xs text-hud-green">
                {aliveCount}/{players.length} ALIVE
              </div>
            </div>
            <div className="max-h-40 overflow-y-auto">
              {players.map(p => (
                <PlayerRow
                  key={p.id}
                  player={{ ...p, ...wsPlayers[p.user_id] }}
                  teamName={teams.find(t => t.id === p.team_id)?.name}
                  teamColor={teamColorMap[p.team_id] || '#888'}
                />
              ))}
              {players.length === 0 && (
                <div className="text-xs text-hud-text/30 text-center py-3 tracking-widest">NO PLAYERS YET</div>
              )}
            </div>
          </div>

          {/* Event feed */}
          <div className="hud-panel p-4">
            <div className="text-xs text-hud-text tracking-widest mb-3">EVENT FEED</div>
            <EventFeed events={events} />
          </div>
        </div>
      </div>
    </Layout>
  );
}
