
/* ── Navbar Base ── */
.navbar {
  position: sticky; top: 0; z-index: 1000;
  background: rgba(7, 7, 14, 0.65);
  backdrop-filter: blur(32px) saturate(220%);
  -webkit-backdrop-filter: blur(32px) saturate(220%);
  border-bottom: 1px solid transparent;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  overflow: visible;
}
.navbar::before {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 50% -20%, rgba(0,240,255,0.07) 0%, transparent 60%),
    radial-gradient(ellipse 60% 50% at 30% 120%, rgba(124,58,237,0.05) 0%, transparent 50%),
    radial-gradient(ellipse 50% 40% at 70% 120%, rgba(236,72,153,0.03) 0%, transparent 50%);
  z-index: -1;
  animation: navbarAura 10s ease-in-out infinite alternate;
}
.navbar::after {
  content: '';
  position: absolute; inset: 0;
  background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,240,255,0.008) 2px, rgba(0,240,255,0.008) 4px);
  z-index: -1;
  pointer-events: none;
}
.navbar-scrolled {
  background: rgba(7, 7, 14, 0.85);
  border-bottom-color: rgba(0,240,255,0.06);
}

/* ── Scanline border ── */
.navbar-scanline {
  position: absolute; bottom: -1px; left: 0; right: 0; height: 1px;
  background: transparent;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}
.navbar-scanline.active {
  background: linear-gradient(90deg, transparent 0%, rgba(0,240,255,0.5) 5%, rgba(0,240,255,0.9) 25%, #7C3AED 50%, rgba(0,240,255,0.9) 75%, rgba(0,240,255,0.5) 95%, transparent 100%);
  background-size: 200% 100%;
  animation: scanlineBreathe 4s ease-in-out infinite;
  box-shadow: 0 0 10px rgba(0,240,255,0.2), 0 0 30px rgba(0,240,255,0.06);
}

/* ── Glow orb ── */
.navbar-glow-orb {
  position: absolute; top: -50px; left: 50%; transform: translateX(-50%);
  width: 400px; height: 100px;
  background: radial-gradient(ellipse at center, rgba(0,240,255,0.08) 0%, rgba(0,240,255,0.03) 30%, transparent 70%);
  pointer-events: none;
  animation: orbDance 6s ease-in-out infinite;
}

/* ── Brand / Logo ── */
.navbar-brand { display: flex; align-items: center; gap: 10px; text-decoration: none; flex-shrink: 0; position: relative; }
.navbar-brand:hover { text-decoration: none; }
.brand-mark {
  position: relative; width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
}
.brand-svg {
  position: relative; z-index: 1;
  width: 26px; height: 26px;
  filter: drop-shadow(0 0 6px rgba(0,240,255,0.4));
  transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.brand-svg path {
  transition: d 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.navbar:hover .brand-svg {
  animation: brandFloat 3s ease-in-out infinite, brandSpin 10s linear infinite;
}
.brand-pulse {
  position: absolute; inset: -6px; border-radius: 50%;
  border: 1px solid rgba(0,240,255,0.15);
  opacity: 0;
  transition: opacity 0.3s;
}
.brand-pulse.pulsing {
  opacity: 0.5;
  animation: brandPulse 2.5s ease-in-out infinite;
  box-shadow: 0 0 20px rgba(0,240,255,0.1);
}
.brand-text {
  font-size: 18px; font-weight: 800; letter-spacing: -0.6px;
  background: linear-gradient(135deg, #fff 30%, #00F0FF 60%, #7C3AED 100%);
  background-size: 200% 100%;
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: brandTextGradient 6s ease-in-out infinite;
  transition: filter 0.3s;
}
.navbar:hover .brand-text {
  filter: drop-shadow(0 0 8px rgba(0,240,255,0.2));
}

/* ── Nav Links ── */
.nav-links {
  display: flex; align-items: center; gap: 2px; flex: 1;
  position: relative; justify-content: center;
}
.nav-lk {
  position: relative; display: flex; align-items: center; gap: 6px;
  padding: 8px 16px; font-size: 13px; font-weight: 500;
  color: rgba(148,163,184,0.85); text-decoration: none;
  border-radius: 8px; transition: all 0.25s cubic-bezier(0.23, 1, 0.32, 1);
  font-family: var(--font-sans); letter-spacing: 0;
  z-index: 1; overflow: hidden;
}
.nav-lk::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(0,240,255,0.08), rgba(124,58,237,0.04));
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 8px;
}
.nav-lk::after {
  content: '';
  position: absolute; bottom: 4px; left: 50%; transform: translateX(-50%) scaleX(0);
  width: 60%; height: 2px;
  background: linear-gradient(90deg, transparent, var(--cyber-cyan), transparent);
  border-radius: 1px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: blur(1px);
}
.nav-lk:hover {
  color: #fff;
  transform: translateY(-1px);
}
.nav-lk:hover::before {
  opacity: 1;
}
.nav-lk:hover::after {
  transform: translateX(-50%) scaleX(1);
}
.nav-lk.active {
  color: var(--cyber-cyan);
  text-shadow: 0 0 16px rgba(0,240,255,0.3);
  background: rgba(0,240,255,0.06);
  box-shadow: inset 0 0 0 1px rgba(0,240,255,0.08);
}
.nav-lk.active::after {
  transform: translateX(-50%) scaleX(1);
  opacity: 0.8;
  animation: activeGlow 2s ease-in-out infinite;
}
.nav-lk .nl-icon {
  color: rgba(148,163,184,0.6);
  transition: all 0.3s;
  display: flex; align-items: center;
}
.nav-lk:hover .nl-icon {
  color: var(--cyber-cyan);
  filter: drop-shadow(0 0 8px rgba(0,240,255,0.4));
  transform: scale(1.1);
}
.nav-lk.active .nl-icon {
  color: var(--cyber-cyan);
  filter: drop-shadow(0 0 8px rgba(0,240,255,0.5));
}

/* ── Nav Indicator ── */
.nav-indicator {
  position: absolute; bottom: -2px; height: 3px;
  background: linear-gradient(90deg, #00F0FF, #06B6D4, #7C3AED, #EC4899);
  background-size: 300% 100%;
  border-radius: 0 0 3px 3px;
  transition: left 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), width 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.3s;
  pointer-events: none;
  box-shadow: 0 0 14px rgba(0,240,255,0.3), 0 0 30px rgba(0,240,255,0.08);
  animation: indicatorFlow 3s linear infinite;
}

/* ── User Dropdown ── */
.user-dropdown-btn {
  display: flex; align-items: center; gap: 8px; padding: 4px 10px 4px 4px;
  border: 1px solid rgba(255,255,255,0.06); border-radius: 10px;
  background: rgba(255,255,255,0.02);
  color: var(--text-secondary);
  cursor: pointer; transition: all 0.25s; font-family: inherit;
}
.user-dropdown-btn:hover {
  border-color: rgba(0,240,255,0.2);
  background: rgba(0,240,255,0.06);
  box-shadow: 0 0 12px rgba(0,240,255,0.06);
}
.user-dropdown-btn svg { transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1); }
.user-dropdown-btn svg.rotated { transform: rotate(180deg); }

.user-menu-dropdown {
  position: fixed; z-index: 10000; min-width: 230px;
  background: rgba(10, 12, 18, 0.92);
  border: 1px solid rgba(0,240,255,0.1);
  border-radius: 14px; padding: 6px;
  box-shadow: 0 16px 48px rgba(0,0,0,0.6), 0 0 0 1px rgba(0,240,255,0.04), 0 0 40px rgba(0,240,255,0.03);
  backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  animation: dropdownIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.user-menu-dropdown::before {
  content: '';
  position: absolute; inset: -1px;
  border-radius: 15px;
  background: linear-gradient(135deg, rgba(0,240,255,0.08), transparent, rgba(124,58,237,0.06));
  z-index: -1;
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: exclude;
  -webkit-mask-composite: xor;
  padding: 1px;
  pointer-events: none;
}
