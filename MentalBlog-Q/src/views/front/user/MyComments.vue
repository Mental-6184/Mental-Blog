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
          <h2>My Comments</h2>
          <p class="hint">{{ comments.length }} total</p>
        </div>
        <div v-if="loading" class="empty-state"><p>Loading...</p></div>
        <div v-else-if="comments.length===0" class="empty-state"><p>No comments yet</p></div>
        <div v-else class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="ci-head">
              <router-link :to="`/article/${c.articleId}`" class="ci-link">View article &rarr;</router-link>
              <span :class="['status-badge', c.status===1 ? 'approved' : 'pending']">{{ c.status===1 ? 'Approved' : 'Pending' }}</span>
            </div>
            <p class="ci-text">{{ c.content }}</p>
            <div class="ci-foot">
              <span>{{ c.createdAt?.substring(0,10) }}</span>
              <button class="btn-danger-sm" @click="handleDelete(c.id)">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div></div>
</template>
<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { getMyComments, deleteComment } from '@/api/comment'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'
const comments=ref<any[]>([]); const loading=ref(false)
onMounted(async () => { nextTick(() => { gsap.from(".user-tabs", { opacity: 0, y: -10, duration: 0.4, ease: "power3.out" }); gsap.from(".uc-card", { opacity: 0, y: 20, duration: 0.5, delay: 0.1, ease: "power3.out" }) }); loading.value=true; try{const r=await getMyComments();comments.value=r.data||[]}catch(e){} loading.value=false })
async function handleDelete(id:number){try{await deleteComment(id);ElMessage.success('Deleted');comments.value=comments.value.filter((c:any)=>c.id!==id)}catch(e){}}
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

.comment-list { display: flex; flex-direction: column; gap: 12px; }
.comment-item { padding: 16px; background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; transition: all 200ms; }
.comment-item:hover { border-color: rgba(0,240,255,0.08); }
.ci-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.ci-link { font-size: 13px; font-weight: 500; color: var(--cyber-cyan); text-decoration: none; }
.ci-link:hover { text-decoration: underline; }
.status-badge { font-size: 11px; font-weight: 600; font-family: var(--font-mono); padding: 2px 10px; border-radius: 4px; }
.status-badge.approved { background: rgba(0,240,255,0.06); color: var(--cyber-cyan); border: 1px solid rgba(0,240,255,0.1); }
.status-badge.pending { background: rgba(124,58,237,0.06); color: var(--cyber-purple); border: 1px solid rgba(124,58,237,0.1); }
.ci-text { font-size: 14px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 8px; }
.ci-foot { display: flex; align-items: center; justify-content: space-between; font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); }
.btn-danger-sm { padding: 4px 12px; font-size: 11px; font-weight: 500; border-radius: 4px; border: 1px solid rgba(124,58,237,0.1); background: transparent; color: var(--text-tertiary); cursor: pointer; transition: all 150ms; font-family: inherit; }
.btn-danger-sm:hover { border-color: rgba(124,58,237,0.2); color: var(--cyber-purple); background: rgba(124,58,237,0.04); }
</style>
