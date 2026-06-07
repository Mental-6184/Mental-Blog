<template>
  <div class="page-section">
    <div class="container">
      <!-- ＝＝ Page Header ＝＝ -->
      <div class="archives-header">
        <div class="ah-top">
          <div class="ah-title-row">
            <div class="ah-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            </div>
            <div>
              <h1 class="ah-title">Archives</h1>
              <p class="ah-subtitle">A timeline of thoughts, by year and month</p>
            </div>
          </div>
          <div class="ah-stats-panel">
            <div class="asp-item">
              <span class="asp-num">{{ totalCount }}</span>
              <span class="asp-lbl">Articles</span>
            </div>
            <div class="asp-divider"></div>
            <div class="asp-item">
              <span class="asp-num">{{ yearList.length }}</span>
              <span class="asp-lbl">Years</span>
            </div>
            <div class="asp-divider"></div>
            <div class="asp-item">
              <span class="asp-num">{{ totalWords }}</span>
              <span class="asp-lbl">Words</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ＝＝ Loading / Empty ＝＝ -->
      <div v-if="loading" class="skeleton-timeline">
        <div class="sk-year" v-for="i in 3" :key="i">
          <div class="sk-year-head"></div>
          <div class="sk-cards">
            <div class="sk-card-line" v-for="j in 3" :key="j" :style="{ width: (55 + Math.random() * 45) + '%' }"></div>
          </div>
        </div>
      </div>

      <div v-else-if="archives.length === 0" class="empty-state" style="padding: 80px 0">
        <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.15"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/></svg>
        <p>No articles have been published yet.</p>
        <p class="empty-hint">Start writing and your posts will appear here, organized by date.</p>
      </div>

      <!-- ＝＝ Timeline ＝＝ -->
      <div v-else class="archives-layout">
        <!-- Year quick-nav -->
        <nav class="year-nav" v-if="yearList.length > 1">
          <button
            v-for="yr in yearList"
            :key="yr"
            :class="['yn-btn', { 'yn-active': activeYear === yr }]"
            @click="scrollToYear(yr)"
          >
            {{ yr }}
          </button>
        </nav>

        <!-- Timeline content -->
        <div class="timeline-wrapper" ref="timelineRef">
          <div class="timeline-line"></div>

          <div
            v-for="group in archives"
            :key="group.year + '-' + group.month"
            :data-year="group.year"
            class="tl-group"
            :ref="el => { if (el) setGroupRef(el, group.year) }"
          >
            <!-- Month header -->
            <div class="tl-month-header" :class="{ 'tl-first-of-year': isFirstOfYear(group) }">
              <div class="tl-month-dot">
                <div class="tl-month-dot-inner" v-if="isFirstOfYear(group)"></div>
              </div>
              <div class="tl-month-label">
                <span class="tl-month-txt">{{ monthName(group.month) }}</span>
                <span class="tl-year-tag" v-if="isFirstOfYear(group)">{{ group.year }}</span>
              </div>
              <div class="tl-month-count">{{ group.articles.length }} posts</div>
            </div>

            <!-- Articles -->
            <div class="tl-cards">
              <article
                v-for="a in group.articles"
                :key="a.id"
                class="tl-card"
                @click="$router.push(`/article/${a.id}`)"
              >
                <div class="tlc-date-box">
                  <span class="tlc-day">{{ formatDay(a.publishedAt) }}</span>
                  <span class="tlc-wd">{{ weekdayName(a.publishedAt) }}</span>
                </div>
                <div class="tlc-body">
                  <h3 class="tlc-title">{{ a.title }}</h3>
                  <div class="tlc-meta">
                    <span v-if="a.categoryName" class="tlc-cat">{{ a.categoryName }}</span>
                    <span class="tlc-stat" v-if="a.viewCount > 0">
                      <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      {{ fmtNum(a.viewCount) }}
                    </span>
                  </div>
                </div>
                <div class="tlc-arrow">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                </div>
              </article>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getArchives } from '@/api/article'

gsap.registerPlugin(ScrollTrigger)

const archives = ref<any[]>([])
const totalCount = ref(0)
const loading = ref(false)
const activeYear = ref<number>(0)
const timelineRef = ref<HTMLElement | null>(null)
const groupRefMap: Record<number, HTMLElement[]> = {}
const setGroupRef = (el: HTMLElement, year: number) => {
  if (!groupRefMap[year]) groupRefMap[year] = []
  groupRefMap[year].push(el)
}

// ── Computed ──
const yearList = computed(() => {
  const years = new Set<number>()
  archives.value.forEach(g => years.add(g.year))
  return Array.from(years).sort((a, b) => b - a)
})

const totalWords = computed(() => {
  let c = 0
  archives.value.forEach(g => {
    g.articles.forEach((a: any) => {
      if (a.content) c += a.content.replace(/[#*\->\[\]()`!|~\s]/g, '').length
    })
  })
  return fmtNumWords(c)
})

// ── Helpers ──
const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

function monthName(m: number) { return monthNames[m - 1] || '' }
function weekdayName(s: string) { if (!s) return ''; return weekdays[new Date(s).getDay()] || '' }
function formatDay(s: string) { if (!s) return '--'; return s.substring(8, 10).replace(/^0/, '') }
function fmtNum(n: number) { if (!n) return '0'; if (n >= 1000) return (n / 1000).toFixed(1) + 'k'; return String(n) }
function fmtNumWords(n: number) { if (n >= 100000) return (n / 100000).toFixed(1) + 'w'; if (n >= 1000) return (n / 1000).toFixed(1) + 'k'; return String(n) }

function isFirstOfYear(group: any) {
  const idx = archives.value.indexOf(group)
  if (idx === 0) return true
  return archives.value[idx - 1].year !== group.year
}

function scrollToYear(yr: number) {
  activeYear.value = yr
  const els = groupRefMap[yr]
  if (els && els[0]) {
    els[0].scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// ── Scroll observer for active year ──
let yearObserver: IntersectionObserver | null = null

function setupYearObserver() {
  const groups = timelineRef.value?.querySelectorAll<HTMLElement>('.tl-group[data-year]')
  if (!groups || !groups.length) return

  yearObserver = new IntersectionObserver(
    (entries) => {
      const visible = entries
        .filter(e => e.isIntersecting)
        .map(e => Number((e.target as HTMLElement).dataset.year))
      if (visible.length > 0) activeYear.value = Math.min(...visible)
    },
    { rootMargin: '-20% 0px -70% 0px' },
  )
  groups.forEach(g => yearObserver!.observe(g))
}

// ── Animations ──
function runAnim() {
  nextTick(() => {
    gsap.from('.ah-stats-panel', {
      opacity: 0, y: 24, duration: 0.6, ease: 'power3.out', delay: 0.1,
    })

    gsap.from('.tl-group', {
      opacity: 0, y: 20, duration: 0.5, stagger: 0.06, ease: 'power3.out',
      scrollTrigger: { trigger: '.timeline-wrapper', start: 'top 80%' },
    })
  })
}

// ── Lifecycle ──
onMounted(async () => {
  loading.value = true
  try {
    const r = await getArchives()
    archives.value = (r.data || []).sort((a: any, b: any) => {
      if (a.year !== b.year) return b.year - a.year
      return b.month - a.month
    })
    totalCount.value = archives.value.reduce((s: number, g: any) => s + (g.articles?.length || 0), 0)
    if (yearList.value.length > 0) activeYear.value = yearList.value[0]
  } catch (e) { /* ignore */ }
  loading.value = false

  nextTick(() => {
    setupYearObserver()
    runAnim()
  })
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
  yearObserver?.disconnect()
})
</script>

<style scoped>
/* ═══════════════════════════════
   ARCHIVES PAGE — v2
   ═══════════════════════════════ */
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 44px 0 100px; }

/* ── Page Header ── */
.archives-header { margin-bottom: 40px; }
.ah-top {
  display: flex; align-items: center; justify-content: space-between;
  gap: 24px; flex-wrap: wrap;
}
.ah-title-row { display: flex; align-items: center; gap: 14px; }
.ah-icon {
  width: 48px; height: 48px; display: flex; align-items: center; justify-content: center;
  border-radius: 12px; background: rgba(0,240,255,0.06);
  border: 1px solid rgba(0,240,255,0.12); color: var(--cyber-cyan);
}
.ah-title { font-size: 26px; font-weight: 800; color: #fff; letter-spacing: -0.5px; margin-bottom: 2px; }
.ah-subtitle { font-size: 13px; color: var(--text-tertiary); font-family: var(--font-mono); }

/* Stats panel */
.ah-stats-panel {
  display: flex; align-items: center; gap: 0;
  background: var(--bg-elevated); border: 1px solid var(--border-subtle);
  border-radius: 12px; padding: 14px 20px;
}
.asp-item { display: flex; flex-direction: column; align-items: center; gap: 2px; padding: 0 20px; }
.asp-num {
  font-size: 18px; font-weight: 800; font-family: var(--font-mono);
  color: var(--cyber-cyan);
}
.asp-lbl { font-size: 10px; font-weight: 600; color: var(--text-tertiary); text-transform: uppercase; letter-spacing: 0.5px; }
.asp-divider { width: 1px; height: 32px; background: var(--border-subtle); }

/* ── Year Quick Nav ── */
.year-nav {
  display: flex; gap: 6px; margin-bottom: 32px; flex-wrap: wrap;
}
.yn-btn {
  padding: 6px 18px; font-size: 13px; font-weight: 600; font-family: var(--font-mono);
  border: 1px solid var(--border-subtle); border-radius: 8px;
  background: transparent; color: var(--text-secondary);
  cursor: pointer; transition: all 0.2s;
}
.yn-btn:hover { border-color: rgba(0,240,255,0.2); color: var(--cyber-cyan); }
.yn-active {
  border-color: rgba(0,240,255,0.3) !important;
  color: var(--cyber-cyan) !important;
  background: rgba(0,240,255,0.06) !important;
  box-shadow: 0 0 12px rgba(0,240,255,0.08);
}

/* ── Timeline ── */
.archives-layout { }
.timeline-wrapper { position: relative; padding-left: 48px; }

/* Vertical line */
.timeline-line {
  position: absolute; left: 19px; top: 8px; bottom: 8px; width: 2px;
  background: linear-gradient(to bottom, var(--cyber-cyan), var(--cyber-purple), transparent);
  opacity: 0.25; border-radius: 1px;
}

/* ── Timeline group (month) ── */
.tl-group { margin-bottom: 28px; position: relative; }

/* Month header */
.tl-month-header {
  display: flex; align-items: center; gap: 12px; margin-bottom: 10px;
}
.tl-first-of-year { }
.tl-month-header.tl-first-of-year { padding-top: 4px; }

.tl-month-dot {
  position: absolute; left: -38px; top: 6px;
  width: 14px; height: 14px; border-radius: 50%;
  background: var(--bg-deep); border: 2px solid rgba(0,240,255,0.3);
  z-index: 1; display: flex; align-items: center; justify-content: center;
}
.tl-first-of-year .tl-month-dot {
  width: 18px; height: 18px; left: -40px;
  border-color: var(--cyber-cyan);
  box-shadow: 0 0 14px rgba(0,240,255,0.3);
}
.tl-month-dot-inner {
  width: 7px; height: 7px; border-radius: 50%;
  background: var(--cyber-cyan);
}

.tl-month-label { display: flex; align-items: center; gap: 10px; }
.tl-month-txt { font-size: 14px; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 1px; }
.tl-year-tag {
  font-size: 11px; font-weight: 700; color: var(--cyber-cyan);
  background: rgba(0,240,255,0.08); border: 1px solid rgba(0,240,255,0.15);
  padding: 2px 10px; border-radius: 4px; font-family: var(--font-mono);
}
.tl-month-count {
  margin-left: auto; font-size: 11px; color: var(--text-tertiary);
  font-family: var(--font-mono); background: rgba(255,255,255,0.03);
  padding: 2px 10px; border-radius: 4px;
}

/* ── Article cards ── */
.tl-cards { display: flex; flex-direction: column; gap: 8px; }

.tl-card {
  display: flex; align-items: center; gap: 14px;
  padding: 12px 16px; border-radius: 10px; cursor: pointer;
  background: var(--bg-elevated); border: 1px solid var(--border-subtle);
  transition: all 0.25s;
}
.tl-card:hover {
  border-color: rgba(0,240,255,0.15);
  background: rgba(0,240,255,0.03);
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  transform: translateX(4px);
}

/* Date box */
.tlc-date-box {
  width: 42px; flex-shrink: 0; display: flex; flex-direction: column;
  align-items: center; gap: 0;
}
.tlc-day {
  font-size: 20px; font-weight: 800; font-family: var(--font-mono);
  color: var(--text-primary); line-height: 1;
}
.tlc-wd {
  font-size: 9px; font-weight: 600; color: var(--text-tertiary);
  text-transform: uppercase; letter-spacing: 1px; margin-top: 2px;
  font-family: var(--font-mono);
}

/* Body */
.tlc-body { flex: 1; min-width: 0; }
.tlc-title {
  font-size: 14px; font-weight: 600; color: var(--text-primary);
  line-height: 1.4; transition: color 0.2s;
  overflow: hidden; white-space: nowrap; text-overflow: ellipsis;
}
.tl-card:hover .tlc-title { color: var(--cyber-cyan); }

.tlc-meta {
  display: flex; align-items: center; gap: 10px; margin-top: 4px;
}
.tlc-cat {
  font-size: 10px; font-weight: 600; color: var(--cyber-cyan);
  padding: 1px 8px; border-radius: 3px; background: rgba(0,240,255,0.06);
  font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.5px;
}
.tlc-stat {
  display: flex; align-items: center; gap: 3px;
  font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono);
}

/* Arrow */
.tlc-arrow {
  flex-shrink: 0; color: var(--text-tertiary);
  transition: all 0.25s;
}
.tl-card:hover .tlc-arrow { color: var(--cyber-cyan); transform: translateX(3px); }

/* ── Skeleton ── */
.skeleton-timeline { max-width: 640px; margin: 0 auto; padding-left: 48px; }
.sk-year { margin-bottom: 28px; }
.sk-year-head {
  width: 80px; height: 18px; background: var(--bg-elevated); border-radius: 4px;
  margin-bottom: 12px; animation: skShimmer 2s infinite;
}
.sk-cards { display: flex; flex-direction: column; gap: 8px; }
.sk-card-line {
  height: 48px; background: var(--bg-elevated); border-radius: 10px;
  animation: skShimmer 2s infinite;
}
@keyframes skShimmer { 0%,100% { opacity: 0.3; } 50% { opacity: 0.6; } }

/* ── Empty state ── */
.empty-hint {
  font-size: 12px; color: var(--text-tertiary); margin-top: 4px;
  opacity: 0.5; font-family: var(--font-mono);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .ah-top { flex-direction: column; align-items: flex-start; }
  .ah-stats-panel { width: 100%; justify-content: center; }
  .ah-title { font-size: 22px; }
  .timeline-wrapper { padding-left: 36px; }
  .timeline-line { left: 14px; }
  .tl-month-dot { left: -30px; }
  .tl-first-of-year .tl-month-dot { left: -32px; }
  .tl-card { padding: 10px 14px; gap: 10px; }
  .tlc-day { font-size: 17px; }
  .tlc-title { font-size: 13px; }
  .tl-card:hover { transform: translateX(2px); }
}

@media (max-width: 480px) {
  .timeline-wrapper { padding-left: 28px; }
  .timeline-line { left: 10px; }
  .tl-month-dot { left: -24px; width: 12px; height: 12px; }
  .tl-first-of-year .tl-month-dot { left: -26px; width: 14px; height: 14px; }
}
</style>
