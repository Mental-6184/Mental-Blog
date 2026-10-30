<template>
  <div class="page-section"><div class="container">
    <div class="user-tabs">
      <router-link to="/user/profile" class="tab-link" active-class="active">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg> Profile
      </router-link>
      <router-link to="/user/comments" class="tab-link" active-class="active">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg> Comments
      </router-link>
      <router-link to="/user/favorites" class="tab-link" active-class="active">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg> Favorites
      </router-link>
      <router-link to="/user/password" class="tab-link" active-class="active">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg> Password
      </router-link>
    </div>
    <div class="user-content">
      <div class="uc-card">
        <div class="uc-head">
          <h2>My Favorites</h2>
          <p class="hint">{{ articles.length }} articles</p>
        </div>
        <div v-if="loading" class="empty-state"><p>Loading...</p></div>
        <div v-else-if="articles.length===0" class="empty-state"><p>No favorites yet</p></div>
        <div v-else class="fav-list">
          <div v-for="a in articles" :key="a.id" class="fav-item" @click="$router.push(`/article/${a.id}`)">
            <div class="fav-body">
              <div class="fav-meta">
                <span v-if="a.categoryName" class="meta-cat">{{ a.categoryName }}</span>
                <span class="meta-date">{{ a.publishedAt?.substring(0,10) }}</span>
              </div>
              <h3>{{ a.title }}</h3>
              <p>{{ a.summary||'No summary' }}</p>
            </div>
            <button class="btn-unfav" @click.stop="handleUnfavorite(a.id)">Unfavorite</button>
          </div>
        </div>
      </div>
    </div>
  </div></div>
</template>
<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { getMyFavorites, toggleFavoriteArticle } from '@/api/interact'
import { getArticleById } from '@/api/article'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'
const articles=ref<any[]>([]); const loading=ref(false)
onMounted(async () => { nextTick(() => { gsap.from(".user-tabs", { opacity: 0, y: -10, duration: 0.4, ease: "power3.out" }); gsap.from(".uc-card", { opacity: 0, y: 20, duration: 0.5, delay: 0.1, ease: "power3.out" }) }); loading.value=true; try{const r=await getMyFavorites();const ids:number[]=r.data||[];const results=await Promise.all(ids.map(id=>getArticleById(id).catch(()=>null)));articles.value=results.filter(r=>r?.data).map(r=>r!.data)}catch(e){} loading.value=false })
async function handleUnfavorite(id:number){try{await toggleFavoriteArticle(id);articles.value=articles.value.filter((a:any)=>a.id!==id);ElMessage.success('Removed from favorites')}catch(e){}}
</script>
<style scoped>
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 32px 0 80px; }

.user-tabs { display: flex; gap: 4px; margin-bottom: 28px; padding: 4px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; overflow-x: auto; }
.tab-link { display: flex; align-items: center; gap: 6px; padding: 10px 18px; font-size: 14px; font-weight: 500; color: var(--text-secondary); text-decoration: none; border-radius: 8px; transition: all 200ms; white-space: nowrap; }
.tab-link:hover { color: var(--text-primary); background: rgba(0,240,255,0.03); }
.tab-link.active { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); font-weight: 600; }

.user-content { max-width: 640px; }
.uc-card { background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 24px 28px; }
.uc-head { margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid var(--border-subtle); }
.uc-head h2 { font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 2px; }
.hint { font-size: 13px; color: var(--text-tertiary); font-family: var(--font-mono); }

.fav-list { display: flex; flex-direction: column; gap: 12px; }
.fav-item { display: flex; align-items: center; padding: 16px 20px; gap: 12px; background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; cursor: pointer; transition: all 200ms; }
.fav-item:hover { border-color: rgba(0,240,255,0.08); }
.fav-item:hover h3 { color: var(--cyber-cyan); }
.fav-body { flex: 1; min-width: 0; }
.fav-meta { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.meta-cat { font-size: 11px; font-weight: 600; font-family: var(--font-mono); color: var(--cyber-cyan); }
.meta-date { font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); }
.fav-body h3 { font-size: 15px; font-weight: 600; margin: 4px 0; color: var(--text-primary); transition: color 200ms; }
.fav-body p { font-size: 13px; color: var(--text-tertiary); overflow: hidden; max-height: 2.7em; line-height: 1.35; }

.btn-unfav { flex-shrink: 0; padding: 6px 14px; font-size: 11px; font-weight: 500; border-radius: 4px; border: 1px solid var(--border-subtle); background: transparent; color: var(--text-tertiary); cursor: pointer; transition: all 150ms; font-family: inherit; white-space: nowrap; }
.btn-unfav:hover { border-color: rgba(124,58,237,0.15); color: var(--cyber-purple); background: rgba(124,58,237,0.04); }
</style>
