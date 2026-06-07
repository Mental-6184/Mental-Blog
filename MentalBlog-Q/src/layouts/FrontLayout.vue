<template>
  <div class="front-layout">
    <!-- ＝＝ Global Progress Bar ＝＝ -->
    <div class="global-progress" :style="{ width: progress + '%' }"></div>

    <!-- ════════════════
         NAVBAR — Cyber Command Center
         ════════════════ -->
    <header class="navbar" :class="{ 'navbar-scrolled': scrolled, 'navbar-hidden': !showNav }">
      <!-- Scan-line border (animated on scroll) -->
      <div class="navbar-scanline" :class="{ active: scrolled }"></div>

      <!-- Background glow orb -->
      <div class="navbar-glow-orb" v-if="scrolled"></div>

      <div class="navbar-inner container">
        <!-- ── Logo ── -->
        <router-link to="/" class="navbar-brand" @click="closeMobileMenu">
          <div class="brand-mark">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" class="brand-svg">
              <defs>
                <linearGradient id="lgGrad" x1="2" y1="2" x2="22" y2="22">
                  <stop offset="0%" stop-color="var(--cyber-cyan)"/>
                  <stop offset="100%" stop-color="var(--cyber-purple)"/>
                </linearGradient>
              </defs>
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                    stroke="url(#lgGrad)" stroke-width="1.5" fill="none" stroke-linejoin="round"/>
            </svg>
            <!-- Pulse ring -->
            <div class="brand-pulse" :class="{ pulsing: !scrolled }"></div>
          </div>
          <span class="brand-text">{{ siteName }}</span>
        </router-link>

        <!-- ── Desktop Nav Links ── -->
        <nav class="nav-links" ref="navLinksRef">
          <router-link to="/" class="nav-lk" active-class="active" :exact="true" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg></span>
            <span class="nl-label">Home</span>
          </router-link>
          <router-link to="/archives" class="nav-lk" active-class="active" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
            <span class="nl-label">Archives</span>
          </router-link>
          <router-link to="/tags" class="nav-lk" active-class="active" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg></span>
            <span class="nl-label">Tags</span>
          </router-link>
          <router-link to="/about" class="nav-lk" active-class="active" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
            <span class="nl-label">About</span>
          </router-link>
          <router-link to="/links" class="nav-lk" active-class="active" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg></span>
            <span class="nl-label">Links</span>
          </router-link>
          <router-link to="/gallery" class="nav-lk" active-class="active" @click="closeMobileMenu">
            <span class="nl-icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg></span>
            <span class="nl-label">Gallery</span>
          </router-link>
          <!-- Sliding indicator -->
          <div class="nav-indicator" :style="indicatorStyle" v-if="indicatorStyle"></div>
        </nav>

        <!-- ── Right Actions ── -->
        <div class="navbar-actions">
          <!-- Search (expandable) -->
          <div class="search-area" :class="{ 'search-open': searchOpen }">
            <form @submit.prevent="doSearch" class="search-form">
              <input
                ref="searchInputRef"
                v-model="searchQuery"
                type="text"
                class="search-input"
                placeholder="Search articles..."
                @blur="onSearchBlur"
                @keydown.escape="closeSearch"
              />
            </form>
            <button class="na-btn search-trigger" @click="toggleSearch" :aria-label="searchOpen ? 'Close search' : 'Open search'">
              <svg v-if="!searchOpen" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
              <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <!-- Theme toggle placeholder -->
          <button class="na-btn" @click="toggleTheme" aria-label="Toggle theme" title="Toggle theme">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
          </button>

          <span class="na-divider"></span>

          <!-- User / Auth -->
          <template v-if="userStore.token">
            <button class="user-dropdown-btn" @click="showUserMenu = !showUserMenu" ref="userBtnRef" aria-label="User menu">
              <span class="ud-avatar avatar avatar-sm">{{ displayName[0].toUpperCase() }}</span>
              <span class="ud-name">{{ displayName }}</span>
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" :class="{ rotated: showUserMenu }"><polyline points="6 9 12 15 18 9"/></svg>
            </button>
            <!-- Custom dropdown -->
            <Teleport to="body">
              <div v-if="showUserMenu" class="user-menu-overlay" @click="showUserMenu = false"></div>
              <div v-if="showUserMenu" class="user-menu-dropdown" :style="userMenuStyle">
                <div class="um-header">
                  <span class="ud-avatar avatar avatar-lg">{{ displayName[0].toUpperCase() }}</span>
                  <div>
                    <div class="um-name">{{ displayName }}</div>
                    <div class="um-email">{{ userStore.email || userStore.username || '' }}</div>
                  </div>
                </div>
                <div class="um-divider"></div>
                <button class="um-item" @click="$router.push('/user/profile'); showUserMenu = false">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  Profile
                </button>
                <button v-if="userStore.role === 'ADMIN'" class="um-item um-admin" @click="goToAdmin">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="9" y1="21" x2="9" y2="9"/></svg>
                  Admin Panel
                </button>
                <div class="um-divider"></div>
                <button class="um-item um-logout" @click="handleLogout">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                  Logout
                </button>
                <!-- Dropdown arrow -->
                <div class="um-arrow"></div>
              </div>
            </Teleport>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-ghost btn-sm">Login</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">Register</router-link>
          </template>

          <!-- Mobile hamburger -->
          <button class="na-btn hamburger-btn" @click="toggleMobileMenu" aria-label="Toggle menu" :class="{ active: mobileMenuOpen }">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <line v-if="!mobileMenuOpen" x1="3" y1="6" x2="21" y2="6"/>
              <line v-if="!mobileMenuOpen" x1="3" y1="12" x2="21" y2="12"/>
              <line v-if="!mobileMenuOpen" x1="3" y1="18" x2="21" y2="18"/>
              <line v-if="mobileMenuOpen" x1="18" y1="6" x2="6" y2="18"/>
              <line v-if="mobileMenuOpen" x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
      </div>
    </header>

    <!-- ════════════════
         SECONDARY CATEGORY BAR — Pill Chips
         ════════════════ -->
    <div class="sub-nav" v-if="categories.length > 0 && showSub">
      <div class="sub-nav-inner container">
        <div class="sub-fade-left"></div>
        <div class="sub-scroll" ref="subScrollRef">
          <router-link to="/" class="sub-lk" exact @click="selectCategory(0)">
            <span class="sl-icon">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
            </span>
            All
          </router-link>
          <router-link
            v-for="c in categories"
            :key="c.id"
            :to="`/category/${c.slug}`"
            class="sub-lk"
            @click="selectCategory(c.id)"
          >
            {{ c.name }}
            <span class="sl-count" v-if="c.articleCount !== undefined">{{ c.articleCount }}</span>
          </router-link>
        </div>
        <div class="sub-fade-right"></div>
      </div>
    </div>

    <!-- ════════════════
         MOBILE DRAWER OVERLAY
         ════════════════ -->
    <Teleport to="body">
      <Transition name="drawer">
        <div v-if="mobileMenuOpen" class="mobile-drawer-overlay" @click="closeMobileMenu">
          <Transition name="drawer-panel">
            <div v-if="mobileMenuOpen" class="mobile-drawer" @click.stop>
              <div class="md-header">
                <span class="md-brand">{{ siteName }}</span>
                <button class="md-close" @click="closeMobileMenu" aria-label="Close menu">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                </button>
              </div>

              <!-- Search in drawer -->
              <div class="md-search">
                <form @submit.prevent="doSearchFromDrawer">
                  <div class="md-search-box">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
                    <input v-model="searchQuery" type="text" placeholder="Search..." class="md-search-input" />
                  </div>
                </form>
              </div>

              <nav class="md-nav">
                <router-link to="/" class="md-lk" exact @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg></span>
                  <span>Home</span>
                </router-link>
                <router-link to="/archives" class="md-lk" @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg></span>
                  <span>Archives</span>
                </router-link>
                <router-link to="/tags" class="md-lk" @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg></span>
                  <span>Tags</span>
                </router-link>
                <router-link to="/about" class="md-lk" @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></span>
                  <span>About</span>
                </router-link>
                <router-link to="/links" class="md-lk" @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg></span>
                  <span>Links</span>
                </router-link>
                <router-link to="/gallery" class="md-lk" @click="closeMobileMenu">
                  <span class="md-lk-icon"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg></span>
                  <span>Gallery</span>
                </router-link>
              </nav>

              <!-- Categories in drawer -->
              <div class="md-cats" v-if="categories.length > 0">
                <div class="md-cats-title">Categories</div>
                <div class="md-cats-grid">
                  <router-link v-for="c in categories" :key="c.id" :to="`/category/${c.slug}`" class="md-cat-pill" @click="closeMobileMenu">{{ c.name }}</router-link>
                </div>
              </div>

              <div class="md-footer">
                <template v-if="userStore.token">
                  <div class="md-user-info">
                    <span class="avatar avatar-md">{{ displayName[0].toUpperCase() }}</span>
                    <div>
                      <div class="md-user-name">{{ displayName }}</div>
                      <div class="md-user-actions">
                        <button @click="$router.push('/user/profile'); closeMobileMenu()">Profile</button>
                        <span class="md-dot">·</span>
                        <button @click="handleLogout">Logout</button>
                      </div>
                    </div>
                  </div>
                </template>
                <template v-else>
                  <router-link to="/login" class="btn btn-primary btn-sm" style="flex:1" @click="closeMobileMenu">Login</router-link>
                  <router-link to="/register" class="btn btn-ghost btn-sm" style="flex:1" @click="closeMobileMenu">Register</router-link>
                </template>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>

    <!-- ── Main ── -->
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition :name="transitionName" mode="out-in">
          <keep-alive include="Home">
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </transition>
      </router-view>
    </main>

    <!-- ── Footer ── -->
    <footer class="footer">
      <div class="footer-inner container">
        <div class="footer-brand">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="1.5" fill="var(--cyber-cyan)"/><circle cx="12" cy="12" r="8" stroke="var(--cyber-cyan)" stroke-width="0.5" fill="none"/></svg>
          <span>{{ siteName }}</span>
        </div>
        <p class="footer-text">{{ footerText }}</p>
        <div class="footer-links"><a href="/api/rss" target="_blank" class="footer-lk">RSS</a></div>
      </div>
    </footer>

    <button v-show="showBackTop" class="back-top" @click="scrollToTop" aria-label="Back to top">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="18 15 12 9 6 15"/></svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSiteConfig } from '@/api/site'
import { getCategories } from '@/api/category'

const router = useRouter()
const route = useRoute()
const transitionName = ref('fade')
watch(() => route.path, (to, from) => { transitionName.value = from === '/' ? 'fade' : 'slide' })

const userStore = useUserStore()

// ── Site data ──
const siteName = ref('MentalBlog')
const footerText = ref('© 2026 MentalBlog')
const categories = ref<any[]>([])

// ── Scroll state ──
const scrolled = ref(false)
const showBackTop = ref(false)
const progress = ref(0)
const showNav = ref(true)
let lastScrollY = 0

// ── Search ──
const searchOpen = ref(false)
const searchQuery = ref('')
const searchInputRef = ref<HTMLInputElement | null>(null)

// ── Mobile ──
const mobileMenuOpen = ref(false)
let navResizeCleanup: (() => void) | null = null
const isMobile = ref(false)

// ── User menu ──
const showUserMenu = ref(false)
const userBtnRef = ref<HTMLElement | null>(null)
const userMenuStyle = ref<Record<string, string>>({})

// ── Nav indicator ──
const indicatorStyle = ref<Record<string, string> | null>(null)
const navLinksRef = ref<HTMLElement | null>(null)

// ── Category ──
const subScrollRef = ref<HTMLElement | null>(null)
const navCanvas = ref<HTMLCanvasElement | null>(null)
const activeCategoryId = ref<number>(0)

const displayName = computed(() => userStore.nickname || userStore.username || 'U')
const showSub = computed(() => !['Login','Register','ArticleDetail','NotFound'].includes(route.name as string))

// ── Methods ──

function handleScroll() {
  const sy = window.scrollY
  const dh = document.documentElement.scrollHeight - window.innerHeight

  scrolled.value = sy > 10
  showBackTop.value = sy > 400
  progress.value = dh > 0 ? Math.min((sy / dh) * 100, 100) : 0

  // Hide navbar on scroll down, show on scroll up
  if (sy > lastScrollY && sy > 150) {
    showNav.value = false
  } else {
    showNav.value = true
  }
  lastScrollY = sy

  updateIndicator()
}

function handleResize() {
  isMobile.value = window.innerWidth < 768
}

function toggleSearch() {
  if (searchOpen.value) {
    closeSearch()
  } else {
    searchOpen.value = true
    nextTick(() => searchInputRef.value?.focus())
  }
}

function closeSearch() {
  searchOpen.value = false
  searchQuery.value = ''
}

function onSearchBlur() {
  if (!searchQuery.value.trim()) {
    setTimeout(() => { closeSearch() }, 200)
  }
}

function doSearch() {
  const q = searchQuery.value.trim()
  if (!q) return
  closeSearch()
  router.push(`/search?q=${encodeURIComponent(q)}`)
}

function doSearchFromDrawer() {
  const q = searchQuery.value.trim()
  if (!q) return
  closeMobileMenu()
  router.push(`/search?q=${encodeURIComponent(q)}`)
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

function toggleTheme() {
  // Placeholder — integrated with useTheme composable already loaded
  document.documentElement.classList.toggle('dark')
}

function selectCategory(id: number) {
  activeCategoryId.value = id
}
function goToAdmin() { showUserMenu.value = false; router.push("/admin").then(() => { window.location.reload() }) }
function handleLogout() {
  userStore.logout()
  showUserMenu.value = false
  router.push("/")
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// ── Nav sliding indicator ──

function initNavParticles() {
  const c = navCanvas.value
  if (!c) return
  const parent = c.parentElement
  if (!parent) return
  c.width = parent.offsetWidth
  c.height = parent.offsetHeight
  const ctx = c.getContext('2d')
  if (!ctx) return
  const count = 35
  const dots = []
  for (let i = 0; i < count; i++) {
    dots.push({ x: Math.random() * c.width, y: Math.random() * c.height, vx: (Math.random() - 0.5) * 0.15, vy: (Math.random() - 0.5) * 0.15, r: Math.random() * 1 + 0.3, o: Math.random() * 0.3 + 0.08 })
  }
  let id
  function draw() {
    ctx.clearRect(0, 0, c.width, c.height)
    for (const d of dots) {
      d.x += d.vx; d.y += d.vy
      if (d.x < 0 || d.x > c.width) d.vx *= -1
      if (d.y < 0 || d.y > c.height) d.vy *= -1
      ctx.beginPath()
      ctx.arc(d.x, d.y, d.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(0,240,255,${d.o})`
      ctx.fill()
    }
    for (let i = 0; i < dots.length; i++) {
      for (let j = i + 1; j < dots.length; j++) {
        const dx = dots[i].x - dots[j].x, dy = dots[i].y - dots[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 80) {
          ctx.beginPath()
          ctx.moveTo(dots[i].x, dots[i].y)
          ctx.lineTo(dots[j].x, dots[j].y)
          ctx.strokeStyle = `rgba(0,240,255,${(1 - dist / 80) * 0.06})`
          ctx.lineWidth = 0.4
          ctx.stroke()
        }
      }
    }
    id = requestAnimationFrame(draw)
  }
  id = requestAnimationFrame(draw)
  const resize = () => { if (c) { c.width = parent.offsetWidth; c.height = parent.offsetHeight } }
    navResizeCleanup = () => window.removeEventListener('resize', resize)
  window.addEventListener('resize', resize)
}

function updateIndicator() {
  if (isMobile.value) return
  nextTick(() => {
    const activeLink = navLinksRef.value?.querySelector('.nav-lk.router-link-active, .nav-lk.active') as HTMLElement
    if (activeLink && navLinksRef.value) {
      const navRect = navLinksRef.value.getBoundingClientRect()
      const linkRect = activeLink.getBoundingClientRect()
      indicatorStyle.value = {
        left: `${linkRect.left - navRect.left}px`,
        width: `${linkRect.width}px`,
        opacity: '1',
      }
    }
  })
}

// ── User dropdown positioning ──
function updateUserMenuStyle() {
  if (!userBtnRef.value) return
  const rect = userBtnRef.value.getBoundingClientRect()
  userMenuStyle.value = {
    top: `${rect.bottom + 10}px`,
    right: `${window.innerWidth - rect.right}px`,
  }
}

watch(showUserMenu, (val) => {
  if (val) updateUserMenuStyle()
})

watch(
  () => route.path,
  () => {
    closeMobileMenu()
    closeSearch()
    showUserMenu.value = false
  },
)

// ── Lifecycle ──
onMounted(async () => {
  try { initNavParticles();
    const [cfg, cat] = await Promise.all([getSiteConfig(), getCategories()])
    if (cfg.data?.site_name) siteName.value = cfg.data.site_name
    if (cfg.data?.site_footer) footerText.value = cfg.data.site_footer
    categories.value = cat.data || []
  } catch (e) { /* ignore */ }

  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('resize', handleResize)
  handleResize()
  updateIndicator()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleResize)
  navResizeCleanup?.()
})
</script>

<style scoped>
/* ════════════════════════════════════════════
   FrontLayout v5 — Cyber Command Center Navbar
   ════════════════════════════════════════════ */

.front-layout { min-height: 100vh; display: flex; flex-direction: column; }

/* ── Global Progress Bar ── */
.global-progress {
  position: fixed; top: 0; left: 0; height: 2px;
  background: linear-gradient(90deg, var(--cyber-cyan), var(--cyber-purple));
  z-index: 1002; transition: width 0.1s linear;
  box-shadow: 0 0 10px var(--cyber-cyan-glow);
}

﻿/* ═══════════════════════ NAVBAR ═══════════════════════ */
.navbar {
  position: sticky; top: 0; z-index: 1000;
  background: rgba(7, 7, 14, 0.55);
  backdrop-filter: blur(36px) saturate(240%);
  -webkit-backdrop-filter: blur(36px) saturate(240%);
  border-bottom: 1px solid transparent;
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  overflow: visible;
}
.navbar::before {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 50% -20%, rgba(0,240,255,0.07) 0%, transparent 60%),
    radial-gradient(ellipse 60% 50% at 30% 120%, rgba(124,58,237,0.04) 0%, transparent 50%),
    radial-gradient(ellipse 50% 40% at 70% 120%, rgba(236,72,153,0.03) 0%, transparent 50%);
  z-index: -1;
  animation: navbarAura 12s ease-in-out infinite alternate;
}
.navbar::after {
  content: '';
  position: absolute; inset: 0;
  background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,240,255,0.006) 2px, rgba(0,240,255,0.006) 4px);
  z-index: -1; pointer-events: none;
}
.navbar-scrolled {
  background: rgba(7, 7, 14, 0.85);
  border-bottom-color: rgba(0,240,255,0.06);
}
.navbar-hidden { transform: translateY(-100%); }

/* Canvas particle layer */
.navbar-canvas {
  position: absolute; inset: 0; z-index: 0;
  pointer-events: none;
  opacity: 0.5;
}

/* Scanline border */
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

/* Glow orb */
.navbar-glow-orb {
  position: absolute; top: -50px; left: 50%; transform: translateX(-50%);
  width: 400px; height: 100px;
  background: radial-gradient(ellipse at center, rgba(0,240,255,0.08) 0%, rgba(0,240,255,0.03) 30%, transparent 70%);
  pointer-events: none;
  animation: orbDance 6s ease-in-out infinite;
}

.navbar-inner {
  height: 64px; display: flex; align-items: center; gap: 20px;
  position: relative; z-index: 1;
}

/* Brand / Logo */
.navbar-brand {
  display: flex; align-items: center; gap: 10px;
  text-decoration: none; flex-shrink: 0; position: relative;
}
.navbar-brand:hover { text-decoration: none; }
.brand-mark {
  position: relative; width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
}
.brand-svg {
  position: relative; z-index: 1;
  width: 26px; height: 26px;
  filter: drop-shadow(0 0 8px rgba(0,240,255,0.35));
  transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.navbar:hover .brand-svg {
  animation: brandFloat 3s ease-in-out infinite, brandSpin 12s linear infinite;
}
.brand-pulse {
  position: absolute; inset: -6px; border-radius: 50%;
  border: 1px solid rgba(0,240,255,0.12);
  opacity: 0; transition: opacity 0.3s;
}
.brand-pulse.pulsing {
  opacity: 0.5;
  animation: brandPulse 2.5s ease-in-out infinite;
  box-shadow: 0 0 20px rgba(0,240,255,0.08);
}
.brand-text {
  font-size: 18px; font-weight: 800; letter-spacing: -0.6px;
  background: linear-gradient(135deg, #fff 20%, #00F0FF 50%, #7C3AED 80%);
  background-size: 200% 100%;
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: brandTextShift 6s ease-in-out infinite;
  transition: filter 0.3s;
}
.navbar:hover .brand-text { filter: drop-shadow(0 0 8px rgba(0,240,255,0.15)); }

/* Nav Links */
.nav-links {
  display: flex; align-items: center; gap: 2px; flex: 1;
  position: relative; justify-content: center;
}
.nav-lk {
  position: relative; display: flex; align-items: center; gap: 6px;
  padding: 8px 16px; font-size: 13px; font-weight: 500;
  color: rgba(148,163,184,0.8); text-decoration: none;
  border-radius: 8px; transition: all 0.25s cubic-bezier(0.23, 1, 0.32, 1);
  font-family: var(--font-sans); letter-spacing: 0;
  z-index: 1; overflow: hidden;
}
.nav-lk::before {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(0,240,255,0.08), rgba(124,58,237,0.04));
  opacity: 0; transition: opacity 0.3s;
  border-radius: 8px;
}
.nav-lk::after {
  content: '';
  position: absolute; bottom: 4px; left: 50%; transform: translateX(-50%) scaleX(0);
  width: 50%; height: 2px;
  background: linear-gradient(90deg, transparent, var(--cyber-cyan), transparent);
  border-radius: 1px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: blur(2px);
}
.nav-lk:hover { color: #fff; transform: translateY(-1px); }
.nav-lk:hover::before { opacity: 1; }
.nav-lk:hover::after { transform: translateX(-50%) scaleX(1); }
.nav-lk.active {
  color: var(--cyber-cyan);
  text-shadow: 0 0 16px rgba(0,240,255,0.25);
  background: rgba(0,240,255,0.06);
  box-shadow: inset 0 0 0 1px rgba(0,240,255,0.08);
}
.nav-lk.active::after {
  transform: translateX(-50%) scaleX(1);
  opacity: 0.8;
  animation: activePulse 2s ease-in-out infinite;
}
.nav-lk .nl-icon {
  color: rgba(148,163,184,0.5);
  transition: all 0.3s;
  display: flex; align-items: center;
}
.nav-lk:hover .nl-icon { color: var(--cyber-cyan); filter: drop-shadow(0 0 8px rgba(0,240,255,0.4)); transform: scale(1.12); }
.nav-lk.active .nl-icon { color: var(--cyber-cyan); filter: drop-shadow(0 0 8px rgba(0,240,255,0.4)); }

/* Nav Indicator */
.nav-indicator {
  position: absolute; bottom: -2px; height: 3px;
  background: linear-gradient(90deg, #00F0FF, #06B6D4, #7C3AED);
  background-size: 300% 100%;
  border-radius: 0 0 3px 3px;
  transition: left 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), width 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.3s;
  pointer-events: none;
  box-shadow: 0 0 14px rgba(0,240,255,0.3), 0 0 30px rgba(0,240,255,0.08);
  animation: indicatorSweep 3s linear infinite;
}

/* ── Navbar Actions ── */
.navbar-actions { display: flex; align-items: center; gap: 4px; flex-shrink: 0; }
.na-btn {
  width: 38px; height: 38px; display: flex; align-items: center; justify-content: center;
  border: none; background: none; color: var(--text-secondary);
  border-radius: 8px; cursor: pointer; transition: all 0.2s; position: relative;
}
.na-btn:hover { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); }
.na-btn:active { transform: scale(0.94); }
.na-divider { width: 1px; height: 22px; background: var(--border-default); margin: 0 4px; }
.hamburger-btn { display: none; }

/* ── Search ── */
.search-area { position: relative; display: flex; align-items: center; }
.search-form { position: absolute; right: 100%; top: 50%; transform: translateY(-50%); margin-right: 8px; }
.search-input {
  width: 0; height: 36px; padding: 0; font-size: 14px; font-family: var(--font-sans);
  border: 1px solid transparent; border-radius: 8px;
  background: transparent; color: var(--text-primary); outline: none;
  transition: all 0.35s cubic-bezier(0.23, 1, 0.32, 1);
  opacity: 0; cursor: default;
}
.search-open .search-input {
  width: 200px; padding: 0 12px;
  border-color: rgba(0,240,255,0.25);
  background: var(--bg-surface);
  opacity: 1; cursor: text;
}
.search-open .search-input:focus { border-color: var(--cyber-cyan); box-shadow: 0 0 0 3px rgba(0,240,255,0.1); }

/* ── User Button ── */
.user-dropdown-btn {
  display: flex; align-items: center; gap: 8px; padding: 4px 10px 4px 4px;
  border: 1px solid var(--border-subtle); border-radius: 10px;
  background: transparent; color: var(--text-secondary);
  cursor: pointer; transition: all 0.2s; font-family: inherit;
}
.user-dropdown-btn:hover { border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.04); }
.user-dropdown-btn svg { transition: transform 0.2s; }
.user-dropdown-btn svg.rotated { transform: rotate(180deg); }
.ud-name {
  font-size: 13px; font-weight: 500; color: var(--text-secondary);
  max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.user-menu-overlay { position: fixed; inset: 0; z-index: 9999; }
.user-menu-dropdown {
  position: fixed; z-index: 10000; min-width: 230px;
  background: rgba(10, 12, 18, 0.92);
  border: 1px solid rgba(0,240,255,0.1);
  border-radius: 14px; padding: 6px;
  box-shadow: 0 16px 48px rgba(0,0,0,0.6), 0 0 0 1px rgba(0,240,255,0.04), 0 0 40px rgba(0,240,255,0.03);
  backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px);
  animation: dropdownIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}


/* ── User Dropdown Items ── */
.um-arrow {
  position: absolute; top: -6px; right: 40px; width: 12px; height: 12px;
  background: rgba(24, 27, 36, 0.92); border-left: 1px solid rgba(0,240,255,0.12);
  border-top: 1px solid rgba(0,240,255,0.12); transform: rotate(45deg);
  border-radius: 2px 0 0 0;
}
.um-header { display: flex; align-items: center; gap: 12px; padding: 8px 10px; }
.um-name { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.um-email { font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); margin-top: 1px; }
.um-divider { height: 1px; background: var(--border-subtle); margin: 6px 0; }
.um-item {
  width: 100%; display: flex; align-items: center; gap: 10px;
  padding: 9px 12px; font-size: 13px; font-weight: 500; color: var(--text-secondary);
  border: none; background: transparent; border-radius: 8px;
  cursor: pointer; transition: all 0.15s; font-family: inherit; text-align: left;
}
.um-item:hover { background: rgba(0,240,255,0.06); color: var(--cyber-cyan); }
.um-admin { color: var(--cyber-cyan); }
.um-logout:hover { background: rgba(239,68,68,0.08); color: var(--cyber-red); }

/* ── Login/Register buttons in navbar ── */
.navbar-actions .btn-ghost { background: transparent; color: var(--text-secondary); border: 1px solid transparent; padding: 6px 14px; font-size: 13px; border-radius: 8px; transition: all 0.2s; }
.navbar-actions .btn-ghost:hover { color: var(--cyber-cyan); background: rgba(0,240,255,0.04); }
.navbar-actions .btn-primary { background: var(--cyber-cyan); color: #0A0A0F; padding: 6px 14px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; cursor: pointer; transition: all 0.2s; }
.navbar-actions .btn-primary:hover { box-shadow: 0 0 16px rgba(0,240,255,0.2); }

/* ═══════════════════════ SECONDARY NAV ═══════════════════════ */
.sub-nav {
  position: static; z-index: 99;
  background: rgba(10,10,15,0.7); backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);
  border-bottom: 1px solid var(--border-subtle);
  transition: all 0.3s;
}
.sub-nav-inner {
  position: relative; height: 44px; display: flex; align-items: center;
}
.sub-scroll {
  display: flex; align-items: center; gap: 4px;
  overflow-x: auto; scrollbar-width: none; padding: 0 12px;
  -webkit-overflow-scrolling: touch;
}
.sub-scroll::-webkit-scrollbar { display: none; }

.sub-fade-left, .sub-fade-right {
  position: absolute; top: 0; bottom: 0; width: 32px; z-index: 2;
  pointer-events: none;
}
.sub-fade-left {
  left: 0;
  background: linear-gradient(90deg, rgba(10,10,15,0.85) 0%, transparent 100%);
}
.sub-fade-right {
  right: 0;
  background: linear-gradient(270deg, rgba(10,10,15,0.85) 0%, transparent 100%);
}

.sub-lk {
  display: flex; align-items: center; gap: 5px;
  padding: 5px 14px; font-size: 12px; font-weight: 500;
  color: var(--text-tertiary); text-decoration: none;
  border-radius: 20px; white-space: nowrap; transition: all 0.2s;
  font-family: var(--font-sans); border: 1px solid transparent;
}
.sub-lk:hover { color: var(--text-primary); background: rgba(255,255,255,0.04); }
.sub-lk.exact-active, .sub-lk.router-link-exact-active {
  color: var(--cyber-cyan); background: rgba(0,240,255,0.08);
  border-color: rgba(0,240,255,0.15);
  box-shadow: 0 0 12px rgba(0,240,255,0.06);
}
.sl-icon { display: flex; align-items: center; }
.sl-count {
  font-size: 10px; font-weight: 600; font-family: var(--font-mono);
  color: var(--text-tertiary); background: rgba(255,255,255,0.04);
  padding: 1px 6px; border-radius: 10px;
}

/* ═══════════════════════ MOBILE DRAWER ═══════════════════════ */
.mobile-drawer-overlay {
  position: fixed; inset: 0; z-index: 2000;
  background: rgba(0,0,0,0.6); backdrop-filter: blur(4px);
}
.mobile-drawer {
  position: fixed; top: 0; right: 0; bottom: 0; width: min(320px, 85vw); z-index: 2001;
  background: var(--bg-deep);
  border-left: 1px solid rgba(0,240,255,0.08);
  display: flex; flex-direction: column;
  box-shadow: -8px 0 32px rgba(0,0,0,0.5);
  overflow-y: auto;
}

/* Drawer transitions */
.drawer-enter-active { transition: opacity 0.25s ease; }
.drawer-leave-active { transition: opacity 0.2s ease; }
.drawer-enter-from, .drawer-leave-to { opacity: 0; }
.drawer-panel-enter-active { transition: transform 0.35s cubic-bezier(0.23, 1, 0.32, 1); }
.drawer-panel-leave-active { transition: transform 0.2s ease; }
.drawer-panel-enter-from, .drawer-panel-leave-to { transform: translateX(100%); }

.md-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 20px; border-bottom: 1px solid var(--border-subtle);
}
.md-brand {
  font-size: 16px; font-weight: 800;
  background: linear-gradient(135deg, #fff, var(--cyber-cyan));
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.md-close {
  width: 36px; height: 36px; display: flex; align-items: center; justify-content: center;
  border: 1px solid var(--border-subtle); border-radius: 8px;
  background: transparent; color: var(--text-secondary); cursor: pointer;
  transition: all 0.15s;
}
.md-close:hover { border-color: var(--cyber-cyan); color: var(--cyber-cyan); }

.md-search { padding: 16px 20px; }
.md-search-box {
  display: flex; align-items: center; gap: 8px;
  padding: 0 14px; height: 40px; border-radius: 10px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle);
  color: var(--text-tertiary);
}
.md-search-input {
  flex: 1; height: 100%; border: none; background: transparent;
  font-size: 14px; font-family: var(--font-sans); color: var(--text-primary);
  outline: none;
}
.md-search-input::placeholder { color: var(--text-tertiary); }

.md-nav { flex: 1; padding: 0 12px; display: flex; flex-direction: column; gap: 2px; }
.md-lk {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px;
  font-size: 14px; font-weight: 500; color: var(--text-secondary);
  text-decoration: none; border-radius: 10px; transition: all 0.15s;
}
.md-lk:hover { background: rgba(0,240,255,0.04); color: var(--text-primary); }
.md-lk.router-link-active { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); }
.md-lk-icon { color: var(--text-tertiary); display: flex; align-items: center; }
.md-lk.router-link-active .md-lk-icon { color: var(--cyber-cyan); }

.md-cats { padding: 16px 20px; border-top: 1px solid var(--border-subtle); }
.md-cats-title { font-size: 11px; font-weight: 700; color: var(--text-tertiary); text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; }
.md-cats-grid { display: flex; flex-wrap: wrap; gap: 6px; }
.md-cat-pill {
  padding: 4px 12px; font-size: 12px; font-weight: 500; color: var(--text-secondary);
  background: rgba(255,255,255,0.03); border: 1px solid var(--border-subtle);
  border-radius: 20px; text-decoration: none; transition: all 0.15s;
}
.md-cat-pill:hover { border-color: rgba(0,240,255,0.2); color: var(--cyber-cyan); }

.md-footer {
  padding: 16px 20px; border-top: 1px solid var(--border-subtle);
}
.md-user-info { display: flex; align-items: center; gap: 12px; }
.md-user-name { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.md-user-actions { display: flex; align-items: center; gap: 4px; margin-top: 4px; font-size: 12px; color: var(--text-tertiary); }
.md-user-actions button {
  border: none; background: none; color: var(--text-tertiary); cursor: pointer;
  font-size: 12px; font-family: inherit; padding: 0; transition: color 0.15s;
}
.md-user-actions button:hover { color: var(--cyber-cyan); }
.md-dot { opacity: 0.4; }

/* ── Main ── */
.main-content { flex: 1; }

/* ── Footer ── */
.footer { margin-top: 48px; padding: 24px 0; border-top: 1px solid var(--border-subtle); background: var(--bg-surface); }
.footer-inner { display: flex; align-items: center; justify-content: center; gap: 16px; flex-wrap: wrap; }
.footer-brand { display: flex; align-items: center; gap: 6px; font-size: 12px; font-weight: 600; color: var(--text-secondary); }
.footer-text { font-size: 12px; color: var(--text-tertiary); }
.footer-lk { font-size: 12px; color: var(--text-tertiary); }
.footer-lk:hover { color: var(--cyber-cyan); }

/* Back to top */
.back-top {
  position: fixed; bottom: 28px; right: 28px; z-index: 99;
  width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;
  border: 1px solid var(--border-default); border-radius: 10px;
  background: var(--bg-elevated); color: var(--text-tertiary);
  cursor: pointer; transition: all 0.25s;
}
.back-top:hover { border-color: var(--cyber-cyan); color: var(--cyber-cyan); box-shadow: var(--shadow-glow-cyan); }

/* ═══════════════════════ RESPONSIVE ═══════════════════════ */
@media (max-width: 1023px) {
  .nav-links { display: none; }
  .ud-name { display: none; }
}

@media (max-width: 767px) {
  .navbar { position: sticky; }
  .navbar::before { background: rgba(10,10,15,0.95); }
  .navbar-inner { padding: 0 16px; }
  .nav-links { display: none; }
  .hamburger-btn { display: flex; }
  .search-open .search-input { width: 140px; }
  .ud-name { display: none; }
  .sub-nav { display: none; }
  .back-top { bottom: 80px; }
  .navbar-brand { gap: 6px; }
  .brand-text { font-size: 15px; }
  .search-form { position: absolute; right: 44px; }
}
/* ═══ Page Route Transitions ═══ */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-enter-active { transition: all 0.25s cubic-bezier(0.23, 1, 0.32, 1); }
.slide-leave-active { transition: all 0.15s ease-in; }
.slide-enter-from { opacity: 0; transform: translateX(20px); }
.slide-leave-to { opacity: 0; transform: translateX(-20px); }

/* Responsive — reduce motion if user prefers */
@media (prefers-reduced-motion: reduce) {
 .fade-enter-active, .fade-leave-active,
  .slide-enter-active, .slide-leave-active { transition: none !important; }
}

/* Navbar Enhanced Animations */
@keyframes navbarAura {
  0% { opacity: 0.6; }
  100% { opacity: 1; }
}
@keyframes orbPulse {
  0%, 100% { opacity: 0.6; transform: translateX(-50%) scaleY(1); }
  50% { opacity: 1; transform: translateX(-50%) scaleY(1.2); }
}
@keyframes brandSpin {
  to { transform: rotate(360deg); }
}
.navbar:hover .brand-svg { animation: brandSpin 8s linear infinite; }
.navbar-scanline.active {
  background: linear-gradient(90deg, transparent 0%, rgba(0,240,255,0.4) 10%, rgba(0,240,255,0.8) 30%, rgba(0,240,255,0.9) 50%, rgba(124,58,237,0.6) 70%, rgba(0,240,255,0.3) 90%, transparent 100%);
  background-size: 200% 100%;
  animation: scanlineFlow 3s ease-in-out infinite;
  box-shadow: 0 0 8px rgba(0,240,255,0.3), 0 0 20px rgba(0,240,255,0.1);
}
@keyframes scanlineFlow {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}



/* ═══ Navbar Enhanced Animations ═══ */
@keyframes navbarAura {
  0% { opacity: 0.5; }
  100% { opacity: 1; }
}
@keyframes scanlineBreathe {
  0%, 100% { opacity: 0.5; background-position: 0% 0; }
  50% { opacity: 1; background-position: 200% 0; }
}
@keyframes orbDance {
  0%, 100% { transform: translateX(-50%) scaleY(1); opacity: 0.5; }
  50% { transform: translateX(-50%) scaleY(1.3); opacity: 1; }
}
@keyframes brandFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}
@keyframes brandSpin {
  to { transform: rotate(360deg); }
}
@keyframes brandTextShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}
@keyframes brandPulse {
  0%, 100% { transform: scale(1); opacity: 0.2; }
  50% { transform: scale(1.5); opacity: 0.6; }
}
@keyframes indicatorSweep {
  0% { background-position: 0% 0; }
  100% { background-position: 300% 0; }
}
@keyframes activePulse {
  0%, 100% { opacity: 0.6; filter: blur(2px); }
  50% { opacity: 1; filter: blur(3px); }
}


@keyframes dropdownIn {
  from { opacity: 0; transform: translateY(-8px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

</style>
