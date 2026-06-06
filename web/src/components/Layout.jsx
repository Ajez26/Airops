import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const navItems = [
  { path: '/', label: 'DASHBOARD', icon: '◉' },
  { path: '/match/new', label: 'NEW MATCH', icon: '⊕' },
  { path: '/stats', label: 'STATS', icon: '◈' },
];

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const location = useLocation();

  return (
    <div className="min-h-screen bg-hud-bg flex flex-col">
      {/* Top nav */}
      <header className="border-b border-hud-border bg-hud-surface">
        <div className="max-w-7xl mx-auto px-4 h-14 flex items-center justify-between">
          {/* Logo */}
          <div className="flex items-center gap-4">
            <span className="text-xl font-display font-bold tracking-widest text-hud-green hud-glow-green">
              AIROPS
            </span>
            <div className="h-4 w-px bg-hud-border" />
            <span className="text-xs text-hud-text tracking-widest">COMMAND CENTER</span>
          </div>

          {/* Nav */}
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`px-4 py-2 text-xs tracking-widest transition-all duration-150 ${
                  location.pathname === item.path
                    ? 'text-hud-green border-b border-hud-green hud-glow-green'
                    : 'text-hud-text hover:text-hud-green'
                }`}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </nav>

          {/* User */}
          <div className="flex items-center gap-3">
            <div className="text-xs text-hud-text tracking-wider hidden sm:block">
              {user?.displayName?.toUpperCase() || 'OPERATOR'}
            </div>
            <button
              onClick={logout}
              className="text-xs text-hud-text/50 hover:text-hud-red tracking-widest transition-colors"
            >
              LOGOUT
            </button>
          </div>
        </div>
      </header>

      {/* Main */}
      <main className="flex-1 max-w-7xl mx-auto w-full px-4 py-6">
        {children}
      </main>

      {/* Bottom status bar */}
      <footer className="border-t border-hud-border bg-hud-surface">
        <div className="max-w-7xl mx-auto px-4 h-8 flex items-center justify-between text-xs text-hud-text/30 tracking-widest">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1.5">
              <div className="status-dot active" />
              <span>SYS ONLINE</span>
            </div>
            <span>AIROPS v1.0.0</span>
          </div>
          <span>{new Date().toISOString().slice(0, 19).replace('T', ' ')} UTC</span>
        </div>
      </footer>
    </div>
  );
}
