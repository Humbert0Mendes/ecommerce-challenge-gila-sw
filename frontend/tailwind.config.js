/** @type {import('tailwindcss').Config} */
import daisyui from 'daisyui';
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: { extend: { colors: { brand: { 50: '#eff6ff', 500: '#1565c0', 700: '#003c8f' } } } },
  plugins: [daisyui],
  daisyui: { themes: ['light'] },
};
