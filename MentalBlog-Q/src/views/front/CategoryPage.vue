<template>
  <div class="cat-page" v-if="pageReady">
    <div class="container">
      <div class="page-h" ref="headerRef"><h1>{{ categoryName||'Category' }}</h1><p>{{ articles.length }} articles</p></div>
      <div class="cw">
        <div class="main-col">
          <div v-if="loading" class="empty-state"><p>Loading...</p></div>
          <div v-else-if="articles.length===0" class="empty-state"><p>No articles in this category</p></div>
          <div v-else class="feed-list">
            <article v-for="(a, i) in articles" :key="a.id" class="fi" @click="goArticle(a.id)" :ref="el => setCardRef(el, i)">
              <div class="fi-thumb">
                <img :src="a.coverImage||dc" :alt="a.title" loading="lazy" />
                <div class="fi-thumb-overlay">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                </div>
              </div>
              <div class="fi-body">
                <div class="fi-meta">
                  <span v-if="a.categoryName" class="fi-cat">{{ a.categoryName }}</span>
                  <span class="fi-date">{{ a.publishedAt?.substring(0,10)||'' }}</span>
                  <span class="fi-read" v-if="a.content">{{ Math.max(1, Math.ceil((a.content.replace(/<[^>]*>/g,'').length||0)/500)) }} min read</span>
                </div>
                <h2 class="fi-title">{{ a.title||'' }}</h2>
                <p class="fi-desc" v-if="a.summary">{{ a.summary }}</p>
                <div class="fi-foot">
                  <div class="fi-tags" v-if="a.tags?.length">
                    <span class="fi-tag" v-for="t in a.tags.slice(0,3)" :key="t">#{{ t }}</span>
                  </div>
                  <div class="fi-stats">
                    <span class="fis-item">{{ a.viewCount||0 }} views</span>
                    <span class="fis-item">{{ a.likeCount||0 }} likes</span>
                    <span class="fis-item">{{ a.commentCount||0 }} comments</span>
                  </div>
                </div>
              </div>
            </article>
          </div>
        </div>
        <Sidebar context="category" :context-name="categoryName" :context-slug="route.params.slug" />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import Sidebar from '@/components/front/Sidebar.vue'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute(); const router = useRouter()
const articles = ref<any[]>([]); const categoryName = ref('')
const loading = ref(false); const pageReady = ref(false)
const headerRef = ref<HTMLElement | null>(null)
const cardRefs: (HTMLElement | null)[] = []
const setCardRef = (el: any, i: number) => { cardRefs[i] = el as HTMLElement }
const dc = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="180" fill="%23181B24"%3E%3Crect width="300" height="180"/%3E%3C/svg%3E'

async function loadData() {
  loading.value = true; pageReady.value = false
  const slug = route.params.slug as string
  if (!slug) { loading.value = false; pageReady.value = true; return }
  try {
    const cr = await getCategories(); const cats = (cr as any).data || []
    const cat = cats.find((c: any) => c.slug === slug)
    if (cat) { categoryName.value = cat.name; const r = await getArticles({ page: 1, pageSize: 100, categoryId: cat.id }); articles.value = (r as any).records || [] }
    else { categoryName.value = slug; articles.value = [] }
  } catch (e) { articles.value = []; categoryName.value = slug }
  loading.value = false; pageReady.value = true
  nextTick(() => {
    gsap.from(headerRef.value, { opacity: 0, y: -20, duration: 0.5, ease: 'power3.out' })
    cardRefs.forEach((c, i) => { if (!c) return; gsap.from(c, { opacity: 0, y: 24, duration: 0.4, delay: 0.1 + i * 0.04, ease: 'power3.out', scrollTrigger: { trigger: c, start: 'top 90%' } }) })
  })
}
onMounted(() => loadData())
watch(() => route.params.slug, () => loadData())
function goArticle(id: number) { if (id) router.push(`/article/${id}`) }
onUnmounted
</script>
<style scoped>(() => { ScrollTrigger.getAll().forEach(t => t.kill()) })
.cat-page { background: var(--bg-deep); min-height: 60vh; padding: 44px 0 100px; }
.page-h { margin-bottom: 28px; }
.page-h h1 { font-size: 26px; font-weight: 800; color: #fff; letter-spacing: -.5px; margin-bottom: 2px; }
.page-h p { font-size: 14px; color: var(--text-secondary); font-family: var(--font-mono); }
.cw { display: flex; gap: 28px; }
.main-col { flex: 1; min-width: 0; }
.feed-list { display: flex; flex-direction: column; gap: 12px; }

/* ── Card (exactly matching Home.vue) ── */
.fi { display: flex; background: var(--bg-elevated); will-change: transform; border: 1px solid var(--border-subtle); border-radius: 10px; overflow: hidden; cursor: pointer; transition: all 0.3s; }
.fi:hover { border-color: rgba(0,240,255,0.15); box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,240,255,0.04); transform: translateY(-2px); }
.fi-thumb { width: 200px; min-height: 130px; flex-shrink: 0; overflow: hidden; background: var(--bg-surface); position: relative; }
.fi-thumb img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.fi:hover .fi-thumb img { transform: scale(1.06); }
.fi-thumb-overlay { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.4); color: #fff; opacity: 0; transition: opacity 0.3s; }
.fi:hover .fi-thumb-overlay { opacity: 1; }
.fi-body { flex: 1; padding: 16px 20px; min-width: 0; display: flex; flex-direction: column; }
.fi-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; font-size: 11px; flex-wrap: wrap; }
.fi-cat { font-family: var(--font-mono); font-size: 10px; font-weight: 600; color: var(--cyber-cyan); padding: 2px 8px; border-radius: 4px; background: rgba(0,240,255,0.06); text-transform: uppercase; letter-spacing: 0.5px; }
.fi-date { color: var(--text-tertiary); font-family: var(--font-mono); }
.fi-read { color: var(--text-tertiary); font-family: var(--font-mono); padding: 1px 6px; background: rgba(255,255,255,0.03); border-radius: 4px; }
.fi-title { font-size: 16px; font-weight: 700; line-height: 1.4; margin-bottom: 6px; color: var(--text-primary); overflow: hidden; max-height: 2.8em; transition: color 0.2s; }
.fi:hover .fi-title { color: var(--cyber-cyan); }
.fi-desc { font-size: 13px; color: var(--text-tertiary); line-height: 1.5; margin-bottom: auto; overflow: hidden; max-height: 2.8em; }
.fi-foot { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-top: 10px; }
.fi-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.fi-tag { font-size: 10px; font-weight: 500; color: var(--text-tertiary); }
.fi:hover .fi-tag { color: var(--text-secondary); }
.fi-stats { display: flex; gap: 12px; }
.fis-item { font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); }

@media(max-width:768px) { .cw { flex-direction: column; } .fi-thumb { width: 130px; } }
@media(max-width:480px) { .fi { flex-direction: column; } .fi-thumb { width: 100%; height: 160px; } }
</style>


