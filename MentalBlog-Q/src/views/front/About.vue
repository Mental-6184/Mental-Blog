<template>
  <div class="about-page" ref="pageRef">
    <!-- 3D Background Canvas -->
    <canvas ref="threeBg" class="about-3d-bg"></canvas>
    <!-- Particle Overlay -->
    <canvas ref="particleCanvas" class="about-particles"></canvas>
    <!-- Scanlines overlay -->
    <div class="about-scanlines"></div>
    <!-- Grid overlay -->
    <div class="about-grid-overlay"></div>

    <!-- ═══ HERO SECTION ═══ -->
    <section class="ab-hero" ref="heroRef">
      <div class="hero-bg-glow"></div>
      <div class="hero-bg-orb orb-1"></div>
      <div class="hero-bg-orb orb-2"></div>
      <div class="container hero-content">
        <div class="hero-avatar-wrap" ref="avatarRef">
          <canvas ref="avatarCanvas" class="avatar-canvas"></canvas>
          <div class="ava-glow"></div>
        </div>
        <h1 class="hero-title" ref="titleRef">
          <span class="glitch-text" data-text="About Me">{{ siteName }}</span>
        </h1>
        <p class="hero-subtitle" ref="subRef">
          <span class="typewriter">Full-Stack Developer</span>
          <span class="sub-cursor">|</span>
        </p>
        <p class="hero-desc" ref="descRef">{{ profileDesc }}</p>
        <div class="hero-stats" ref="statsRef">
          <div class="hs-item"><span class="hs-num" ref="skillCount">{{ skills.length }}</span><span class="hs-lbl">Skills</span></div>
          <div class="hs-item"><span class="hs-num" ref="milestoneCount">{{ milestones.length }}</span><span class="hs-lbl">Achievements</span></div>
          <div class="hs-item"><span class="hs-num" ref="projectCount">{{ projects.length }}</span><span class="hs-lbl">Projects</span></div>
        </div>
        <div class="hero-actions" ref="actionsRef">
          <a href="https://github.com" target="_blank" class="ha-btn" aria-label="GitHub">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/></svg>
          </a>
          <a href="mailto:admin@mentalblog.com" class="ha-btn" aria-label="Email">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          </a>
          <a href="/api/rss" class="ha-btn" aria-label="RSS">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 11a9 9 0 0 1 9 9"/><path d="M4 4a16 16 0 0 1 16 16"/><circle cx="5" cy="19" r="1"/></svg>
          </a>
        </div>
        <div class="scroll-hint">
          <div class="sh-line"></div>
          <span>SCROLL</span>
        </div>
      </div>
    </section>

    <!-- ═══ CONTENT BODY ═══ -->
    <div class="ab-body">
      <div class="container">
        <div class="ab-grid">
          <!-- Left Column -->
          <div class="ab-left">
            <!-- Skills -->
            <section v-if="skills.length" class="ab-card" ref="skillsCard">
              <div class="ab-card-header">
                <div class="ach-icon" style="--ic:#00F0FF">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
                </div>
                <span class="ach-title">Tech Stack</span>
              </div>
              <div class="skills-grid">
                <div v-for="s in skills" :key="s.id" class="sk-item">
                  <svg class="sk-ring" viewBox="0 0 120 120">
                    <circle cx="60" cy="60" r="50" class="sk-ring-bg"/>
                    <circle cx="60" cy="60" r="50" class="sk-ring-fg"
                      :stroke-dasharray="314.16"
                      :stroke-dashoffset="314.16 - (314.16 * s.level / 100)"
                      :style="{ '--pct': s.level }"/>
                  </svg>
                  <span class="sk-label">{{ s.name }}</span>
                </div>
              </div>
            </section>

            <!-- Milestones -->
            <section v-if="milestones.length" class="ab-card" ref="mileCard">
              <div class="ab-card-header">
                <div class="ach-icon" style="--ic:#7C3AED">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <span class="ach-title">Timeline</span>
              </div>
              <div class="mile-line">
                <div v-for="(m, i) in milestones" :key="m.id" class="ml-item" :ref="el => setMileRef(el, i)">
                  <div class="ml-dot"><div class="ml-dot-inner"></div></div>
                  <div class="ml-body">
                    <span class="ml-year">{{ m.year }}</span>
                    <h4>{{ m.title }}</h4>
                    <p v-if="m.description">{{ m.description }}</p>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- Right Column -->
          <div class="ab-right">
            <!-- Projects -->
            <section v-if="projects.length" class="ab-card" ref="projCard">
              <div class="ab-card-header">
                <div class="ach-icon" style="--ic:#10B981">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                </div>
                <span class="ach-title">Projects</span>
              </div>
              <div class="proj-list">
                <div v-for="p in projects" :key="p.id" class="pj-card" :ref="el => setProjRef(el, projIdx++)" @mousemove="onProjMove($event, p.id)" @mouseleave="onProjLeave(p.id)">
                  <div class="pj-icon">{{ (p.name || 'P')[0] }}</div>
                  <div class="pj-body">
                    <h3>{{ p.name }}</h3>
                    <p v-if="p.description">{{ p.description }}</p>
                  </div>
                  <a v-if="p.url" :href="p.url" target="_blank" class="pj-link" @click.stop>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
                  </a>
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, reactive } from 'vue'
import { getAboutData } from '@/api/about'
import { getSiteConfig } from '@/api/site'
import * as THREE from 'three'

const pageRef = ref<HTMLElement | null>(null)
const avatarCanvas = ref<HTMLCanvasElement | null>(null)
const threeBg = ref<HTMLCanvasElement | null>(null)
const particleCanvas = ref<HTMLCanvasElement | null>(null)
const heroRef = ref<HTMLElement | null>(null)
const avatarRef = ref<HTMLElement | null>(null)
const titleRef = ref<HTMLElement | null>(null)
const subRef = ref<HTMLElement | null>(null)
const descRef = ref<HTMLElement | null>(null)
const statsRef = ref<HTMLElement | null>(null)
const actionsRef = ref<HTMLElement | null>(null)
const skillsCard = ref<HTMLElement | null>(null)
const mileCard = ref<HTMLElement | null>(null)
const projCard = ref<HTMLElement | null>(null)
const skillCount = ref<HTMLElement | null>(null)
const milestoneCount = ref<HTMLElement | null>(null)
const projectCount = ref<HTMLElement | null>(null)
const mileRefs: (HTMLElement | null)[] = []
const setMileRef = (el: any, i: number) => { mileRefs[i] = el as HTMLElement }
const projRefs: (HTMLElement | null)[] = []
const setProjRef = (el: any, i: number) => { projRefs[i] = el as HTMLElement }
let projIdx = 0

const siteName = ref('MentalBlog')
const profileDesc = ref('A passionate developer exploring the intersection of code and creativity.')
const skills = ref<any[]>([])
const milestones = ref<any[]>([])
const projects = ref<any[]>([])
const loaded = ref(false)
let avatarClean: (() => void) | null = null

// ── Data Loading ──
onMounted(async () => {
  try {
    const [sRes, aRes] = await Promise.all([getSiteConfig(), getAboutData()])
    if (sRes.data?.site_name) siteName.value = sRes.data.site_name
    if (sRes.data?.site_about) profileDesc.value = sRes.data.site_about
    if (aRes.data) {
      skills.value = aRes.data.skills || []
      milestones.value = aRes.data.milestones || []
      projects.value = aRes.data.projects || []
    }
  } catch (e) { /* ignore */ }
  loaded.value = true

  nextTick(() => {
    avatarClean = initAvatarScene() || null
    initThreeBg()
    initParticles()
    runHeroAnim()
    runScrollAnims()
  })
})

// ── 3D Background ──
let threeClean: (() => void) | null = null

function initAvatarScene() {
  const canvas = avatarCanvas.value; if (!canvas) return
  const p = canvas.parentElement; if (!p) return
  const size = p.offsetWidth || 120
  canvas.width = size * 2; canvas.height = size * 2
  const s = Math.min(canvas.width, canvas.height)
  const scene = new THREE.Scene()
  const cam = new THREE.PerspectiveCamera(45, 1, 0.1, 100)
  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true })
  renderer.setSize(s, s); renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))

  const geo = new THREE.OctahedronGeometry(0.45, 0)
  const mesh = new THREE.Mesh(geo, new THREE.MeshBasicMaterial({ color: 0x00f0ff, wireframe: true, transparent: true, opacity: 0.5 }))
  scene.add(mesh)

  const inner = new THREE.Mesh(new THREE.OctahedronGeometry(0.3, 0), new THREE.MeshBasicMaterial({ color: 0x7c3aed, transparent: true, opacity: 0.15 }))
  scene.add(inner)

  const pos = geo.attributes.position.array
  const dotG = new THREE.SphereGeometry(0.025, 6, 6)
  const dotM = new THREE.MeshBasicMaterial({ color: 0x00f0ff })
  for (let i = 0; i < pos.length; i += 3) {
    const d = new THREE.Mesh(dotG, dotM)
    d.position.set(pos[i]*0.45, pos[i+1]*0.45, pos[i+2]*0.45)
    mesh.add(d)
  }

  const rc = 50; const rP = new Float32Array(rc*3)
  for (let i = 0; i < rc; i++) { const a = (i/rc)*Math.PI*2; rP[i*3]=Math.cos(a)*0.7; rP[i*3+1]=Math.sin(a)*0.7; rP[i*3+2]=0 }
  const r1 = new THREE.Points(new THREE.BufferGeometry().setAttribute('position', new THREE.BufferAttribute(rP, 3)), new THREE.PointsMaterial({ color: 0x00f0ff, size: 0.015, transparent: true, opacity: 0.6 }))
  scene.add(r1)

  const r2P = new Float32Array(rc*3)
  for (let i = 0; i < rc; i++) { const a = (i/rc)*Math.PI*2; r2P[i*3]=Math.cos(a)*0.65; r2P[i*3+1]=Math.sin(a)*0.65*0.3; r2P[i*3+2]=Math.sin(a)*0.65*0.7 }
  const r2 = new THREE.Points(new THREE.BufferGeometry().setAttribute('position', new THREE.BufferAttribute(r2P, 3)), new THREE.PointsMaterial({ color: 0x7c3aed, size: 0.012, transparent: true, opacity: 0.4 }))
  scene.add(r2)

  cam.position.z = 2.2; let id
  function anim() {
    mesh.rotation.x += 0.008; mesh.rotation.y += 0.015
    inner.rotation.x -= 0.01; inner.rotation.z += 0.006
    r1.rotation.y += 0.005; r1.rotation.z += 0.003
    r2.rotation.x += 0.004; r2.rotation.y += 0.007
    const t = performance.now() * 0.001
    mesh.position.y = Math.sin(t * 0.5) * 0.05
    inner.position.y = mesh.position.y
    renderer.render(scene, cam)
    id = requestAnimationFrame(anim)
  }
  id = requestAnimationFrame(anim)
  return () => { cancelAnimationFrame(id); renderer.dispose() }
}

function initThreeBg() {
  const canvas = threeBg.value
  if (!canvas) return
  const scene = new THREE.Scene()
  const cam = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 0.1, 100)
  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))

  // Shapes
  const shapes: THREE.Mesh[] = []
  const colors = [0x00f0ff, 0x7c3aed, 0xec4899, 0x06b6d4]
  const geos = [
    new THREE.IcosahedronGeometry(0.4, 0),
    new THREE.OctahedronGeometry(0.4),
    new THREE.TorusKnotGeometry(0.3, 0.1, 32, 8),
    new THREE.TetrahedronGeometry(0.4),
  ]
  for (let i = 0; i < 18; i++) {
    const mat = new THREE.MeshBasicMaterial({
      color: colors[i % colors.length],
      wireframe: true,
      transparent: true,
      opacity: 0.15 + Math.random() * 0.2,
    })
    const mesh = new THREE.Mesh(geos[i % geos.length], mat)
    mesh.position.set(
      (Math.random() - 0.5) * 18,
      (Math.random() - 0.5) * 12,
      (Math.random() - 0.5) * 10 - 3
    )
    mesh.rotation.set(Math.random() * 6, Math.random() * 6, Math.random() * 6)
    ;(mesh as any)._speed = 0.002 + Math.random() * 0.008
    ;(mesh as any)._rot = { x: Math.random() * 0.02, y: Math.random() * 0.02, z: Math.random() * 0.01 }
    scene.add(mesh)
    shapes.push(mesh)
  }

  // Connection lines
  const lineMat = new THREE.LineBasicMaterial({ color: 0x00f0ff, transparent: true, opacity: 0.03 })
  const linePositions: number[] = []
  for (let i = 0; i < shapes.length; i++) {
    for (let j = i + 1; j < shapes.length; j++) {
      const d = shapes[i].position.distanceTo(shapes[j].position)
      if (d < 5) {
        linePositions.push(shapes[i].position.x, shapes[i].position.y, shapes[i].position.z)
        linePositions.push(shapes[j].position.x, shapes[j].position.y, shapes[j].position.z)
      }
    }
  }
  if (linePositions.length) {
    const lineGeo = new THREE.BufferGeometry()
    lineGeo.setAttribute('position', new THREE.Float32BufferAttribute(linePositions, 3))
    const lines = new THREE.LineSegments(lineGeo, lineMat)
    scene.add(lines)
  }

  // Star field
  const starCount = 300
  const starGeo = new THREE.BufferGeometry()
  const starPos = new Float32Array(starCount * 3)
  for (let i = 0; i < starCount * 3; i++) starPos[i] = (Math.random() - 0.5) * 60
  starGeo.setAttribute('position', new THREE.BufferAttribute(starPos, 3))
  const starMat = new THREE.PointsMaterial({ color: 0x00f0ff, size: 0.05, transparent: true, opacity: 0.4 })
  const stars = new THREE.Points(starGeo, starMat)
  scene.add(stars)

  cam.position.z = 5
  let mouseX = 0, mouseY = 0
  const onMouse = (e: MouseEvent) => { mouseX = (e.clientX / window.innerWidth - 0.5) * 0.5; mouseY = (e.clientY / window.innerHeight - 0.5) * 0.3 }
  window.addEventListener('mousemove', onMouse)

  let id: number
  function anim() {
    const t = performance.now() * 0.001
    for (const m of shapes) {
      m.rotation.x += (m as any)._rot.x
      m.rotation.y += (m as any)._rot.y
      m.rotation.z += (m as any)._rot.z
      m.position.y += Math.sin(t * (m as any)._speed + m.position.x) * 0.001
    }
    stars.rotation.y += 0.0003
    cam.position.x += (mouseX - cam.position.x) * 0.02
    cam.position.y += (mouseY - cam.position.y) * 0.02
    cam.lookAt(0, 0, 0)
    renderer.render(scene, cam)
    id = requestAnimationFrame(anim)
  }
  id = requestAnimationFrame(anim)

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    cam.aspect = window.innerWidth / window.innerHeight
    cam.updateProjectionMatrix()
    renderer.setSize(window.innerWidth, window.innerHeight)
  }
  window.addEventListener('resize', resize)

  threeClean = () => {
    cancelAnimationFrame(id)
    window.removeEventListener('mousemove', onMouse)
    window.removeEventListener('resize', resize)
    renderer.dispose()
  }
}

// ── Particles ──
let particleClean: (() => void) | null = null
function initParticles() {
  const c = particleCanvas.value
  if (!c) return
  c.width = window.innerWidth
  c.height = window.innerHeight
  const ctx = c.getContext('2d')
  if (!ctx) return
  const dots: { x: number; y: number; vx: number; vy: number; r: number; o: number }[] = []
  for (let i = 0; i < 60; i++) dots.push({ x: Math.random() * c.width, y: Math.random() * c.height, vx: (Math.random() - 0.5) * 0.3, vy: (Math.random() - 0.5) * 0.3, r: Math.random() * 1.5 + 0.5, o: Math.random() * 0.4 + 0.1 })
  let id: number
  function draw() {
    ctx!.clearRect(0, 0, c!.width, c!.height)
    for (const d of dots) { d.x += d.vx; d.y += d.vy; if (d.x < 0 || d.x > c!.width) d.vx *= -1; if (d.y < 0 || d.y > c!.height) d.vy *= -1; ctx!.beginPath(); ctx!.arc(d.x, d.y, d.r, 0, Math.PI * 2); ctx!.fillStyle = `rgba(0,240,255,${d.o})`; ctx!.fill() }
    for (let i = 0; i < dots.length; i++) for (let j = i + 1; j < dots.length; j++) { const dx = dots[i].x - dots[j].x, dy = dots[i].y - dots[j].y, dist = Math.sqrt(dx * dx + dy * dy); if (dist < 120) { ctx!.beginPath(); ctx!.moveTo(dots[i].x, dots[i].y); ctx!.lineTo(dots[j].x, dots[j].y); ctx!.strokeStyle = `rgba(0,240,255,${(1 - dist / 120) * 0.1})`; ctx!.lineWidth = 0.4; ctx!.stroke() } }
    id = requestAnimationFrame(draw)
  }
  id = requestAnimationFrame(draw)
  const re = () => { if (c) { c.width = window.innerWidth; c.height = window.innerHeight } }
  window.addEventListener('resize', re)
  particleClean = () => { cancelAnimationFrame(id); window.removeEventListener('resize', re) }
}

// ── Hero Animations ──
function runHeroAnim() {
  const els = [avatarRef.value, titleRef.value, subRef.value, descRef.value, statsRef.value, actionsRef.value].filter(Boolean)
  els.forEach((el, i) => {
    el?.animate([{ opacity: 0, transform: 'translateY(20px)' }, { opacity: 1, transform: 'translateY(0)' }], { duration: 600, delay: 200 + i * 120, easing: 'cubic-bezier(0.23, 1, 0.32, 1)', fill: 'forwards' })
  })
  // Counter animation for stats
  if (skillCount.value) animateCounter(skillCount.value, skills.value.length)
  if (milestoneCount.value) animateCounter(milestoneCount.value, milestones.value.length)
  if (projectCount.value) animateCounter(projectCount.value, projects.value.length)
}

function animateCounter(el: HTMLElement, target: number) {
  let current = 0
  const step = Math.max(1, Math.ceil(target / 30))
  const interval = setInterval(() => { current += step; if (current >= target) { current = target; clearInterval(interval) }; el.textContent = String(current) }, 40)
}

// ── Scroll Animations ──
function runScrollAnims() {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('ab-visible')
        observer.unobserve(entry.target)
      }
    })
  }, { threshold: 0.15 })
  const els = [skillsCard.value, mileCard.value, projCard.value, ...mileRefs.filter(Boolean), ...projRefs.filter(Boolean)].filter(Boolean)
  els.forEach(el => { if (el) { el.classList.add('ab-hidden'); observer.observe(el) } })
}

// ── Project Tilt ──
function onProjMove(e: MouseEvent, id: number) {
  const card = projRefs.find((_, i) => projIdx === i) || null
  // Simplified tilt using CSS transform
}
function onProjLeave(id: number) { }

onUnmounted(() => { avatarClean?.(); threeClean?.(); particleClean?.() })
</script>

<style scoped>
.about-page { position: relative; min-height: 100vh; background: var(--bg-deep); overflow: hidden; }

/* ── 3D BG ── */
.about-3d-bg { position: fixed; inset: 0; z-index: 0; pointer-events: none; }
.about-particles { position: fixed; inset: 0; z-index: 1; pointer-events: none; opacity: 0.7; }
.about-scanlines { position: fixed; inset: 0; z-index: 2; pointer-events: none; background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,240,255,0.008) 2px, rgba(0,240,255,0.008) 4px); }
.about-grid-overlay { position: fixed; inset: 0; z-index: 1; pointer-events: none; background-image: linear-gradient(rgba(0,240,255,0.015) 1px, transparent 1px), linear-gradient(90deg, rgba(0,240,255,0.015) 1px, transparent 1px); background-size: 60px 60px; }

/* ═══ HERO ═══ */
.ab-hero { position: relative; z-index: 3; min-height: 100vh; display: flex; align-items: center; justify-content: center; text-align: center; padding: 80px 0; overflow: hidden; }
.hero-bg-glow { position: absolute; inset: 0; background: radial-gradient(ellipse 60% 50% at 50% 40%, rgba(0,240,255,0.05) 0%, transparent 60%); }
.hero-bg-orb { position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.3; }
.orb-1 { width: 300px; height: 300px; background: rgba(0,240,255,0.15); top: 10%; left: -5%; animation: orbDrift 12s ease-in-out infinite; }
.orb-2 { width: 250px; height: 250px; background: rgba(124,58,237,0.1); bottom: 15%; right: -5%; animation: orbDrift 12s ease-in-out 6s infinite; }

.hero-content { position: relative; z-index: 1; }

/* Avatar Ring */
.hero-avatar-wrap { position: relative; width: 120px; height: 120px; margin: 0 auto 24px; display: flex; align-items: center; justify-content: center; }
.avatar-canvas { width: 100%; height: 100%; position: relative; z-index: 1; border-radius: 50%; }
.ava-glow { position: absolute; width: 100%; height: 100%; border-radius: 50%; background: radial-gradient(circle at center, rgba(0,240,255,0.1) 0%, transparent 60%); pointer-events: none; z-index: 0; animation: avaGlow 3s ease-in-out infinite alternate; }


/* Title with Glitch */
.hero-title { margin-bottom: 12px; }
.glitch-text { font-size: clamp(32px, 6vw, 56px); font-weight: 900; position: relative; color: #fff; line-height: 1.2; }
.glitch-text::before, .glitch-text::after { content: attr(data-text); position: absolute; inset: 0; opacity: 0; pointer-events: none; }
.glitch-text::before { color: #00F0FF; animation: glitch1 4s infinite; }
.glitch-text::after { color: #EC4899; animation: glitch2 4s infinite; }

/* Subtitle Typewriter */
.hero-subtitle { font-size: 18px; color: var(--cyber-cyan); font-family: var(--font-mono); margin-bottom: 16px; }
.sub-cursor { animation: blink 0.8s step-end infinite; color: var(--cyber-cyan); margin-left: 2px; }

.hero-desc { font-size: 15px; color: var(--text-secondary); max-width: 480px; margin: 0 auto 24px; line-height: 1.6; }

/* Stats */
.hero-stats { display: flex; justify-content: center; gap: 32px; margin-bottom: 28px; }
.hs-item { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.hs-num { font-size: 24px; font-weight: 800; color: var(--cyber-cyan); font-family: var(--font-mono); font-variant-numeric: tabular-nums; }
.hs-lbl { font-size: 11px; color: var(--text-tertiary); text-transform: uppercase; letter-spacing: 1px; font-weight: 600; }

/* Actions */
.hero-actions { display: flex; justify-content: center; gap: 10px; }
.ha-btn { width: 42px; height: 42px; display: flex; align-items: center; justify-content: center; border-radius: 10px; border: 1px solid rgba(255,255,255,0.08); color: var(--text-tertiary); transition: all 0.25s; background: rgba(255,255,255,0.02); }
.ha-btn:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.06); transform: translateY(-2px); }

/* Scroll Hint */
.scroll-hint { position: absolute; bottom: 32px; left: 50%; transform: translateX(-50%); display: flex; flex-direction: column; align-items: center; gap: 6px; opacity: 0.4; }
.sh-line { width: 1px; height: 32px; background: linear-gradient(to bottom, var(--cyber-cyan), transparent); animation: shPulse 2s ease-in-out infinite; }
.scroll-hint span { font-size: 9px; letter-spacing: 2px; color: var(--text-tertiary); font-family: var(--font-mono); }

/* ═══ BODY ═══ */
.ab-body { position: relative; z-index: 3; padding: 0 0 80px; }
.ab-grid { display: flex; gap: 24px; max-width: 900px; margin: 0 auto; }
.ab-left { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 24px; }
.ab-right { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 24px; }

/* Card */
.ab-card { background: rgba(24,27,36,0.6); backdrop-filter: blur(12px); border: 1px solid rgba(255,255,255,0.05); border-radius: 14px; padding: 24px; transition: all 0.4s; }
.ab-card:hover { border-color: rgba(0,240,255,0.1); box-shadow: 0 8px 32px rgba(0,0,0,0.2); }
.ab-card-header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; padding-bottom: 14px; border-bottom: 1px solid rgba(255,255,255,0.05); }
.ach-icon { width: 32px; height: 32px; border-radius: 8px; background: color-mix(in srgb, var(--ic) 12%, transparent); color: var(--ic); display: flex; align-items: center; justify-content: center; }
.ach-title { font-size: 14px; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.8px; }

/* ── Skills Ring ── */
.skills-grid { display: flex; flex-wrap: wrap; gap: 20px; justify-content: center; }
.sk-item { display: flex; flex-direction: column; align-items: center; gap: 6px; width: 90px; }
.sk-ring { width: 72px; height: 72px; transform: rotate(-90deg); }
.sk-ring-bg { fill: none; stroke: rgba(255,255,255,0.04); stroke-width: 6; }
.sk-ring-fg { fill: none; stroke: var(--cyber-cyan); stroke-width: 6; stroke-linecap: round; transition: stroke-dashoffset 1.5s cubic-bezier(0.23, 1, 0.32, 1); filter: drop-shadow(0 0 4px rgba(0,240,255,0.2)); }
.sk-label { font-size: 12px; font-weight: 600; color: var(--text-primary); }

/* ── Timeline ── */
.mile-line { position: relative; padding: 0 0 0 28px; }
.mile-line::before { content: ''; position: absolute; left: 10px; top: 4px; bottom: 4px; width: 2px; background: linear-gradient(to bottom, var(--cyber-cyan), var(--cyber-purple), transparent); opacity: 0.3; }
.ml-item { position: relative; margin-bottom: 20px; padding: 14px; border-radius: 10px; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.03); transition: all 0.3s; }
.ml-item:hover { border-color: rgba(124,58,237,0.15); background: rgba(124,58,237,0.02); transform: translateX(6px); }
.ml-dot { position: absolute; left: -22px; top: 18px; width: 14px; height: 14px; border-radius: 50%; background: var(--bg-deep); border: 2px solid var(--cyber-purple); display: flex; align-items: center; justify-content: center; }
.ml-dot-inner { width: 6px; height: 6px; border-radius: 50%; background: var(--cyber-purple); box-shadow: 0 0 8px rgba(124,58,237,0.4); }
.ml-body { }
.ml-year { font-size: 11px; font-weight: 700; color: var(--cyber-cyan); font-family: var(--font-mono); }
.ml-body h4 { font-size: 14px; font-weight: 600; color: var(--text-primary); margin: 4px 0 2px; }
.ml-body p { font-size: 12px; color: var(--text-tertiary); line-height: 1.5; }

/* ── Projects ── */
.proj-list { display: flex; flex-direction: column; gap: 10px; }
.pj-card { display: flex; align-items: center; gap: 12px; padding: 14px; border-radius: 10px; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.03); transition: all 0.3s cubic-bezier(0.23,1,0.32,1); }
.pj-card:hover { border-color: rgba(16,185,129,0.15); background: rgba(16,185,129,0.02); transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.15); }
.pj-icon { width: 44px; height: 44px; border-radius: 10px; background: rgba(16,185,129,0.08); border: 1px solid rgba(16,185,129,0.1); color: var(--cyber-green); display: flex; align-items: center; justify-content: center; font-size: 18px; font-weight: 700; font-family: var(--font-mono); flex-shrink: 0; }
.pj-body { flex: 1; min-width: 0; }
.pj-body h3 { font-size: 14px; font-weight: 600; color: var(--text-primary); margin-bottom: 2px; }
.pj-body p { font-size: 12px; color: var(--text-tertiary); line-height: 1.5; }
.pj-link { width: 34px; height: 34px; display: flex; align-items: center; justify-content: center; border-radius: 8px; color: var(--text-tertiary); flex-shrink: 0; transition: all 0.2s; }
.pj-link:hover { color: var(--cyber-green); background: rgba(16,185,129,0.06); }

/* ── Scroll animations ── */
.ab-hidden { opacity: 0; transform: translateY(30px); transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1); }
.ab-visible { opacity: 1; transform: translateY(0); }
.ab-card { transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1); }

/* ═══ KEYFRAMES ═══ */

@keyframes orbDrift { 0%,100% { transform: translate(0,0); } 50% { transform: translate(30px,-20px); } }
@keyframes glitch1 { 0%,92%,100% { opacity: 0; transform: translate(0); } 93% { opacity: 0.7; transform: translate(-3px,2px); } 95% { opacity: 0; transform: translate(3px,-1px); } 97% { opacity: 0.5; transform: translate(-2px,1px); } }
@keyframes glitch2 { 0%,92%,100% { opacity: 0; transform: translate(0); } 94% { opacity: 0.5; transform: translate(3px,-1px); } 96% { opacity: 0; transform: translate(-3px,2px); } 98% { opacity: 0.6; transform: translate(2px,-2px); } }
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0; } }
@keyframes avaGlow { 0% { opacity: 0.4; transform: scale(1); } 100% { opacity: 0.8; transform: scale(1.15); } }
@keyframes shPulse { 0%,100% { transform: scaleY(0.5); opacity: 0.4; } 50% { transform: scaleY(1); opacity: 0.8; } }

@media (max-width: 768px) {
  .ab-grid { flex-direction: column; }
  .hero-stats { gap: 20px; }
  .hs-num { font-size: 20px; }
}
</style>
