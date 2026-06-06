import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../components/Layout';
import { getMyStats, getLeaderboard } from '../utils/api';

export default function DashboardPage() {
  const [stats, setStats] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getMyStats(), getLeaderboard()])
      .then(([s, l]) => {
        setStats(s.data);
        setLeaderboard(l.data?.slice(0, 10) || []);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const StatCard = ({ label, value, unit = '', glow = 'green' }) => (
    <div className="hud-panel p-4">
      <div className="text-xs text-hud-text tracking-widest mb-2">{label}</div>
      <div className={`text-2xl font-display font-bold tracking-wider ${
        glow === 'green' ? 'text-hud-green hud-glow-green' :
        glow === 'amber' ? 'text-hud-amber hud-glow-amber' :
        'text-hud-red hud-glow-red'
      }`}>
        {loading ? '—' : (value ?? '0')}{unit}
      </div>
    </div>
  );

  return (
    <Layout>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <div className="text-xs text-hud-text tracking-widest mb-1">COMMAND CENTER</div>
          <h1 className="text-2xl font-display font-bold tracking-wider text-hud-green hud-glow-green">
            DASHBOARD
          </h1>
        </div>
        <Link
          to="/match/new"
          className="px-6 py-2.5 border border-hud-green bg-hud-green/10 hover:bg-hud-green/20
                     text-hud-green text-xs font-mono tracking-widest transition-all duration-200"
        >
          ⊕ NEW MATCH
        </Link>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <StatCard label="TOTAL MATCHES" value={stats?.total_matches} />
        <StatCard label="WINS" value={stats?.wins} glow="amber" />
        <StatCard label="K/D RATIO" value={stats?.kd_ratio?.toFixed(2)} glow="amber" />
        <StatCard label="ACCURACY" value={stats?.accuracy} unit="%" glow="green" />
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <StatCard label="TOTAL KILLS" value={stats?.total_kills} glow="red" />
        <StatCard label="TOTAL DEATHS" value={stats?.total_deaths} glow="red" />
        <StatCard label="MVPS" value={stats?.mvp_count} glow="amber" />
        <StatCard label="OBJECTIVES" value={stats?.objectives_captured} glow="green" />
      </div>

      {/* Leaderboard */}
      <div className="hud-panel p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="text-xs text-hud-text tracking-widest">GLOBAL LEADERBOARD</div>
          <Link to="/stats" className="text-xs text-hud-green/60 hover:text-hud-green tracking-widest">
            VIEW ALL →
          </Link>
        </div>

        <div className="space-y-1">
          {/* Header */}
          <div className="grid grid-cols-12 text-xs text-hud-text/50 tracking-widest py-2 border-b border-hud-border">
            <div className="col-span-1">#</div>
            <div className="col-span-5">OPERATOR</div>
            <div className="col-span-2 text-right">KILLS</div>
            <div className="col-span-2 text-right">K/D</div>
            <div className="col-span-2 text-right">WINS</div>
          </div>

          {leaderboard.length === 0 && !loading && (
            <div className="py-8 text-center text-hud-text/30 text-xs tracking-widest">
              NO DATA — BE THE FIRST TO PLAY
            </div>
          )}

          {leaderboard.map((player, i) => (
            <div
              key={player.user_id}
              className={`grid grid-cols-12 py-2.5 text-xs transition-colors hover:bg-hud-green/5 ${
                i === 0 ? 'text-hud-amber' : i === 1 ? 'text-hud-green/70' : 'text-hud-text'
              }`}
            >
              <div className="col-span-1 font-bold">{i + 1}</div>
              <div className="col-span-5 tracking-wide">{player.display_name || 'UNKNOWN'}</div>
              <div className="col-span-2 text-right">{player.total_kills || 0}</div>
              <div className="col-span-2 text-right">{((player.total_kills || 0) / Math.max(player.total_deaths || 1, 1)).toFixed(2)}</div>
              <div className="col-span-2 text-right">{player.wins || 0}</div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}
