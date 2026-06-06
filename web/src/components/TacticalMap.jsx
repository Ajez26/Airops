import React, { useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Circle, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix Leaflet default icon
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const createPlayerIcon = (teamColor, isDead) => L.divIcon({
  className: '',
  html: `
    <div style="
      width: 24px; height: 24px;
      border-radius: 50%;
      background: ${isDead ? '#333' : teamColor};
      border: 2px solid ${isDead ? '#555' : 'rgba(255,255,255,0.6)'};
      box-shadow: 0 0 8px ${isDead ? 'transparent' : teamColor}80;
      opacity: ${isDead ? 0.4 : 1};
      display: flex; align-items: center; justify-content: center;
      font-size: 10px; color: white; font-weight: bold;
    ">${isDead ? '✕' : '◉'}</div>
  `,
  iconSize: [24, 24],
  iconAnchor: [12, 12],
});

const TEAM_COLORS = {
  alpha: '#FF4444',
  bravo: '#4444FF',
  neutral: '#888888',
};

function MapUpdater({ center }) {
  const map = useMap();
  useEffect(() => {
    if (center) map.setView(center, map.getZoom());
  }, [center, map]);
  return null;
}

export default function TacticalMap({ players = {}, objectives = [], teams = [] }) {
  const teamColorMap = {};
  teams.forEach(t => { teamColorMap[t.id] = t.color; });

  const center = Object.values(players).find(p => p.lat && p.lng)
    ? [Object.values(players).find(p => p.lat).lat, Object.values(players).find(p => p.lat).lng]
    : [19.4326, -99.1332]; // Mexico City fallback

  return (
    <MapContainer
      center={center}
      zoom={16}
      style={{ height: '100%', width: '100%', background: '#0A0E0F' }}
      className="rounded"
    >
      <TileLayer
        url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        attribution='&copy; <a href="https://carto.com/">CARTO</a>'
      />

      <MapUpdater center={null} />

      {/* Objectives */}
      {objectives.map(obj => (
        <React.Fragment key={obj.id}>
          <Circle
            center={[obj.lat, obj.lng]}
            radius={20}
            pathOptions={{
              color: obj.captured_by ? teamColorMap[obj.captured_by] || '#888' : '#FFB300',
              fillOpacity: 0.2,
              weight: 2,
            }}
          />
          <Marker
            position={[obj.lat, obj.lng]}
            icon={L.divIcon({
              className: '',
              html: `<div style="
                font-size: 18px; filter: drop-shadow(0 0 6px #FFB300);
              ">⊙</div>`,
              iconSize: [20, 20],
              iconAnchor: [10, 10],
            })}
          >
            <Popup>
              <div style={{ background: '#111', color: '#00FF41', padding: '4px 8px', fontFamily: 'monospace', fontSize: '12px' }}>
                {obj.name}<br/>
                Status: {obj.captured_by ? 'CAPTURED' : 'NEUTRAL'}
              </div>
            </Popup>
          </Marker>
        </React.Fragment>
      ))}

      {/* Players */}
      {Object.entries(players).map(([playerId, player]) => {
        if (!player.lat || !player.lng) return null;
        const color = teamColorMap[player.teamId] || '#888888';
        const isDead = player.status === 'dead';

        return (
          <Marker
            key={playerId}
            position={[player.lat, player.lng]}
            icon={createPlayerIcon(color, isDead)}
          >
            <Popup>
              <div style={{ background: '#111', color: '#00FF41', padding: '4px 8px', fontFamily: 'monospace', fontSize: '12px' }}>
                {player.displayName || playerId.slice(0, 8)}<br/>
                Status: {isDead ? '☠ DEAD' : '◉ ALIVE'}<br/>
                Team: {player.teamId?.slice(0, 8) || 'NONE'}
              </div>
            </Popup>
          </Marker>
        );
      })}
    </MapContainer>
  );
}
