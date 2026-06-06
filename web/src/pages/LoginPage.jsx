import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function LoginPage() {
  const { loginWithGoogle } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleGoogle = async () => {
    setLoading(true);
    setError('');
    try {
      await loginWithGoogle();
      navigate('/');
    } catch (err) {
      setError('Login failed. Try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-hud-bg flex items-center justify-center p-4">
      {/* Scanline background */}
      <div className="absolute inset-0 opacity-5"
        style={{ backgroundImage: 'repeating-linear-gradient(0deg, transparent, transparent 2px, #00FF41 2px, #00FF41 3px)', backgroundSize: '100% 4px' }}
      />

      <div className="relative w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-10">
          <div className="inline-block mb-3">
            <div className="text-5xl font-display font-bold tracking-[0.3em] text-hud-green hud-glow-green">
              AIROPS
            </div>
            <div className="text-xs tracking-[0.5em] text-hud-text mt-1">TACTICAL FIELD SYSTEM v1.0</div>
          </div>
          <div className="flex items-center gap-2 justify-center mt-2">
            <div className="h-px flex-1 bg-hud-border" />
            <div className="status-dot active" />
            <div className="h-px flex-1 bg-hud-border" />
          </div>
        </div>

        {/* Login card */}
        <div className="hud-panel p-8">
          <div className="text-xs text-hud-text tracking-widest mb-6 text-center">
            OPERATOR AUTHENTICATION REQUIRED
          </div>

          {error && (
            <div className="mb-4 p-3 border border-hud-red/30 bg-hud-red/10 text-hud-red text-sm text-center">
              {error}
            </div>
          )}

          <button
            onClick={handleGoogle}
            disabled={loading}
            className="w-full py-3 px-6 border border-hud-green/40 bg-hud-green/5 hover:bg-hud-green/10 
                       text-hud-green font-mono text-sm tracking-widest transition-all duration-200
                       disabled:opacity-50 disabled:cursor-not-allowed
                       clip-path-[polygon(0_0,calc(100%-8px)_0,100%_8px,100%_100%,8px_100%,0_calc(100%-8px))]"
          >
            {loading ? (
              <span className="animate-pulse">AUTHENTICATING...</span>
            ) : (
              <span>⟶ SIGN IN WITH GOOGLE</span>
            )}
          </button>

          <div className="mt-6 text-center text-xs text-hud-text/50 tracking-wider">
            ORGANIZER / PLAYER ACCESS
          </div>
        </div>

        {/* Bottom status bar */}
        <div className="mt-4 flex items-center justify-between text-xs text-hud-text/30 tracking-widest">
          <span>SYS.READY</span>
          <div className="flex items-center gap-2">
            <div className="status-dot active" />
            <span>SECURE CHANNEL</span>
          </div>
        </div>
      </div>
    </div>
  );
}
