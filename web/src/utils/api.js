import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:3000';

const api = axios.create({
  baseURL: `${API_URL}/api`,
});

// Attach token from localStorage automatically
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('airops_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auth
export const loginWithFirebase = (token) =>
  api.post('/auth/login', {}, { headers: { Authorization: `Bearer ${token}` } });

// Matches
export const createMatch = (data) => api.post('/matches', data);
export const joinMatch = (code) => api.post('/matches/join', { code });
export const getMatch = (id) => api.get(`/matches/${id}`);
export const startMatch = (id) => api.post(`/matches/${id}/start`);
export const endMatch = (id) => api.post(`/matches/${id}/end`);

// Players
export const getPlayer = (id) => api.get(`/players/${id}`);
export const getMyProfile = () => api.get('/players/me');

// Stats
export const getMyStats = () => api.get('/stats/me');
export const getMatchStats = (matchId) => api.get(`/stats/match/${matchId}`);
export const getLeaderboard = () => api.get('/stats/leaderboard');

export default api;
