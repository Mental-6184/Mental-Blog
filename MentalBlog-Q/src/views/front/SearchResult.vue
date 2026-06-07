<template>
  <div class="page-section"><div class="container">
    <!-- ═══ Search Box ═══ -->
    <div class="search-header" ref="headerRef">
      <h2 class="sh-title">Search</h2>
      <div class="sh-line"></div>
    </div>
    <div class="search-box" ref="searchBoxRef">
      <div class="search-input-wrap">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
        <input v-model="keyword" placeholder="Search articles..." @keyup.enter="doSearch" />
      </div>
      <button class="btn btn-primary btn-sm" @click="doSearch" :disabled="!keyword.trim()">Search</button>
    </div>

    <!-- ═══ Results as feed cards ═══ -->
    <div v-if="loading" class="skeleton-feed">
      <div class="sk-card" v-for="i in 3" :key="i">
        <div class="sk-thumb"></div>
        <div class="sk-body">
          <div class="sk-line w30"></div>
          <div class="sk-line w80"></div>
          <div class="sk-line w60"></div>
        </div>
      </div>
    </div>

    <div v-else-if="searched && articles.length===0" class="empty-state" style="margin-top:32px">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
      <p>No results for <strong>"{{ keyword }}"</strong></p>
    </div>

    <div v-else-if="searched" class="feed-list" ref="feedRef">
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
          </div>
          <h2 class="fi-title" v-html="highlight(a.title)"></h2>
          <p class="fi-desc" v-if="a.summary" v-html="highlight(a.summary)"></p>
          <div class="fi-foot">
            <div class="fi-tags" v-if="a.tags?.length">
              <span class="fi-tag" v-for="t in a.tags.slice(0,3)" :key="t">#{{ t }}</span>
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
  </div></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getArticles } from '@/api/article'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const keyword = ref('')
const articles = ref<any[]>([])
const searched = ref(false)
const loading = ref(false)
const dc = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="180" fill="%23181B24"%3E%3Crect width="300" height="180"/%3E%3C/svg%3E'
const headerRef = ref<HTMLElement | null>(null)
const searchBoxRef = ref<HTMLElement | null>(null)
const feedRef = ref<HTMLElement | null>(null)
const cardRefs: (HTMLElement | null)[] = []
const setCardRef = (el: any, i: number) => { if (el) cardRefs[i] = el as HTMLElement }

onMounted(() => {
  const q = route.query.q as string
  if (q) { keyword.value = q; doSearch() }
  nextTick(() => {
    gsap.from(headerRef.value, { opacity: 0, y: -16, duration: 0.5, ease: 'power3.out' })
    gsap.from(searchBoxRef.value, { opacity: 0, y: 16, duration: 0.5, delay: 0.1, ease: 'power3.out' })
  })
})

function fmtNum(n: number) { if (!n) return '0'; if (n >= 1000) return (n / 1000).toFixed(1) + 'k'; return String(n) }
const formatDate = (s: string) => { if (!s) return ''; const d = new Date(s); return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` }

function highlight(text: string): string {
  if (!text || !keyword.value.trim()) return text
  const words = keyword.value.trim().split(/\s+/)
  let result = text
  for (const w of words) {
    const escaped = w.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    result = result.replace(new RegExp(`(${escaped})`, 'gi'), '<mark style="background:rgba(0,240,255,0.15);color:var(--cyber-cyan);border-radius:2px;padding:0 2px">$1</mark>')
  }
  return result
}

onUnmounted(() => { ScrollTrigger.getAll().forEach(t => t.kill()) })
async function doSearch() {
  if (!keyword.value.trim()) return
  searched.value = true
  loading.value = true
  try {
    const r = await getArticles({ keyword: keyword.value, page: 1, pageSize: 20 })
    articles.value = (r as any).records || (r as any).data || []
  } catch (e) { /* ignore */ }
  loading.value = false
  nextTick(() => {
    cardRefs.forEach((c, i) => {
      if (!c) return
      gsap.from(c, { opacity: 0, y: 24, duration: 0.4, delay: i * 0.05, scrollTrigger: { trigger: c, start: 'top 90%' } })
    })
  })
}
</script>

<style scoped>
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 40px 0 100px; }

/* ── Header ── */
.search-header { margin-bottom: 20px; }
.sh-title { font-size: 20px; font-weight: 800; color: #fff; margin-bottom: 8px; }
.sh-line { width: 40px; height: 2px; background: var(--cyber-cyan); border-radius: 1px; }

/* ── Search box ── */
.search-box { display: flex; gap: 10px; margin-bottom: 28px; }
.search-input-wrap {
  flex: 1; display: flex; align-items: center; gap: 10px;
  padding: 0 16px; height: 44px; border-radius: 10px;
  background: var(--bg-surface); border: 1px solid var(--border-subtle);
  color: var(--text-tertiary); transition: border-color 0.2s;
}
.search-input-wrap:focus-within { border-color: rgba(0,240,255,0.2); }
.search-input-wrap input {
  flex: 1; height: 100%; border: none; background: transparent; outline: none;
  font-size: 14px; font-family: var(--font-sans); color: var(--text-primary);
}
.search-input-wrap input::placeholder { color: var(--text-tertiary); }

/* ── Skeleton ── */
.skeleton-feed { display: flex; flex-direction: column; gap: 12px; }
.sk-card { display: flex; gap: 14px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 14px; }
.sk-thumb { width: 130px; height: 85px; background: var(--bg-surface); border-radius: 6px; animation: skShimmer 2s infinite; }
.sk-body { flex: 1; display: flex; flex-direction: column; gap: 8px; justify-content: center; }
.sk-line { height: 12px; background: var(--bg-surface); border-radius: 4px; animation: skShimmer 2s infinite; }
.sk-line.w30 { width: 30%; } .sk-line.w60 { width: 60%; } .sk-line.w80 { width: 80%; }
@keyframes skShimmer { 0%,100% { opacity: 0.3; } 50% { opacity: 0.6; } }

/* ── Feed items ── */
.feed-list { display: flex; flex-direction: column; gap: 12px; }
.feed-item {
  display: flex; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px;
  overflow: hidden; cursor: pointer; transition: all 0.3s;
}
.feed-item:hover { border-color: rgba(0,240,255,0.15); box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,240,255,0.04); transform: translateY(-2px); }
.fi-thumb { width: 200px; min-height: 130px; flex-shrink: 0; overflow: hidden; background: var(--bg-surface); position: relative; }
.fi-thumb img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.feed-item:hover .fi-thumb img { transform: scale(1.06); }
.fi-thumb-overlay { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.4); color: #fff; opacity: 0; transition: opacity 0.3s; }
.feed-item:hover .fi-thumb-overlay { opacity: 1; }
.fi-body { flex: 1; padding: 16px 20px; min-width: 0; display: flex; flex-direction: column; }
.fi-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; font-size: 11px; flex-wrap: wrap; }
.fi-cat { font-family: var(--font-mono); font-size: 10px; font-weight: 600; color: var(--cyber-cyan); padding: 2px 8px; border-radius: 4px; background: rgba(0,240,255,0.06); text-transform: uppercase; letter-spacing: 0.5px; }
.fi-date { color: var(--text-tertiary); font-family: var(--font-mono); }
.fi-title { font-size: 16px; font-weight: 700; line-height: 1.35; margin-bottom: 6px; overflow: hidden; max-height: 2.8em; line-height: 1.4; color: var(--text-primary); transition: color 0.2s; }
.feed-item:hover .fi-title { color: var(--cyber-cyan); }
.fi-desc { font-size: 13px; color: var(--text-tertiary); line-height: 1.5; margin-bottom: auto; overflow: hidden; max-height: 2.8em; line-height: 1.4; }
.fi-foot { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-top: 10px; }
.fi-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.fi-tag { font-size: 10px; font-weight: 500; color: var(--text-tertiary); }
.fi-stats { display: flex; gap: 12px; }
.fis-item { display: flex; align-items: center; gap: 3px; font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); }
.empty-state { text-align: center; padding: 64px 24px; color: var(--text-tertiary); }
.empty-state svg { margin-bottom: 16px; }
.empty-state p { font-size: 14px; }

@media (max-width: 640px) {
  .feed-item { flex-direction: column; }
  .fi-thumb { width: 100%; height: 180px; min-height: auto; }
}
</style>
