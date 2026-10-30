<template>
  <div class="home-page">
    <section class="home-intro">
      <div class="container intro-inner">
        <div class="intro-copy">
          <p class="eyebrow">{{ siteName }} · 技术与思考</p>
          <h1>记录代码，也记录生活。</h1>
          <p class="intro-desc">在这里阅读关于开发、产品和日常思考的文章。</p>
          <form class="home-search" @submit.prevent="doSearch">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
            <input v-model="searchQuery" placeholder="搜索文章、主题或关键词" aria-label="搜索文章" />
            <button type="submit">搜索</button>
          </form>
        </div>
        <div class="intro-stats" aria-label="站点统计"><div><strong>{{ articleCount }}</strong><span>篇文章</span></div><div><strong>{{ catCount }}</strong><span>个分类</span></div><div><strong>{{ tagCount }}</strong><span>个标签</span></div></div>
      </div>
    </section>

    <div class="main-section container">
      <section class="content-area">
        <div class="section-heading"><div><p class="eyebrow">持续更新</p><h2>最新文章</h2></div><router-link to="/archives" class="section-link">查看全部 <span>→</span></router-link></div>
        <div v-if="loading" class="skeleton-feed"><div class="sk-card" v-for="i in 5" :key="i"><div class="sk-thumb"></div><div class="sk-body"><div class="sk-line w30"></div><div class="sk-line w80"></div><div class="sk-line w60"></div></div></div></div>
        <div v-else-if="!articles.length" class="empty-state"><p>暂时还没有发布文章。</p></div>
        <div v-else class="feed-list">
          <article v-for="a in articles" :key="a.id" class="feed-item" @click="$router.push(`/article/${a.id}`)">
            <div class="fi-thumb"><img :src="a.coverImage || dc" :alt="a.title" loading="lazy" /></div>
            <div class="fi-body"><div class="fi-meta"><span v-if="a.categoryName" class="fi-cat">{{ a.categoryName }}</span><span class="fi-date">{{ formatDate(a.publishedAt) }}</span><span class="fi-read" v-if="a.content">{{ readTime(a.content) }} 分钟阅读</span></div><h2 class="fi-title">{{ a.title }}</h2><p class="fi-desc" v-if="a.summary">{{ a.summary }}</p><div class="fi-foot"><div class="fi-tags" v-if="a.tags?.length"><span class="fi-tag" v-for="t in a.tags.slice(0, 3)" :key="t">#{{ t }}</span></div><div class="fi-stats"><span>{{ fmtNum(a.viewCount) }} 阅读</span><span>{{ fmtNum(a.commentCount) }} 评论</span></div></div></div>
          </article>
        </div>
        <div class="feed-pagination" v-if="total > pageSize"><el-pagination v-model:current-page="cp" :page-size="pageSize" :total="total" layout="prev, pager, next" background @current-change="loadArticles" /></div>
      </section>
      <Sidebar context="home" :hot-articles="hotArts" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticles } from '@/api/article'
import { getCategories, getTags } from '@/api/category'
import { getSiteConfig } from '@/api/site'
import Sidebar from '@/components/front/Sidebar.vue'

const router = useRouter(); const siteName = ref('MentalBlog'); const searchQuery = ref(''); const articles = ref<any[]>([]); const hotArts = ref<any[]>([]); const cp = ref(1); const pageSize = 10; const total = ref(0); const loading = ref(false); const articleCount = ref(0); const catCount = ref(0); const tagCount = ref(0)
const dc = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="180" fill="%23E2E8F0"%3E%3Crect width="300" height="180"/%3E%3C/svg%3E'
function fmtNum(n: number) { return !n ? '0' : n >= 1000 ? (n / 1000).toFixed(1) + 'k' : String(n) }
function formatDate(s: string) { if (!s) return ''; const d = new Date(s); return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}` }
function readTime(content: string) { return Math.max(1, Math.ceil((content.replace(/<[^>]*>/g, '').length || 0) / 500)) }
function doSearch() { const q = searchQuery.value.trim(); if (q) router.push(`/search?q=${encodeURIComponent(q)}`) }
async function loadArticles() { loading.value = true; try { const r = await getArticles({ page: cp.value, pageSize }); articles.value = (r as any).records || (r as any).data || []; total.value = (r as any).total || 0; hotArts.value = articles.value.slice(0, 5); articleCount.value = total.value } catch { articles.value = [] } loading.value = false }
onMounted(async () => { try { const [site, categories, tags] = await Promise.all([getSiteConfig(), getCategories(), getTags()]); if (site.data?.site_name) siteName.value = site.data.site_name; catCount.value = (categories as any).data?.length || 0; tagCount.value = (tags as any).data?.length || 0 } catch { /* 使用默认统计 */ } await loadArticles() })
</script>

<style scoped>
.home-intro { background: linear-gradient(135deg, #ECFEFF 0%, #FFFFFF 55%, #F8FAFC 100%); border-bottom: 1px solid var(--border-subtle); }.intro-inner { min-height: 310px; display: flex; align-items: center; justify-content: space-between; gap: 48px; padding-top: 46px; padding-bottom: 46px; }.intro-copy { max-width: 680px; }.eyebrow { margin: 0 0 10px; color: var(--cyber-cyan); font-size: 12px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }.intro-copy h1 { margin: 0; color: var(--text-primary); font-size: clamp(30px, 5vw, 52px); line-height: 1.12; letter-spacing: -.04em; }.intro-desc { margin: 14px 0 24px; color: var(--text-secondary); font-size: 16px; }.home-search { display: flex; align-items: center; gap: 10px; max-width: 560px; padding: 5px 6px 5px 16px; background: var(--bg-elevated); border: 1px solid var(--border-default); border-radius: 12px; box-shadow: var(--shadow-sm); }.home-search svg { flex-shrink: 0; color: var(--text-tertiary); }.home-search input { min-width: 0; flex: 1; border: 0; outline: 0; background: transparent; color: var(--text-primary); font: inherit; }.home-search button { border: 0; border-radius: 8px; padding: 9px 18px; background: var(--cyber-cyan); color: #083344; font-weight: 700; cursor: pointer; }.intro-stats { display: flex; gap: 24px; padding: 18px 0; }.intro-stats div { display: flex; flex-direction: column; gap: 3px; min-width: 68px; }.intro-stats strong { color: var(--text-primary); font-size: 25px; line-height: 1; }.intro-stats span { color: var(--text-tertiary); font-size: 12px; }
.main-section { display: grid; grid-template-columns: minmax(0, 1fr) 280px; gap: 32px; padding-top: 38px; padding-bottom: 72px; }.content-area { min-width: 0; }.section-heading { display: flex; align-items: end; justify-content: space-between; gap: 20px; margin-bottom: 20px; }.section-heading .eyebrow { margin-bottom: 4px; }.section-heading h2 { margin: 0; color: var(--text-primary); font-size: 24px; }.section-link { color: var(--text-secondary); font-size: 13px; }.section-link:hover { color: var(--cyber-cyan); }.section-link span { margin-left: 4px; font-size: 18px; }.feed-list { display: flex; flex-direction: column; gap: 14px; }.feed-item { display: flex; min-height: 176px; overflow: hidden; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 12px; cursor: pointer; transition: transform .2s, border-color .2s, box-shadow .2s; }.feed-item:hover { transform: translateY(-2px); border-color: var(--border-accent); box-shadow: var(--shadow-md); }.fi-thumb { width: 220px; flex-shrink: 0; background: var(--bg-surface); }.fi-thumb img { width: 100%; height: 100%; object-fit: cover; }.fi-body { display: flex; min-width: 0; flex: 1; flex-direction: column; padding: 18px 20px; }.fi-meta { display: flex; align-items: center; flex-wrap: wrap; gap: 9px; color: var(--text-tertiary); font-size: 11px; }.fi-cat { padding: 3px 8px; color: var(--cyber-cyan); background: rgba(6,182,212,.1); border-radius: 5px; font-weight: 700; }.fi-read { padding: 2px 6px; background: var(--bg-surface); border-radius: 4px; }.fi-title { margin: 9px 0 6px; color: var(--text-primary); font-size: 19px; line-height: 1.35; }.fi-desc { display: -webkit-box; overflow: hidden; margin: 0; color: var(--text-secondary); font-size: 13px; line-height: 1.6; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }.fi-foot { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-top: auto; padding-top: 12px; }.fi-tags { display: flex; flex-wrap: wrap; gap: 7px; }.fi-tag, .fi-stats { color: var(--text-tertiary); font-size: 11px; }.fi-stats { display: flex; gap: 10px; white-space: nowrap; }.feed-pagination { display: flex; justify-content: center; padding-top: 28px; }.skeleton-feed { display: flex; flex-direction: column; gap: 14px; }.sk-card { display: flex; gap: 14px; padding: 14px; border: 1px solid var(--border-subtle); border-radius: 12px; background: var(--bg-elevated); }.sk-thumb { width: 200px; height: 130px; flex-shrink: 0; background: var(--bg-surface); border-radius: 8px; }.sk-body { flex: 1; padding-top: 10px; }.sk-line { height: 12px; margin-bottom: 14px; background: var(--bg-surface); border-radius: 4px; }.w30 { width: 30%; }.w60 { width: 60%; }.w80 { width: 80%; }
@media (max-width: 900px) { .intro-inner { align-items: flex-start; flex-direction: column; gap: 24px; }.intro-stats { padding-top: 0; }.main-section { grid-template-columns: 1fr; } }
@media (max-width: 600px) { .intro-inner { min-height: 0; padding-top: 32px; padding-bottom: 32px; }.intro-copy h1 { font-size: 34px; }.home-search button { padding: 9px 13px; }.feed-item { flex-direction: column; }.fi-thumb { width: 100%; height: 170px; }.fi-body { padding: 15px 16px; }.fi-title { font-size: 17px; }.fi-foot { align-items: flex-start; flex-direction: column; }.sk-card { flex-direction: column; }.sk-thumb { width: 100%; height: 150px; } }
</style>
