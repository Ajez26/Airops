require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const { createServer } = require('http');
const { WebSocketServer } = require('ws');

const pool = require('./db/pool');
const authRoutes = require('./routes/auth');
const matchRoutes = require('./routes/matches');
const playerRoutes = require('./routes/players');
const statsRoutes = require('./routes/stats');
const { setupWebSocket } = require('./services/websocket');

const app = express();
const server = createServer(app);
const wss = new WebSocketServer({ server });

// Middleware
app.use(helmet());
app.use(cors({ origin: process.env.ALLOWED_ORIGINS?.split(',') || '*' }));
app.use(express.json());

// Attach DB pool to app locals (available in all route handlers as req.app.locals.db)
app.locals.db = pool;

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/matches', matchRoutes);
app.use('/api/players', playerRoutes);
app.use('/api/stats', statsRoutes);

// Health check
app.get('/health', async (req, res) => {
  try {
    await pool.query('SELECT 1');
    res.json({ status: 'ok', version: '1.0.0', db: 'connected' });
  } catch {
    res.status(503).json({ status: 'degraded', version: '1.0.0', db: 'disconnected' });
  }
});

// WebSocket setup (real-time GPS tracking, game events)
setupWebSocket(wss);

// Global error handler — never leak stack traces to client
app.use((err, req, res, _next) => {
  const status = err.status || 500;
  if (status >= 500) console.error('[ERROR]', err);
  res.status(status).json({ error: status < 500 ? err.message : 'Internal server error' });
});

// Handle unhandled promise rejections so the process doesn't crash
process.on('unhandledRejection', (reason) => {
  console.error('[UNHANDLED REJECTION]', reason);
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`🎯 Airops backend running on port ${PORT}`);
  console.log(`   WebSocket ready`);
  console.log(`   DB: ${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '5432'}/${process.env.DB_NAME || 'airops'}`);
});
