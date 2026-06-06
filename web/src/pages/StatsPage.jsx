import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import Layout from '../components/Layout';
import { getMyStats, getLeaderboard } from '../utils/api';

const HudTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-hud-surface border border-hud-border p-2 text-xs font-mono">
      <div className="text-hud-text tracking-widest">{label}</div>
      {payload.map((p, i) => (
        <div key={i} style={{ color: p.color }}>{p.name}: {p.value}</div>
      ))}
    </div>
  );
};

export default function StatsPage() {
  const [stats, setStats] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getMyStats(), getLeaderboard()])
      .then(([s, l]) => {
        setStats(s.data);
        setLeaderboard(l.data || []);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const chartData = [
    { name: 'KILLS', value: stats?.total_kills || 0 },
    { name: 'DEATHS', value: stats?.total_deaths || 0 },
    { name: 'ASSISTS', value: stats?.total_assists || 0 },
    { name: 'OBJECTIVES', value: stats?.objectives_captured || 0 },
    { name: 'MVPs', value: stats?.mvp_count || 0 },
  ];

  return (
    <Layout>
      <div className="mb-6">
        <div className="text-xs text-hud-text tracking-widest mb-1">COMMAND CENTER</div>
        <h1 className="text-2xl font-display font-bold tracking-wider text-hud-green hud-glow-green">
          STATISTICS
        </h1>
      </div>

      {/* Personal stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: 'TOTAL MATCHES', value: stats?.total_matches, glow: 'green' },
          { label: 'WIN RATE', value: stats?.win_rate ? `${stats.win_rate}%` : '—', glow: 'amber' },
          { label: 'K/D RATIO', value: stats?.kd_ratio?.toFixed(2) || '—', glow: 'amber' },
          { label: 'ACCURACY', value: stats?.accuracy ? `${stats.accuracy}%` : '—', glow: 'green' },
        ].map(s => (
          <div key={s.label} className="hud-panel p-4">
            <div className="text-xs text-hud-text tracking-widest mb-2">{s.label}</div>
            <div className={`text-2xl font-display font-bold tracking-wider ${
              s.glow === 'green' ? 'text-hud-green hud-glow-green' : 'text-hud-amber hud-glow-amber'
            }`}>
              {loading ? '—' : (s.value ?? '0')}
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        {/* Bar chart */}
        <div className="hud-panel p-6">
          <div className="text-xs text-hud-text tracking-widest mb-4">PERSONAL COMBAT STATS</div>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={chartData} margin={{ top: 0, right: 0, left: -20, bottom: 0 }}>
              <XAxis dataKey="name" tick={{ fill: '#8FA9AF', fontSize: 10, fontFamily: 'monospace' }} />
              <YAxis tick={{ fill: '#8FA9AF', fontSize: 10, fontFamily: 'monospace' }} />
              <Tooltip content={<HudTooltip />} />
              <Bar dataKey="value" fill="#00FF41" fillOpacity={0.7} radius={[2, 2, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Achievements */}
        <div className="hud-panel p-6">
          <div className="text-xs text-hud-text tracking-widest mb-4">ACHIEVEMENTS</div>
          {stats?.achievements?.length ? (
            <div className="space-y-2">
              {stats.achievements.map((a, i) => (
                <div key={i} className="flex items-center gap-3 py-2 border-b border-hud-border/30">
                  <span className="text-lg">{a.icon || '★'}</span>
                  <div>
                    <div className="text-xs text-hud-green tracking-wider">{a.name}</div>
                    <div className="text-xs text-hud-text/50">{a.description}</div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center text-xs text-hud-text/30 tracking-widest py-8">
              NO ACHIEVEMENTS YET<br/>
              <span className="text-hud-text/20">PLAY MORE MATCHES</span>
            </div>
          )}
        </div>
      </div>

      {/* Full leaderboard */}
      <div className="hud-panel p-6">
        <div className="text-xs text-hud-text tracking-widest mb-4">GLOBAL LEADERBOARD — TOP 50</div>
        <div className="overflow-x-auto">
          <table className="w-full text-xs font-mono">
            <thead>
              <tr className="text-hud-text/50 tracking-widest border-b border-hud-border">
                <th className="text-left py-2 w-8">#</th>
                <th className="text-left py-2">OPERATOR</th>
                <th className="text-right py-2">MATCHES</th>
                <th className="text-right py-2">WINS</th>
                <th className="text-right py-2">KILLS</th>
                <th className="text-right py-2">DEATHS</th>
                <th className="text-right py-2">K/D</th>
              </tr>
            </thead>
            <tbody>
              {leaderboard.map((p, i) => (
                <tr key={p.user_id} className={`border-b border-hud-border/20 hover:bg-hud-green/5 transition-colors ${
                  i === 0 ? 'text-hud-amber' : i < 3 ? 'text-hud-green' : 'text-hud-text'
                }`}>
                  <td className="py-2.5 font-bold">{i + 1}</td>
                  <td className="py-2.5 tracking-wide">{p.display_name || 'UNKNOWN'}</td>
                  <td className="py-2.5 text-right">{p.total_matches || 0}</td>
                  <td className="py-2.5 text-right">{p.wins || 0}</td>
                  <td className="py-2.5 text-right">{p.total_kills || 0}</td>
                  <td className="py-2.5 text-right">{p.total_deaths || 0}</td>
                  <td className="py-2.5 text-right">
                    {((p.total_kills || 0) / Math.max(p.total_deaths || 1, 1)).toFixed(2)}
                  </td>
                </tr>
              ))}
              {leaderboard.length === 0 && (
                <tr>
                  <td colSpan={7} className="text-center py-8 text-hud-text/30 tracking-widest">
                    NO DATA AVAILABLE
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </Layout>
  );
}
