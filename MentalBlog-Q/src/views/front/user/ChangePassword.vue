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
          <h2>Change Password</h2>
          <p class="hint">Regular password changes improve account security</p>
        </div>
        <form @submit.prevent="handleSave">
          <div class="field"><label>Current password</label><input v-model="form.oldPassword" type="password" class="input-cyber" autocomplete="current-password" /></div>
          <div class="field"><label>New password</label><input v-model="form.newPassword" type="password" class="input-cyber" autocomplete="new-password" /><p class="hint">Minimum 6 characters</p></div>
          <div class="form-actions">
            <button type="submit" class="btn-cyber" :disabled="saving||!form.oldPassword||!form.newPassword">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
              {{ saving?'Updating...':'Update password' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div></div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { updatePassword } from '@/api/user'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'
const saving=ref(false); const form=reactive({oldPassword:'',newPassword:''})
onMounted(() => { nextTick(() => { gsap.from(".user-tabs", { opacity: 0, y: -10, duration: 0.4, ease: "power3.out" }); gsap.from(".uc-card", { opacity: 0, y: 20, duration: 0.5, delay: 0.1, ease: "power3.out" }) }) });

async function handleSave(){saving.value=true;try{await updatePassword(form);ElMessage.success('Password updated');form.oldPassword='';form.newPassword=''}catch(e:any){ElMessage.error(e.message||'Update failed')}saving.value=false}
</script>
<style scoped>
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 32px 0 80px; }

.user-tabs { display: flex; gap: 4px; margin-bottom: 28px; padding: 4px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; overflow-x: auto; }
.tab-link { display: flex; align-items: center; gap: 6px; padding: 10px 18px; font-size: 14px; font-weight: 500; color: var(--text-secondary); text-decoration: none; border-radius: 8px; transition: all 200ms; white-space: nowrap; }
.tab-link:hover { color: var(--text-primary); background: rgba(0,240,255,0.03); }
.tab-link.active { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); font-weight: 600; }

.user-content { max-width: 540px; }
.uc-card { background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 28px; }
.uc-head { margin-bottom: 24px; }
.uc-head h2 { font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.hint { font-size: 13px; color: var(--text-tertiary); font-family: var(--font-mono); }

.field { margin-bottom: 20px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--text-secondary); font-family: var(--font-mono); }
.hint { font-size: 11px; color: var(--text-tertiary); margin-top: 4px; }

.input-cyber { width: 100%; height: 42px; padding: 0 14px; font-size: 14px; font-family: inherit; color: var(--text-primary); background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; outline: none; transition: all 200ms; box-sizing: border-box; }
.input-cyber:focus { border-color: rgba(0,240,255,0.2); box-shadow: 0 0 0 3px rgba(0,240,255,0.04); }
.input-cyber::placeholder { color: var(--text-tertiary); }

.form-actions { padding-top: 20px; border-top: 1px solid var(--border-subtle); margin-top: 8px; }
.btn-cyber { display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 250ms; font-family: var(--font-mono); letter-spacing: 0.5px; }
.btn-cyber:hover { box-shadow: 0 0 20px rgba(0,240,255,0.2); }
.btn-cyber:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
