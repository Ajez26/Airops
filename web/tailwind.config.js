/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        // Military HUD palette
        hud: {
          green: '#00FF41',
          amber: '#FFB300',
          red: '#FF4444',
          blue: '#4444FF',
          bg: '#0A0E0F',
          surface: '#111518',
          border: '#1E2A2E',
          text: '#8FA9AF',
        },
        team: {
          alpha: '#FF4444',
          bravo: '#4444FF',
          neutral: '#888888',
        }
      },
      fontFamily: {
        mono: ['JetBrains Mono', 'Courier New', 'monospace'],
        display: ['Rajdhani', 'Impact', 'sans-serif'],
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'scan': 'scan 2s linear infinite',
        'blink': 'blink 1s step-end infinite',
      },
      keyframes: {
        scan: {
          '0%': { backgroundPosition: '0 0' },
          '100%': { backgroundPosition: '0 100%' },
        },
        blink: {
          '50%': { opacity: '0' },
        }
      }
    },
  },
  plugins: [],
};
