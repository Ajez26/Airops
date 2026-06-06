import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './hooks/useAuth';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import NewMatchPage from './pages/NewMatchPage';
import MatchControlPage from './pages/MatchControlPage';
import StatsPage from './pages/StatsPage';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <LoadingScreen />;
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

function LoadingScreen() {
  return (
    <div className="min-h-screen bg-hud-bg flex items-center justify-center">
      <div className="text-center">
        <div className="text-hud-green font-display text-2xl font-bold tracking-widest mb-4 hud-glow-green animate-pulse">
          AIROPS
        </div>
        <div className="text-hud-text text-sm tracking-widest">INITIALIZING...</div>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="/match/new" element={<PrivateRoute><NewMatchPage /></PrivateRoute>} />
          <Route path="/match/:id" element={<PrivateRoute><MatchControlPage /></PrivateRoute>} />
          <Route path="/stats" element={<PrivateRoute><StatsPage /></PrivateRoute>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
