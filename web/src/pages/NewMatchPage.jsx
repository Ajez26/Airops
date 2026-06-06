import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import { createMatch } from '../utils/api';

const GAME_MODES = [
  { id: 'team_deathmatch', label: 'TEAM DEATHMATCH', desc: 'Two teams, most kills wins', icon: '⚔' },
  { id: 'domination', label: 'DOMINATION', desc: 'Capture and hold objectives', icon: '⊙' },
  { id: 'free_for_all', label: 'FREE FOR ALL', desc: 'Every operator for themselves', icon: '◈' },
  { id: 'capture_the_flag', label: 'CAPTURE THE FLAG', desc: 'Steal the enemy flag', icon: '⚑' },
  { id: 'vip', label: 'VIP ESCORT', desc: 'Protect or eliminate the VIP', icon: '★' },
  { id: 'assault', label: 'ASSAULT', desc: 'Attack and defend fixed positions', icon: '⟶' },
];

export default function NewMatchPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '',
    game_mode: '',
    max_players: 20,
    duration_minutes: 30,
    respawn_enabled: true,
    respawn_seconds: 30,
    friendly_fire: false,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.game_mode) { setError('Select a game mode'); return; }
    if (!form.name.trim()) { setError('Enter a match name'); return; }

    setLoading(true);
    setError('');
    try {
      const res = await createMatch({
        name: form.name,
        game_mode: form.game_mode,
        max_players: form.max_players,
        game_config: {
          duration_minutes: form.duration_minutes,
          respawn_enabled: form.respawn_enabled,
          respawn_seconds: form.respawn_seconds,
          friendly_fire: form.friendly_fire,
        }
      });
      navigate(`/match/${res.data.id}`);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create match');
    } finally {
      setLoading(false);
    }
  };

  const Field = ({ label, children }) => (
    <div>
      <label className="block text-xs text-hud-text tracking-widest mb-2">{label}</label>
      {children}
    </div>
  );

  const inputClass = "w-full bg-hud-bg border border-hud-border text-hud-green font-mono text-sm px-3 py-2.5 outline-none focus:border-hud-green/60 transition-colors";

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <div className="mb-6">
          <div className="text-xs text-hud-text tracking-widest mb-1">COMMAND CENTER</div>
          <h1 className="text-2xl font-display font-bold tracking-wider text-hud-green hud-glow-green">
            CREATE MATCH
          </h1>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Match name */}
          <div className="hud-panel p-6">
            <div className="text-xs text-hud-text tracking-widest mb-4">MISSION PARAMETERS</div>
            <Field label="MISSION NAME">
              <input
                type="text"
                value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                placeholder="OPERATION SANDSTORM..."
                className={inputClass}
                maxLength={64}
              />
            </Field>
          </div>

          {/* Game mode */}
          <div className="hud-panel p-6">
            <div className="text-xs text-hud-text tracking-widest mb-4">SELECT GAME MODE</div>
            <div className="grid grid-cols-2 gap-3">
              {GAME_MODES.map(mode => (
                <button
                  key={mode.id}
                  type="button"
                  onClick={() => setForm(f => ({ ...f, game_mode: mode.id }))}
                  className={`p-4 border text-left transition-all duration-150 ${
                    form.game_mode === mode.id
                      ? 'border-hud-green bg-hud-green/10 text-hud-green'
                      : 'border-hud-border hover:border-hud-green/40 text-hud-text'
                  }`}
                >
                  <div className="text-lg mb-1">{mode.icon}</div>
                  <div className="text-xs font-bold tracking-wider">{mode.label}</div>
                  <div className="text-xs opacity-60 mt-1">{mode.desc}</div>
                </button>
              ))}
            </div>
          </div>

          {/* Settings */}
          <div className="hud-panel p-6">
            <div className="text-xs text-hud-text tracking-widest mb-4">MATCH SETTINGS</div>
            <div className="grid grid-cols-2 gap-4">
              <Field label="MAX PLAYERS">
                <input
                  type="number"
                  value={form.max_players}
                  onChange={e => setForm(f => ({ ...f, max_players: parseInt(e.target.value) }))}
                  min={2} max={100}
                  className={inputClass}
                />
              </Field>
              <Field label="DURATION (MIN)">
                <input
                  type="number"
                  value={form.duration_minutes}
                  onChange={e => setForm(f => ({ ...f, duration_minutes: parseInt(e.target.value) }))}
                  min={5} max={180}
                  className={inputClass}
                />
              </Field>
            </div>

            <div className="mt-4 space-y-3">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  checked={form.respawn_enabled}
                  onChange={e => setForm(f => ({ ...f, respawn_enabled: e.target.checked }))}
                  className="accent-hud-green w-4 h-4"
                />
                <span className="text-xs text-hud-text tracking-widest">RESPAWN ENABLED</span>
              </label>

              {form.respawn_enabled && (
                <Field label="RESPAWN DELAY (SECONDS)">
                  <input
                    type="number"
                    value={form.respawn_seconds}
                    onChange={e => setForm(f => ({ ...f, respawn_seconds: parseInt(e.target.value) }))}
                    min={5} max={120}
                    className={inputClass}
                  />
                </Field>
              )}

              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  checked={form.friendly_fire}
                  onChange={e => setForm(f => ({ ...f, friendly_fire: e.target.checked }))}
                  className="accent-hud-red w-4 h-4"
                />
                <span className="text-xs text-hud-text tracking-widest">FRIENDLY FIRE</span>
              </label>
            </div>
          </div>

          {error && (
            <div className="p-3 border border-hud-red/30 bg-hud-red/10 text-hud-red text-sm text-center">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 border border-hud-green bg-hud-green/10 hover:bg-hud-green/20
                       text-hud-green font-mono text-sm tracking-widest transition-all duration-200
                       disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? '⟳ CREATING...' : '⟶ DEPLOY MISSION'}
          </button>
        </form>
      </div>
    </Layout>
  );
}
