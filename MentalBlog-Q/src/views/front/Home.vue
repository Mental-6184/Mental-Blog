<template>
  <div class="home-page">
    <!-- ═══════════════════════════════
         HERO — Fullscreen Immersive
         ═══════════════════════════════ -->
    <section class="hero-section">
      <!-- Background layers -->
      <canvas ref="particleCanvas" class="hero-particle-canvas"></canvas>
      <div class="hero-grid"></div>
      <div class="hero-vignette"></div>

      <!-- Floating code snippets — decorative -->
      <div class="floating-snippets" aria-hidden="true">
        <div class="frag frag-1"><span class="fg-fn">async</span> <span class="fg-fn">function</span> <span class="fg-fn-name">build</span>() {<br>&nbsp;&nbsp;<span class="fg-fn">await</span> <span class="fg-fn-name">deploy</span>(<span class="fg-str">'prod'</span>)<br>}</div>
        <div class="frag frag-2"><span class="fg-cmt">// event loop</span><br><span class="fg-fn">setInterval</span>(<span class="fg-fn-name">tick</span>, 16)</div>
        <div class="frag frag-3">&lt;<span class="fg-tag">Layout</span>&gt;<br>&nbsp;&nbsp;&lt;<span class="fg-tag">Hero</span> /&gt;<br>&lt;/<span class="fg-tag">Layout</span>&gt;</div>
        <div class="frag frag-4"><span class="fg-cmt">/* database */</span><br><span class="fg-fn">SELECT</span> * <span class="fg-fn">FROM</span> articles<br><span class="fg-fn">WHERE</span> published = 1</div>
        <div class="frag frag-5">{<br>&nbsp;&nbsp;<span class="fg-str">"status"</span>: <span class="fg-val">200</span>,<br>&nbsp;&nbsp;<span class="fg-str">"message"</span>: <span class="fg-str">"ok"</span><br>}</div>
        <div class="frag frag-6"><span class="fg-cmt"># config</span><br>server:<br>&nbsp;&nbsp;port: <span class="fg-val">8080</span><br>&nbsp;&nbsp;name: <span class="fg-str">mentalblog</span></div>
      </div>

      <!-- Central content -->
      <div class="hero-center">
        <!-- Left: Terminal -->
        <div ref="leftPanel" class="hero-terminal">
          <div class="term-header">
            <div class="term-dots"><span></span><span></span><span></span></div>
            <span class="term-tab">terminal</span>
            <span class="term-tab active">bash</span>
            <span class="term-tab">+</span>
            <span class="term-actions">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
            </span>
          </div>
          <div class="term-body">
            <div v-for="(l, i) in lines" :key="i" class="term-line">
              <span class="prompt" v-if="l.t !== ''">{{ l.t === 'cmd' ? '❯' : ' ' }}</span>
              <span :class="'c-' + l.t" v-html="l.v"></span>
            </div>
            <span class="cursor">▌</span>
          </div>
        </div>

        <!-- Center: 3D Scene -->
        <div class="hero-core">
          <canvas ref="threeCanvas" class="core-canvas"></canvas>
          <!-- Ornamental rings -->
          <div class="core-rings" aria-hidden="true">
            <div class="cr cr-1"></div>
            <div class="cr cr-2"></div>
            <div class="cr cr-3"></div>
          </div>
          <!-- Glow behind core -->
          <div class="core-glow"></div>
        </div>

        <!-- Right: Stats + Title -->
        <div ref="rightPanel" class="hero-info">
          <div class="hi-title-group">
            <div class="hi-pre-title">
              <span class="hi-line"></span>
              <span class="hi-pre-text">WELCOME TO</span>
              <span class="hi-line"></span>
            </div>
            <h1 class="hi-title">
              <span class="hi-t-1">Mental</span><span class="hi-t-2">Blog</span>
            </h1>
            <p class="hi-subtitle">A digital space for thoughts, code, and creativity.</p>
          </div>

          <div class="hi-stats">
            <div class="his-item">
              <div class="his-ring">
                <svg width="52" height="52" viewBox="0 0 52 52">
                  <circle cx="26" cy="26" r="23" fill="none" stroke="rgba(0,240,255,0.08)" stroke-width="2"/>
                  <circle cx="26" cy="26" r="23" fill="none" stroke="var(--cyber-cyan)" stroke-width="2"
                    stroke-dasharray="145" :stroke-dashoffset="145 - (145 * articleCount / Math.max(articleCount || 1, 50))" stroke-linecap="round"
                    transform="rotate(-90 26 26)" style="transition: stroke-dashoffset 1.5s ease-out"/>
                </svg>
                <span class="his-num">{{ articleCount }}</span>
              </div>
              <span class="his-label">Articles</span>
            </div>
            <div class="his-item">
              <div class="his-ring">
                <svg width="52" height="52" viewBox="0 0 52 52">
                  <circle cx="26" cy="26" r="23" fill="none" stroke="rgba(124,58,237,0.08)" stroke-width="2"/>
                  <circle cx="26" cy="26" r="23" fill="none" stroke="var(--cyber-purple)" stroke-width="2"
                    stroke-dasharray="145" :stroke-dashoffset="145 - (145 * catCount / Math.max(catCount || 1, 20))" stroke-linecap="round"
                    transform="rotate(-90 26 26)" style="transition: stroke-dashoffset 1.5s ease-out"/>
                </svg>
                <span class="his-num p">{{ catCount }}</span>
              </div>
              <span class="his-label">Categories</span>
            </div>
            <div class="his-item">
              <div class="his-ring">
                <svg width="52" height="52" viewBox="0 0 52 52">
                  <circle cx="26" cy="26" r="23" fill="none" stroke="rgba(236,72,153,0.08)" stroke-width="2"/>
                  <circle cx="26" cy="26" r="23" fill="none" stroke="var(--cyber-pink)" stroke-width="2"
                    stroke-dasharray="145" :stroke-dashoffset="145 - (145 * tagCount / Math.max(tagCount || 1, 30))" stroke-linecap="round"
                    transform="rotate(-90 26 26)" style="transition: stroke-dashoffset 1.5s ease-out"/>
                </svg>
                <span class="his-num pk">{{ tagCount }}</span>
              </div>
              <span class="his-label">Tags</span>
            </div>
          </div>

          <div class="hi-actions">
            <router-link to="/archives" class="hero-btn hero-btn-primary">
              <span>Explore Articles</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </router-link>
            <router-link to="/about" class="hero-btn hero-btn-ghost">
              <span>About Me</span>
            </router-link>
          </div>
        </div>
      </div>

      <!-- Scroll indicator -->
      <div class="scroll-hint">
        <div class="sh-line"></div>
        <span class="sh-text">SCROLL</span>
        <svg class="sh-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
      </div>
    </section>

    <!-- ═══════════════════════════════
         FEED — Article List
         ═══════════════════════════════ -->
    <div class="main-section container">
      <div class="content-area">
        <!-- Section header -->
        <div class="feed-header">
          <div class="fh-left">
            <h2 class="fh-title">Latest Articles</h2>
            <div class="fh-line"></div>
          </div>
          <router-link to="/archives" class="fh-link">
            View all
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
          </router-link>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="skeleton-feed">
          <div class="sk-card" v-for="i in 5" :key="i">
            <div class="sk-thumb"></div>
            <div class="sk-body">
              <div class="sk-line w30"></div>
              <div class="sk-line w80"></div>
              <div class="sk-line w60"></div>
            </div>
          </div>
        </div>

        <!-- Empty -->
        <div v-else-if="!articles.length" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          <p>No articles published yet.</p>
        </div>

        <!-- Articles -->
        <div v-else ref="feedRef" class="feed-list">
          <article
            v-for="(a, idx) in articles"
            :key="a.id"
            :ref="el => setCardRef(el, idx)"
            class="feed-item"
            @click="$router.push(`/article/${a.id}`)"
          >
            <div class="fi-thumb">
              <img :src="a.coverImage || dc" :alt="a.title" loading="lazy" />
              <div class="fi-thumb-overlay">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
              </div>
            </div>
            <div class="fi-body">
              <div class="fi-meta">
                <span v-if="a.categoryName" class="fi-cat">{{ a.categoryName }}</span>
                <span class="fi-date">{{ formatDate(a.publishedAt) }}</span>
                <span class="fi-read" v-if="a.content">{{ Math.max(1, Math.ceil((a.content.replace(/<[^>]*>/g,'').length||0)/500)) }} min read</span>
              </div>
              <h2 class="fi-title">{{ a.title }}</h2>
              <p class="fi-desc" v-if="a.summary">{{ a.summary }}</p>
              <div class="fi-foot">
                <div class="fi-tags" v-if="a.tags?.length">
                  <span class="fi-tag" v-for="t in a.tags.slice(0, 3)" :key="t">#{{ t }}</span>
                </div>
                <div class="fi-stats">
                  <span class="fis-item" title="Views">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    {{ fmtNum(a.viewCount) }}
                  </span>
                  <span class="fis-item" title="Likes">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
                    {{ fmtNum(a.likeCount) }}
                  </span>
                  <span class="fis-item" title="Comments">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                    {{ fmtNum(a.commentCount) }}
                  </span>
                </div>
              </div>
            </div>
          </article>
        </div>

        <!-- Pagination -->
        <div class="feed-pagination" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="cp"
            :page-size="ps"
            :total="total"
            layout="prev, pager, next"
            background
            @current-change="loadArticles"
          />
        </div>
      </div>

      <!-- Sidebar -->
      <Sidebar context="home" :hot-articles="hotArts" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, onActivated, onDeactivated, nextTick } from 'vue'

import { getArticles } from '@/api/article'
import { getCategories, getTags } from '@/api/category'
import Sidebar from '@/components/front/Sidebar.vue'

const particleCanvas = ref<HTMLCanvasElement | null>(null)
const threeCanvas = ref<HTMLCanvasElement | null>(null)
const leftPanel = ref<HTMLElement | null>(null)
const rightPanel = ref<HTMLElement | null>(null)
const feedRef = ref<HTMLElement | null>(null)
const cardRefs = ref<(HTMLElement | null)[]>([])
const setCardRef = (el: any, i: number) => { if (el) cardRefs.value[i] = el as HTMLElement }

const articles = ref<any[]>([])
const hotArts = ref<any[]>([])
const cp = ref(1)
const ps = ref(10)
const total = ref(0)
const loading = ref(false)
const articleCount = ref(0)
const catCount = ref(0)
const tagCount = ref(0)
const initialized = ref(false)
const dc = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="180" fill="%23181B24"%3E%3Crect width="300" height="180"/%3E%3C/svg%3E'

// ── Terminal boot sequence ──
const lines = ref<{ v: string; t: string }[]>([])
function bootTerminal() {
  const seq = [
    { v: 'ssh mentalblog@server', t: 'cmd' },
    { v: 'Authenticated with public key.', t: 'out' },
    { v: '', t: '' },
    { v: 'cat /etc/motd', t: 'cmd' },
    { v: '█▀▄▀█ █▀▀ █▀▀▄ ▀▀█▀▀ █▀▀█ █░░  █▀▀▄ █░░ █▀▀█ █▀▀▀', t: 'ok' },
    { v: '█░▀░█ █▀▀ █░░█ ░░█░░ █▄▄█ █░░  █▀▀▄ █░░ █░░█ █░▀█', t: 'ok' },
    { v: '▀░░░▀ ▀▀▀ ▀░░▀ ░░▀░░ ▀░░▀ ▀▀▀  ▀▀▀░ ▀▀▀ ▀▀▀▀ ▀▀▀▀', t: 'ok' },
    { v: '', t: '' },
    { v: 'neofetch --ascii_distro mentalos', t: 'cmd' },
    { v: 'OS: MentalOS x86_64', t: 'out' },
    { v: 'Host: Spring Boot 4.0', t: 'out' },
    { v: 'Kernel: Vue 3 + TypeScript', t: 'out' },
    { v: 'Shell: Bash 5.2', t: 'out' },
    { v: 'Uptime: 365 days, 4 hours, 12 mins', t: 'out' },
    { v: 'Packages: 147 (npm), 38 (maven)', t: 'out' },
    { v: '', t: '' },
    { v: 'node --eval "require(\'./status\').check()"', t: 'cmd' },
    { v: '✓ Database: connected', t: 'ok' },
    { v: '✓ Redis: connected', t: 'ok' },
    { v: '✓ CDN: online', t: 'ok' },
    { v: '', t: '' },
    { v: '█ System ready. Awaiting input.', t: 'ok' },
  ]
  lines.value = []
  seq.forEach((l, i) => setTimeout(() => lines.value.push(l), 120 + i * 180))
}

// ── Particle Network (replaces simple dots) ──
let particleClean: (() => void) | null = null
function initParticleNetwork() {
  const c = particleCanvas.value
  if (!c) return
  c.width = window.innerWidth
  c.height = window.innerHeight

  const ctx = c.getContext('2d')
  if (!ctx) return

  const count = 50
  const nodes: { x: number; y: number; vx: number; vy: number; r: number; o: number }[] = []
  for (let i = 0; i < count; i++) {
    nodes.push({
      x: Math.random() * c.width,
      y: Math.random() * c.height,
      vx: (Math.random() - 0.5) * 0.3,
      vy: (Math.random() - 0.5) * 0.3,
      r: Math.random() * 1.5 + 0.5,
      o: Math.random() * 0.5 + 0.2,
    })
  }

  let id: number
  let mouseX = c.width / 2
  let mouseY = c.height / 2

  const onMouse = (e: MouseEvent) => { mouseX = e.clientX; mouseY = e.clientY }
  window.addEventListener('mousemove', onMouse)

  function draw() {
    ctx!.clearRect(0, 0, c!.width, c!.height)

    for (let i = 0; i < nodes.length; i++) {
      const n = nodes[i]

      // Move
      n.x += n.vx
      n.y += n.vy
      if (n.x < 0 || n.x > c!.width) n.vx *= -1
      if (n.y < 0 || n.y > c!.height) n.vy *= -1

      // Draw node
      ctx!.beginPath()
      ctx!.arc(n.x, n.y, n.r, 0, Math.PI * 2)
      ctx!.fillStyle = `rgba(0,240,255,${n.o})`
      ctx!.fill()

      // Draw connections to nearby nodes
      for (let j = i + 1; j < nodes.length; j++) {
        const n2 = nodes[j]
        const dx = n.x - n2.x
        const dy = n.y - n2.y
        const dist = Math.sqrt(dx * dx + dy * dy)

        if (dist < 100) {
          const alpha = (1 - dist / 150) * 0.12
          ctx!.beginPath()
          ctx!.moveTo(n.x, n.y)
          ctx!.lineTo(n2.x, n2.y)
          ctx!.strokeStyle = `rgba(0,240,255,${alpha})`
          ctx!.lineWidth = 0.5
          ctx!.stroke()
        }

        // Mouse attraction lines
        const dmx = (n.x + n2.x) / 2 - mouseX
        const dmy = (n.y + n2.y) / 2 - mouseY
        const dm = Math.sqrt(dmx * dmx + dmy * dmy)
        if (dm < 200 && dist < 150) {
          const alpha = (1 - dm / 200) * (1 - dist / 150) * 0.25
          ctx!.beginPath()
          ctx!.moveTo((n.x + n2.x) / 2, (n.y + n2.y) / 2)
          ctx!.lineTo(mouseX, mouseY)
          ctx!.strokeStyle = `rgba(0,240,255,${alpha})`
          ctx!.lineWidth = 0.3
          ctx!.stroke()
        }
      }
    }
    id = requestAnimationFrame(draw)
  }
  id = requestAnimationFrame(draw)

  const resize = () => {
    if (!c) return
    c.width = window.innerWidth
    c.height = window.innerHeight
  }
  window.addEventListener('resize', resize)
  particleClean = () => {
    cancelAnimationFrame(id)
    window.removeEventListener('mousemove', onMouse)
    window.removeEventListener('resize', resize)
  }
}

// ── 3D Core Scene (larger, richer) ──
let coreClean: (() => void) | null = null
async function initCore3D() {
  const THREE = await import('three')
  const canvas = threeCanvas.value
  if (!canvas) return

  const scene = new THREE.Scene()
  const cam = new THREE.PerspectiveCamera(40, 1, 0.1, 100)
  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true })
  renderer.setSize(300, 300)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))

  // Main icosahedron (wireframe)
  const coreGeo = new THREE.IcosahedronGeometry(1.2, 1)
  const coreMat = new THREE.MeshBasicMaterial({ color: 0x00f0ff, wireframe: true, transparent: true, opacity: 0.45 })
  const core = new THREE.Mesh(coreGeo, coreMat)
  scene.add(core)

  // Inner icosahedron (solid-ish)
  const innerGeo = new THREE.IcosahedronGeometry(0.75, 0)
  const innerMat = new THREE.MeshBasicMaterial({ color: 0x7c3aed, wireframe: true, transparent: true, opacity: 0.25 })
  const inner = new THREE.Mesh(innerGeo, innerMat)
  scene.add(inner)

  // Dots at vertices
  const dotGeo = new THREE.SphereGeometry(0.03, 6, 6)
  const dotMat = new THREE.MeshBasicMaterial({ color: 0x00f0ff, transparent: true, opacity: 0.8 })
  const positions = (coreGeo.attributes.position as THREE.BufferAttribute).array
  for (let i = 0; i < positions.length; i += 3) {
    const dot = new THREE.Mesh(dotGeo, dotMat)
    dot.position.set(positions[i] * 1.2, positions[i + 1] * 1.2, positions[i + 2] * 1.2)
    core.add(dot)
  }

  // Outer ring 1
  const ringGeo1 = new THREE.TorusGeometry(1.65, 0.015, 8, 64)
  const ringMat1 = new THREE.MeshBasicMaterial({ color: 0x00f0ff, transparent: true, opacity: 0.3 })
  const ring1 = new THREE.Mesh(ringGeo1, ringMat1)
  ring1.rotation.x = Math.PI / 2.5
  scene.add(ring1)

  // Outer ring 2 (tilted)
  const ringGeo2 = new THREE.TorusGeometry(1.85, 0.01, 8, 48)
  const ringMat2 = new THREE.MeshBasicMaterial({ color: 0x7c3aed, transparent: true, opacity: 0.18 })
  const ring2 = new THREE.Mesh(ringGeo2, ringMat2)
  ring2.rotation.x = Math.PI / 3.5
  ring2.rotation.y = Math.PI / 5
  scene.add(ring2)

  // Outer ring 3 (horizontal)
  const ringGeo3 = new THREE.TorusGeometry(2.1, 0.008, 6, 36)
  const ringMat3 = new THREE.MeshBasicMaterial({ color: 0xec4899, transparent: true, opacity: 0.12 })
  const ring3 = new THREE.Mesh(ringGeo3, ringMat3)
  ring3.rotation.x = Math.PI / 2
  scene.add(ring3)

  cam.position.z = 5

  let id: number
  function anim() {
    const t = performance.now() * 0.001
    core.rotation.y += 0.006
    core.rotation.x = Math.sin(t * 0.3) * 0.15
    inner.rotation.y -= 0.009
    inner.rotation.z += 0.004
    ring1.rotation.z += 0.004
    ring1.rotation.y += 0.002
    ring2.rotation.y += 0.003
    ring2.rotation.x += 0.002
    ring3.rotation.z += 0.002

    // Subtle camera movement
    cam.position.x = Math.sin(t * 0.2) * 0.2
    cam.position.y = Math.cos(t * 0.25) * 0.15
    cam.lookAt(0, 0, 0)

    renderer.render(scene, cam)
    id = requestAnimationFrame(anim)
  }
  id = requestAnimationFrame(anim)

  coreClean = () => {
    cancelAnimationFrame(id)
    renderer.dispose()
  }
}

// ── Helpers ──
function fmtNum(n: number) {
  if (!n) return '0'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

const formatDate = (s: string) => {
  if (!s) return ''
  const d = new Date(s)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const loadArticles = async () => {
  loading.value = true
  try {
    const r = await getArticles({ page: cp.value, pageSize: ps.value })
    articles.value = (r as any).records || (r as any).data || []
    total.value = (r as any).total || 0
    hotArts.value = articles.value.slice(0, 5)
  } catch (e) {
    /* ignore */
  }
  loading.value = false
}

// ── Animations ──
async function runAnim() {
  const { default: gsap } = await import('gsap')
  const { default: ScrollTrigger } = await import('gsap/ScrollTrigger')
  gsap.registerPlugin(ScrollTrigger)
  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })
  if (leftPanel.value) tl.from(leftPanel.value, { opacity: 0, x: -60, duration: 0.9 }, 0.1)
  if (rightPanel.value) tl.from(rightPanel.value, { opacity: 0, x: 60, duration: 0.9 }, 0.3)

  // Hero stats ring animation
  tl.from('.his-item', { opacity: 0, y: 20, duration: 0.5, stagger: 0.1 }, 0.6)
  tl.from('.hi-actions', { opacity: 0, y: 16, duration: 0.4 }, 0.8)

  // Floating fragments
  gsap.from('.frag', {
    opacity: 0,
    y: 30,
    duration: 1,
    stagger: 0.15,
    ease: 'power3.out',
    delay: 0.5,
  })

  // Feed card scroll animations
  nextTick(() => {
    cardRefs.value = cardRefs.value.filter(Boolean)
    cardRefs.value.forEach((c, i) => {
      if (!c) return
      gsap.from(c, {
        opacity: 0,
        y: 24,
        duration: 0.4,
        delay: i * 0.05,
        scrollTrigger: { trigger: c, start: 'top 90%' },
      })
    })
  })
}

// ── Lifecycle ──
onMounted(async () => {
    if (initialized.value) { initParticleNetwork(); await initCore3D(); bootTerminal(); await runAnim(); return }
    initialized.value = true
  try {
    const [cRes, tRes] = await Promise.all([getCategories(), getTags()])
    catCount.value = (cRes as any).data?.length || 0
    tagCount.value = (tRes as any).data?.length || 0
  } catch (e) {
    /* ignore */
  }
  await loadArticles()
  articleCount.value = total.value

  nextTick(async () => {
    initParticleNetwork()
    await initCore3D()
    bootTerminal()
    await runAnim()
  })
})

onDeactivated(() => {
    particleClean?.(); particleClean = null;
    coreClean?.(); coreClean = null;
  })

onActivated(() => {
    // 切回首页时恢复画布，同时做轻量 fade-in
    const hero = document.querySelector(".hero-section")
    if (hero) {
      hero.style.opacity = "0.85"
      hero.style.transition = "opacity 0.25s ease-out"
      requestAnimationFrame(() => { hero.style.opacity = "1" })
    }
    if (initialized.value) {
      initParticleNetwork()
      initCore3D()
    }
  })

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
  particleClean?.()
  coreClean?.()
})
</script>

<style scoped>
/* ═══════════════════════════
   HERO SECTION
   ═══════════════════════════ */
.hero-section {
  position: relative; min-height: 100vh; overflow: hidden;
  background: #07070e;
  display: flex; align-items: center; justify-content: center;
}
.hero-particle-canvas { position: absolute; inset: 0; z-index: 1; }
.hero-grid {
  position: absolute; inset: 0; z-index: 0;
  background-image:
    linear-gradient(rgba(0,240,255,0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,240,255,0.02) 1px, transparent 1px);
  background-size: 64px 64px;
}
.hero-vignette {
  position: absolute; inset: 0; z-index: 2; pointer-events: none;
  background: radial-gradient(ellipse at 50% 50%, transparent 50%, rgba(7,7,14,0.7) 100%);
}

/* Floating code snippets */
.floating-snippets { position: absolute; inset: 0; z-index: 3; pointer-events: none; }
.frag {
  position: absolute; font-family: var(--font-mono); font-size: 12px; line-height: 1.6;
  color: rgba(255,255,255,0.18); white-space: pre;
  text-shadow: 0 0 8px rgba(0,240,255,0.1);
  animation: fragFloat 8s ease-in-out infinite;
}
.frag-1 { top: 12%; left: 6%; animation-delay: 0s; }
.frag-2 { top: 22%; right: 8%; animation-delay: 1.5s; }
.frag-3 { top: 55%; left: 3%; animation-delay: 3s; font-size: 11px; }
.frag-4 { top: 65%; right: 5%; animation-delay: 4.5s; }
.frag-5 { bottom: 18%; left: 10%; animation-delay: 2s; }
.frag-6 { bottom: 22%; right: 12%; animation-delay: 5s; }
@keyframes fragFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}
.fg-fn { color: rgba(0,240,255,0.25); }
.fg-fn-name { color: rgba(124,58,237,0.25); }
.fg-str { color: rgba(16,185,129,0.25); }
.fg-cmt { color: rgba(100,116,139,0.25); }
.fg-tag { color: rgba(236,72,153,0.25); }
.fg-val { color: rgba(245,158,11,0.25); }

/* === Central Layout === */
.hero-center {
  position: relative; z-index: 4; display: flex;
  align-items: center; justify-content: center; gap: 48px;
  max-width: 1100px; width: 100%; padding: 0 24px;
}

/* ── Terminal ── */
.hero-terminal { flex: 0 0 340px; will-change: transform, opacity; background: rgba(5,5,12,0.85); border: 1px solid rgba(0,240,255,0.1); border-radius: 10px; overflow: hidden; backdrop-filter: blur(8px); box-shadow: 0 8px 40px rgba(0,0,0,0.5), 0 0 0 1px rgba(0,240,255,0.03); }
.term-header { display: flex; align-items: center; gap: 6px; padding: 8px 14px; background: rgba(255,255,255,0.02); border-bottom: 1px solid rgba(0,240,255,0.06); }
.term-dots { display: flex; gap: 5px; margin-right: 4px; }
.term-dots span { width: 8px; height: 8px; border-radius: 50%; }
.term-dots span:nth-child(1) { background: #FF5F57; }
.term-dots span:nth-child(2) { background: #FEBC2E; }
.term-dots span:nth-child(3) { background: #28C840; }
.term-tab {
  font-size: 10px; color: rgba(255,255,255,0.2); padding: 3px 10px;
  border-radius: 4px; font-family: var(--font-mono); cursor: default;
}
.term-tab.active { background: rgba(255,255,255,0.06); color: rgba(255,255,255,0.5); }
.term-actions { margin-left: auto; color: rgba(255,255,255,0.15); cursor: pointer; display: flex; align-items: center; }
.term-body { padding: 14px 18px; font-family: var(--font-mono); font-size: 12px; line-height: 1.65; min-height: 280px; max-height: 400px; overflow-y: auto; }
.term-body::-webkit-scrollbar { width: 3px; }
.term-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.05); }
.prompt { color: var(--cyber-cyan); margin-right: 6px; opacity: 0.8; }
.c-cmd { color: var(--cyber-cyan); }
.c-out { color: var(--text-secondary); }
.c-ok { color: var(--cyber-green); }
.cursor { color: var(--cyber-cyan); animation: blink 0.8s step-end infinite; }
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0; } }

/* ── 3D Core ── */
.hero-core { flex: 0 0 300px; position: relative; display: flex; align-items: center; justify-content: center; }
.core-canvas { width: 300px; height: 300px; position: relative; z-index: 1; border-radius: 50%; }

/* Decorative CSS rings behind canvas */
.core-rings { position: absolute; inset: 0; z-index: 0; }
.cr { position: absolute; border-radius: 50%; border: 1px solid; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.cr-1 { width: 280px; height: 280px; border-color: rgba(0,240,255,0.06); animation: ringPulse 4s ease-in-out infinite; }
.cr-2 { width: 320px; height: 320px; border-color: rgba(124,58,237,0.04); animation: ringPulse 4s ease-in-out 0.5s infinite; }
.cr-3 { width: 260px; height: 260px; border-color: rgba(236,72,153,0.04); border-style: dashed; animation: ringPulse 4s ease-in-out 1s infinite rotate; }
@keyframes ringPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.08); opacity: 1; }
}
@keyframes rotate { to { transform: translate(-50%, -50%) rotate(360deg); } }

/* Glow behind the core */
.core-glow {
  position: absolute; width: 200px; height: 200px; border-radius: 50%;
  background: radial-gradient(circle, rgba(0,240,255,0.08) 0%, transparent 70%);
  z-index: 0; pointer-events: none;
}

/* ── Right Info ── */
.hero-info { flex: 1; will-change: transform, opacity; display: flex; flex-direction: column; gap: 24px; min-width: 0; }

.hi-title-group { }
.hi-pre-title { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.hi-line { flex: 1; height: 1px; background: linear-gradient(90deg, transparent, rgba(0,240,255,0.15), transparent); }
.hi-pre-text { font-size: 10px; font-weight: 700; color: var(--cyber-cyan); letter-spacing: 3px; font-family: var(--font-mono); }

.hi-title { font-size: clamp(42px, 6vw, 64px); font-weight: 900; line-height: 1; margin-bottom: 8px; }
.hi-t-1 { background: linear-gradient(135deg, var(--cyber-cyan), #06B6D4); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.hi-t-2 { color: #fff; margin-left: 8px; }
.hi-subtitle { font-size: 14px; color: var(--text-tertiary); line-height: 1.5; }

/* Stats rings */
.hi-stats { display: flex; gap: 24px; }
.his-item { display: flex; flex-direction: column; align-items: center; gap: 6px; }
.his-ring { position: relative; display: flex; align-items: center; justify-content: center; }
.his-num { position: absolute; font-size: 15px; font-weight: 800; font-family: var(--font-mono); color: var(--cyber-cyan); }
.his-num.p { color: var(--cyber-purple); }
.his-num.pk { color: var(--cyber-pink); }
.his-label { font-size: 10px; font-weight: 600; color: var(--text-tertiary); text-transform: uppercase; letter-spacing: 1px; }

/* Actions */
.hi-actions { display: flex; gap: 10px; }
.hero-btn {
  display: inline-flex; align-items: center; gap: 8px; padding: 11px 24px;
  font-size: 13px; font-weight: 600; border-radius: 8px; cursor: pointer;
  text-decoration: none; transition: all 0.25s; font-family: var(--font-sans);
}
.hero-btn-primary {
  background: var(--cyber-cyan); color: #0A0A0F;
  box-shadow: 0 0 20px rgba(0,240,255,0.15);
}
.hero-btn-primary:hover {
  box-shadow: 0 0 32px rgba(0,240,255,0.3);
  transform: translateY(-1px);
  color: #0A0A0F;
}
.hero-btn-ghost {
  background: transparent; border: 1px solid rgba(255,255,255,0.12);
  color: var(--text-secondary);
}
.hero-btn-ghost:hover { border-color: var(--cyber-cyan); color: var(--cyber-cyan); background: rgba(0,240,255,0.04); }

/* Scroll hint */
.scroll-hint {
  position: absolute; bottom: 28px; left: 50%; transform: translateX(-50%);
  z-index: 4; display: flex; flex-direction: column; align-items: center; gap: 6px;
}
.sh-line { width: 1px; height: 32px; background: linear-gradient(to bottom, rgba(0,240,255,0.3), transparent); }
.sh-text { font-size: 9px; font-weight: 700; color: rgba(255,255,255,0.15); letter-spacing: 3px; font-family: var(--font-mono); }
.sh-arrow { color: rgba(255,255,255,0.1); animation: arrowBounce 2s ease-in-out infinite; }
@keyframes arrowBounce { 0%,100% { transform: translateY(0); } 50% { transform: translateY(4px); } }

/* ═══════════════════════════
   FEED SECTION
   ═══════════════════════════ */
.main-section { display: flex; gap: 28px; padding-top: 40px; padding-bottom: 64px; }
.content-area { flex: 1; min-width: 0; }

/* Feed header */
.feed-header { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 24px; }
.fh-left { }
.fh-title { font-size: 20px; font-weight: 800; color: #fff; margin-bottom: 8px; }
.fh-line { width: 40px; height: 2px; background: var(--cyber-cyan); border-radius: 1px; }
.fh-link {
  display: flex; align-items: center; gap: 4px; font-size: 12px; font-weight: 500;
  color: var(--text-tertiary); text-decoration: none; font-family: var(--font-mono);
  transition: color 0.2s; padding-bottom: 4px;
}
.fh-link:hover { color: var(--cyber-cyan); }

/* Skeleton feed */
.skeleton-feed { display: flex; flex-direction: column; gap: 12px; }
.sk-card { display: flex; gap: 14px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 14px; }
.sk-thumb { width: 130px; height: 85px; background: var(--bg-surface); border-radius: 6px; animation: skShimmer 2s infinite; }
.sk-body { flex: 1; display: flex; flex-direction: column; gap: 8px; justify-content: center; }
.sk-line { height: 12px; background: var(--bg-surface); border-radius: 4px; animation: skShimmer 2s infinite; }
.sk-line.w30 { width: 30%; } .sk-line.w60 { width: 60%; } .sk-line.w80 { width: 80%; }
@keyframes skShimmer { 0%,100% { opacity: 0.3; } 50% { opacity: 0.6; } }

/* Feed list */
.feed-list { display: flex; flex-direction: column; gap: 12px; }
.feed-item {
  display: flex; background: var(--bg-elevated);
  border: 1px solid var(--border-subtle); border-radius: 10px;
  overflow: hidden; cursor: pointer; transition: all 0.3s;
}
.feed-item:hover {
  border-color: rgba(0,240,255,0.15);
  box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,240,255,0.04);
  transform: translateY(-2px);
}

.fi-thumb {
  width: 200px; min-height: 130px; flex-shrink: 0;
  overflow: hidden; background: var(--bg-surface); position: relative;
}
.fi-thumb img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.feed-item:hover .fi-thumb img { transform: scale(1.06); }
.fi-thumb-overlay {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center;
  background: rgba(0,0,0,0.4); color: #fff; opacity: 0; transition: opacity 0.3s;
}
.feed-item:hover .fi-thumb-overlay { opacity: 1; }

.fi-body { flex: 1; padding: 16px 20px; min-width: 0; display: flex; flex-direction: column; }
.fi-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; font-size: 11px; flex-wrap: wrap; }
.fi-cat {
  font-family: var(--font-mono); font-size: 10px; font-weight: 600;
  color: var(--cyber-cyan); padding: 2px 8px; border-radius: 4px;
  background: rgba(0,240,255,0.06); text-transform: uppercase; letter-spacing: 0.5px;
}
.fi-date { color: var(--text-tertiary); font-family: var(--font-mono); }
.fi-read { color: var(--text-tertiary); font-family: var(--font-mono); padding: 1px 6px; background: rgba(255,255,255,0.03); border-radius: 4px; }

.fi-title {
  font-size: 16px; font-weight: 700; line-height: 1.35; margin-bottom: 6px;
  overflow: hidden; max-height: 2.8em; line-height: 1.4;
  color: var(--text-primary); transition: color 0.2s;
}
.feed-item:hover .fi-title { color: var(--cyber-cyan); }

.fi-desc {
  font-size: 13px; color: var(--text-tertiary); line-height: 1.5; margin-bottom: auto;
  overflow: hidden; max-height: 2.8em; line-height: 1.4;
}

.fi-foot { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-top: 10px; }
.fi-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.fi-tag { font-size: 10px; font-weight: 500; color: var(--text-tertiary); }
.feed-item:hover .fi-tag { color: var(--text-secondary); }
.fi-stats { display: flex; gap: 12px; }
.fis-item { display: flex; align-items: center; gap: 3px; font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); }

/* Pagination */
.feed-pagination { display: flex; justify-content: center; margin-top: 32px; }

/* ═══════════════════════════
   RESPONSIVE
   ═══════════════════════════ */
@media (max-width: 1100px) {
  .hero-center { gap: 28px; }
  .hero-terminal { flex: 0 0 280px; }
  .hero-core { flex: 0 0 240px; }
  .core-canvas { width: 240px; height: 240px; }
  .hero-info { gap: 16px; }
  .hi-stats { gap: 16px; }
  .frag-1, .frag-4, .frag-5 { display: none; }
}

@media (max-width: 820px) {
  .hero-center { flex-direction: column; gap: 24px; padding: 40px 16px; }
  .hero-terminal { flex: 0 0 auto; width: 100%; max-width: 380px; }
  .term-body { max-height: 220px; }
  .hero-core { flex: 0 0 200px; }
  .core-canvas { width: 200px; height: 200px; }
  .hero-info { text-align: center; align-items: center; }
  .hi-pre-title { justify-content: center; }
  .hi-stats { justify-content: center; }
  .hi-actions { justify-content: center; }
  .floating-snippets { display: none; }
}

@media (max-width: 640px) {
  .main-section { flex-direction: column; }
  .feed-item { flex-direction: column; }
  .fi-thumb { width: 100%; height: 180px; min-height: auto; }
  .hero-terminal { max-width: 100%; }
  .hero-core { flex: 0 0 160px; }
  .core-canvas { width: 160px; height: 160px; }
}
</style>


