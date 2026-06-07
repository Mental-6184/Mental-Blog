<template>
  <div class="page-section">
    <div class="container">
      <!-- ＝＝ Page Header ＝＝ -->
      <div class="tags-header">
        <div class="th-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
        </div>
        <div>
          <h1 class="th-title">Tags</h1>
          <p class="th-subtitle">{{ tags.length }} tags · {{ totalArticles }} articles tagged</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="skeleton-tags">
        <div class="sk-tag" v-for="i in 15" :key="i" :style="{ width: (60 + Math.random() * 100) + 'px' }"></div>
      </div>

      <!-- Empty -->
      <div v-else-if="tags.length === 0" class="empty-state" style="padding:80px 0">
        <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.15"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
        <p>No tags yet.</p>
        <p class="empty-hint">Tags will appear once articles are created and categorized.</p>
      </div>

      <!-- Tag Cloud Layout -->
      <div v-else class="tags-layout">
        <!-- Side: top tags by count -->
        <div class="tags-top-list">
          <div class="ttl-header">
            <span class="ttl-icon">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </span>
            <span>Top Tags</span>
          </div>
          <div class="ttl-items">
            <div
              v-for="(t, i) in topTags"
              :key="t.id"
              class="ttl-item"
              @click="$router.push(`/tag/${t.slug}`)"
            >
              <span class="ttl-rank" :class="{ 'ttl-rank-top': i < 3 }">{{ i + 1 }}</span>
              <div class="ttl-body">
                <span class="ttl-name">{{ t.name }}</span>
                <span class="ttl-count">{{ t.articleCount || 0 }} articles</span>
              </div>
              <div class="ttl-bar-bg">
                <div class="ttl-bar-fill" :style="{ width: getTopBarWidth(i, t.articleCount || 0) + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Main: full cloud -->
        <div class="tags-cloud-area">
          <div class="tca-header">
            <span class="tca-label">All Tags</span>
            <div class="tca-search">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
              <input v-model="tagFilter" type="text" placeholder="Filter tags..." class="tca-input" />
            </div>
          </div>
          <div class="tca-cloud">
            <span
              v-for="t in filteredTags"
              :key="t.id"
              class="tca-tag"
              :style="{ fontSize: getTagSize(t.articleCount || 0) + 'px', opacity: getTagOpacity(t.articleCount || 0) }"
              @click="$router.push(`/tag/${t.slug}`)"
            >
              {{ t.name }}
              <sup class="tca-count">{{ t.articleCount || 0 }}</sup>
            </span>
          </div>
          <div v-if="tagFilter && filteredTags.length === 0" class="empty-state" style="padding: 40px 0">
            <p>No tags matching "{{ tagFilter }}"</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import { getTags } from '@/api/category'

const tags = ref<any[]>([])
const loading = ref(false)
const tagFilter = ref('')

const totalArticles = computed(() => tags.value.reduce((s: number, t: any) => s + (t.articleCount || 0), 0))
const maxCount = computed(() => Math.max(...tags.value.map((t: any) => t.articleCount || 0), 1))
const topTags = computed(() => [...tags.value].slice(0, 8))

const filteredTags = computed(() => {
  if (!tagFilter.value.trim()) return tags.value
  const q = tagFilter.value.trim().toLowerCase()
  return tags.value.filter((t: any) => t.name.toLowerCase().includes(q))
})

function getTopBarWidth(idx: number, count: number) {
  if (idx < 3) return 100
  return Math.max(15, (count / Math.max(maxCount.value, 1)) * 100)
}

function getTagSize(count: number) {
  if (!maxCount.value) return 13
  const nums = tags.value.map((t: any) => t.articleCount || 0).filter((n: number) => n > 0)
  if (nums.length === 0) return 13
  const min = Math.min(...nums)
  const range = Math.max(...nums) - min || 1
  const ratio = (count - min) / range
  return 13 + ratio * 22
}

function getTagOpacity(count: number) {
  if (!maxCount.value) return 0.4
  const nums = tags.value.map((t: any) => t.articleCount || 0).filter((n: number) => n > 0)
  if (nums.length === 0) return 0.4
  const min = Math.min(...nums)
  const range = Math.max(...nums) - min || 1
  return 0.35 + (count - min) / range * 0.65
}

onMounted(async () => {
  loading.value = true
  try {
    const r = await getTags()
    const raw = (r as any).data || []
    tags.value = raw.sort((a: any, b: any) => (b.articleCount || 0) - (a.articleCount || 0))
    console.log('[Tags] loaded:', tags.value.length, 'sample:', tags.value.slice(0, 3))
  } catch (e) {
    console.error('[Tags] load failed:', e)
  }
  loading.value = false

  nextTick(() => {
    gsap.from('.tca-tag', {
      opacity: 0, scale: 0.7, duration: 0.4,
      stagger: { each: 0.03, from: 'random' },
      ease: 'back.out(1.7)',
      delay: 0.2,
    })
  })
})
</script>

<style scoped>
/* ═══════════════════════════
   TAGS PAGE
   ═══════════════════════════ */
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 44px 0 100px; }

/* ── Header ── */
.tags-header { display: flex; align-items: center; gap: 16px; margin-bottom: 36px; }
.th-icon {
  width: 52px; height: 52px; display: flex; align-items: center; justify-content: center;
  border-radius: 12px; background: rgba(0,240,255,0.06); border: 1px solid rgba(0,240,255,0.12);
  color: var(--cyber-cyan);
}
.th-title { font-size: 28px; font-weight: 800; color: #fff; letter-spacing: -0.5px; margin-bottom: 3px; }
.th-subtitle { font-size: 13px; color: var(--text-tertiary); font-family: var(--font-mono); }

/* ── Skeleton ── */
.skeleton-tags { display: flex; flex-wrap: wrap; gap: 10px; padding: 40px 0; }
.sk-tag { height: 32px; background: var(--bg-elevated); border-radius: 8px; animation: skShimmer 2s infinite; }
@keyframes skShimmer { 0%,100% { opacity: 0.3; } 50% { opacity: 0.6; } }

.empty-hint { font-size: 12px; color: var(--text-tertiary); opacity: 0.5; margin-top: 4px; font-family: var(--font-mono); }

/* ── Layout ── */
.tags-layout { display: flex; gap: 32px; align-items: flex-start; }

/* ═══ Top Tags List ═══ */
.tags-top-list {
  width: 260px; flex-shrink: 0;
  background: var(--bg-elevated); border: 1px solid var(--border-subtle);
  border-radius: 12px; padding: 18px; overflow: hidden;
}
.ttl-header {
  display: flex; align-items: center; gap: 8px;
  margin-bottom: 14px; padding-bottom: 12px;
  font-size: 13px; font-weight: 700; color: var(--text-secondary);
  border-bottom: 1px solid var(--border-subtle);
  text-transform: uppercase; letter-spacing: 0.6px;
}
.ttl-icon { color: var(--cyber-amber); display: flex; align-items: center; }
.ttl-items { display: flex; flex-direction: column; gap: 6px; }
.ttl-item {
  display: flex; align-items: center; gap: 10px; padding: 8px 10px;
  border-radius: 8px; cursor: pointer; transition: all 0.2s; position: relative; overflow: hidden;
}
.ttl-item:hover { background: rgba(0,240,255,0.04); }
.ttl-rank {
  width: 22px; height: 22px; border-radius: 6px; display: flex;
  align-items: center; justify-content: center;
  font-size: 11px; font-weight: 800; flex-shrink: 0;
  font-family: var(--font-mono); color: var(--text-tertiary);
  background: rgba(255,255,255,0.03);
}
.ttl-rank-top {
  background: linear-gradient(135deg, var(--cyber-amber), #D97706); color: #000;
}
.ttl-body { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 1px; z-index: 1; }
.ttl-name { font-size: 13px; font-weight: 600; color: var(--text-primary); }
.ttl-count { font-size: 10px; color: var(--text-tertiary); font-family: var(--font-mono); }
.ttl-bar-bg {
  position: absolute; left: 0; top: 0; bottom: 0; width: 100%; z-index: 0;
}
.ttl-bar-fill {
  height: 100%; background: rgba(0,240,255,0.04); border-radius: 0 4px 4px 0;
  transition: width 1s ease-out;
}

/* ═══ Tag Cloud ═══ */
.tags-cloud-area { flex: 1; min-width: 0; }
.tca-header {
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
  margin-bottom: 20px;
}
.tca-label {
  font-size: 13px; font-weight: 700; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.6px;
}
.tca-search {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 14px; border-radius: 8px;
  background: var(--bg-elevated); border: 1px solid var(--border-subtle);
  transition: border-color 0.2s;
}
.tca-search:focus-within { border-color: rgba(0,240,255,0.2); }
.tca-search svg { color: var(--text-tertiary); flex-shrink: 0; }
.tca-input {
  border: none; background: transparent; outline: none;
  font-size: 12px; font-family: var(--font-sans); color: var(--text-primary);
  width: 130px;
}
.tca-input::placeholder { color: var(--text-tertiary); }

.tca-cloud {
  display: flex; flex-wrap: wrap; align-items: center; gap: 10px 14px;
  padding: 32px 28px; background: var(--bg-elevated);
  border: 1px solid var(--border-subtle); border-radius: 14px;
  min-height: 200px;
}
.tca-tag {
  display: inline-flex; align-items: center; gap: 3px;
  font-weight: 600; color: var(--cyber-cyan);
  padding: 5px 14px; border-radius: 8px;
  background: rgba(0,240,255,0.04);
  border: 1px solid rgba(0,240,255,0.08);
  cursor: pointer; transition: all 0.2s; white-space: nowrap;
}
.tca-tag:hover {
  background: rgba(0,240,255,0.1);
  border-color: rgba(0,240,255,0.25);
  transform: translateY(-3px) scale(1.04);
  box-shadow: 0 6px 20px rgba(0,240,255,0.1);
  z-index: 1; position: relative;
}
.tca-count {
  font-size: 9px; opacity: 0.55; font-family: var(--font-mono);
  color: var(--text-tertiary);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .tags-layout { flex-direction: column-reverse; }
  .tags-top-list { width: 100%; }
  .tca-cloud { padding: 20px 16px; gap: 8px 10px; }
}
</style>
