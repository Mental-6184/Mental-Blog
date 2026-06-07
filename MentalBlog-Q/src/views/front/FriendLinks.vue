<template>
  <div class="page-section">
    <div class="container">
      <!-- Page Header -->
      <div class="feed-header" ref="headerRef">
        <div class="fh-left">
          <h2 class="fh-title">Friend Links</h2>
          <div class="fh-line"></div>
        </div>
      </div>

      <div v-if="links.length===0" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
        <p>No links yet</p>
      </div>

      <div v-else class="feed-list">
        <a
          v-for="(l, idx) in links"
          :key="l.id"
          :ref="el => setCardRef(el, idx)"
          :href="normalizeUrl(l.url)"
          target="_blank"
          rel="noopener"
          class="feed-item"
        >
          <div class="fi-thumb fl-thumb">
            <span class="fl-icon-letter">{{ l.name[0] }}</span>
            <div class="fi-thumb-overlay">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
            </div>
          </div>
          <div class="fi-body">
            <div class="fi-meta">
              <span class="fi-cat">{{ getDomain(l.url) }}</span>
              <span class="fi-external" v-if="l.description">recommended</span>
            </div>
            <h2 class="fi-title">{{ l.name }}</h2>
            <p class="fi-desc" v-if="l.description">{{ l.description }}</p>
            <div class="fi-foot">
              <div class="fi-stats fi-stats-left">
                <span class="fis-item" title="Visit site">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" y1="14" x2="21" y2="3"/></svg>
                  Open link
                </span>
              </div>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="fl-arrow"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
            </div>
          </div>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getFriendLinks } from '@/api/site'

gsap.registerPlugin(ScrollTrigger)

const links = ref<any[]>([])
const headerRef = ref<HTMLElement | null>(null)
const cardRefs: (HTMLElement | null)[] = []
const setCardRef = (el: any, i: number) => { if (el) cardRefs[i] = el as HTMLElement }

onUnmounted(() => { ScrollTrigger.getAll().forEach(t => t.kill()) })
const normalizeUrl = (u: string) => {
  if (!u) return '#'
  if (/^https?:\/\//i.test(u)) return u
  return 'http://' + u
}

const getDomain = (u: string) => {
  try { return new URL(normalizeUrl(u)).hostname.replace('www.', '') }
  catch { return u }
}

onMounted(async () => {
  try {
    const r = await getFriendLinks()
    links.value = r.data || []
  } catch (e) { /* ignore */ }

  nextTick(() => {
    gsap.from(headerRef.value, { opacity: 0, y: -16, duration: 0.6, ease: 'power3.out' })

    cardRefs.forEach((c, i) => {
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
})
</script>

<style scoped>
.page-section {
  background: var(--bg-deep);
  min-height: 60vh;
  padding: 40px 0 100px;
}

/* ── Feed Header ── */
.feed-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 28px;
}
.fh-title {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 8px;
}
.fh-line {
  width: 40px;
  height: 2px;
  background: var(--cyber-cyan);
  border-radius: 1px;
}

/* ── Feed List (single column) ── */
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ── Feed Item — identical to Home.vue article cards ── */
.feed-item {
  display: flex;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
}
.feed-item:hover {
  border-color: rgba(0,240,255,0.15);
  box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,240,255,0.04);
  transform: translateY(-2px);
  color: inherit;
}

/* ── Thumbnail — gradient letter icon ── */
.fl-thumb {
  width: 200px;
  min-height: 130px;
  flex-shrink: 0;
  overflow: hidden;
  background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple));
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fl-icon-letter {
  font-size: 48px;
  font-weight: 800;
  color: rgba(255,255,255,0.85);
  font-family: var(--font-mono);
  text-shadow: 0 0 20px rgba(0,0,0,0.2);
  transition: transform 0.5s, opacity 0.3s;
}
.feed-item:hover .fl-icon-letter {
  transform: scale(1.12);
  opacity: 0.7;
}

.fi-thumb-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.4);
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}
.feed-item:hover .fi-thumb-overlay {
  opacity: 1;
}

/* ── Body ── */
.fi-body {
  flex: 1;
  padding: 16px 20px;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.fi-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  font-size: 11px;
  flex-wrap: wrap;
}
.fi-cat {
  font-family: var(--font-mono);
  font-size: 10px;
  font-weight: 600;
  color: var(--cyber-cyan);
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(0,240,255,0.06);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.fi-external {
  color: var(--text-tertiary);
  font-family: var(--font-mono);
  padding: 1px 6px;
  background: rgba(255,255,255,0.03);
  border-radius: 4px;
}

.fi-title {
  font-size: 16px;
  font-weight: 700;
  line-height: 1.35;
  margin-bottom: 6px;
  overflow: hidden;
  max-height: 2.8em;
  line-height: 1.4;
  color: var(--text-primary);
  transition: color 0.2s;
}
.feed-item:hover .fi-title {
  color: var(--cyber-cyan);
}

.fi-desc {
  font-size: 13px;
  color: var(--text-tertiary);
  line-height: 1.5;
  margin-bottom: auto;
  overflow: hidden;
  max-height: 2.8em;
  line-height: 1.4;
}

.fi-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 10px;
}
.fi-stats {
  display: flex;
  gap: 12px;
}
.fi-stats-left {
  gap: 8px;
}
.fis-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}
.fl-arrow {
  flex-shrink: 0;
  color: var(--text-tertiary);
  transition: all 0.25s;
}
.feed-item:hover .fl-arrow {
  color: var(--cyber-cyan);
  transform: translateX(4px);
}

/* ── Responsive ── */
@media (max-width: 640px) {
  .feed-item {
    flex-direction: column;
  }
  .fl-thumb {
    width: 100%;
    height: 120px;
    min-height: auto;
  }
  .fl-icon-letter {
    font-size: 36px;
  }
}
</style>
