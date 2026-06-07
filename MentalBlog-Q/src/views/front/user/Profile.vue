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
        <div class="uc-header">
          <div class="avatar-cyber">{{ displayName[0] }}</div>
          <div>
            <h2>{{ displayName }}</h2>
            <div class="uc-meta">
              <span class="role-badge">{{ userStore.role==='ADMIN'?'ADMIN':'USER' }}</span>
            </div>
          </div>
        </div>
        <div class="divider-cyber"></div>
        <form @submit.prevent="handleSave">
          <div class="field"><label>Nickname</label><input v-model="form.nickname" class="input-cyber" /><p class="hint">Displayed on articles and comments</p></div>
          <div class="field"><label>Bio</label><textarea v-model="form.bio" class="input-cyber" rows="3"></textarea></div>
          <div class="field"><label>Avatar URL</label><input v-model="form.avatar" class="input-cyber" /><p class="hint">Leave empty to use initial letter</p></div>
          <div class="form-actions">
            <button type="submit" class="btn-cyber" :disabled="saving">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
              {{ saving?'Saving...':'Save changes' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div></div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateProfile } from '@/api/user'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'
const userStore=useUserStore(); const displayName=computed(()=>userStore.nickname||userStore.username||'U'); const saving=ref(false); const form=reactive({nickname:'',bio:'',avatar:''})
onMounted(async () => { nextTick(() => { gsap.from(".user-tabs", { opacity: 0, y: -10, duration: 0.4, ease: "power3.out" }); gsap.from(".uc-card", { opacity: 0, y: 20, duration: 0.5, delay: 0.1, ease: "power3.out" }) }); try{const r=await getUserInfo();if(r.data) Object.assign(form,r.data)}catch(e){} })
async function handleSave() { saving.value=true; try{await updateProfile(form);ElMessage.success('Saved');userStore.nickname=form.nickname}catch(e:any){ElMessage.error(e.message||'Save failed')} saving.value=false }
</script>
<style scoped>
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 32px 0 80px; }

.user-tabs { display: flex; gap: 4px; margin-bottom: 28px; padding: 4px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; overflow-x: auto; }
.tab-link { display: flex; align-items: center; gap: 6px; padding: 10px 18px; font-size: 14px; font-weight: 500; color: var(--text-secondary); text-decoration: none; border-radius: 8px; transition: all 200ms; white-space: nowrap; }
.tab-link:hover { color: var(--text-primary); background: rgba(0,240,255,0.03); }
.tab-link.active { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); font-weight: 600; }

.user-content { max-width: 640px; }

.uc-card { background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 28px; }
.uc-header { display: flex; align-items: center; gap: 16px; margin-bottom: 8px; }
.uc-header h2 { font-size: 20px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.uc-meta { display: flex; align-items: center; gap: 10px; }
.avatar-cyber { width: 56px; height: 56px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 700; flex-shrink: 0; font-family: var(--font-mono); box-shadow: 0 0 20px rgba(0,240,255,0.1); }
.role-badge { font-size: 11px; font-weight: 600; font-family: var(--font-mono); padding: 2px 10px; border-radius: 4px; background: rgba(0,240,255,0.06); border: 1px solid rgba(0,240,255,0.1); color: var(--cyber-cyan); letter-spacing: 0.5px; }
.divider-cyber { height: 1px; background: var(--border-subtle); margin: 20px 0; }

.field { margin-bottom: 20px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--text-secondary); font-family: var(--font-mono); }
.hint { font-size: 11px; color: var(--text-tertiary); margin-top: 4px; }

.input-cyber { width: 100%; height: 42px; padding: 0 14px; font-size: 14px; font-family: inherit; color: var(--text-primary); background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; outline: none; transition: all 200ms; box-sizing: border-box; }
.input-cyber:focus { border-color: rgba(0,240,255,0.2); box-shadow: 0 0 0 3px rgba(0,240,255,0.04); }
textarea.input-cyber { height: auto; padding: 10px 14px; resize: vertical; }

.form-actions { padding-top: 20px; border-top: 1px solid var(--border-subtle); margin-top: 8px; }
.btn-cyber { display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 250ms; font-family: var(--font-mono); letter-spacing: 0.5px; }
.btn-cyber:hover { box-shadow: 0 0 20px rgba(0,240,255,0.2); }
.btn-cyber:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
