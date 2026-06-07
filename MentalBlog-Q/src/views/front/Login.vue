<template>
  <div class="page-section">
    <div class="auth-wrapper">
      <div class="auth-brand" ref="brandRef">
        <div class="brand-grid"></div>
        <div class="brand-terminal">
          <div class="t-bar">
            <div class="t-dots"><span></span><span></span><span></span></div>
            <span class="t-title">~/login — zsh</span>
          </div>
          <div class="t-body">
            <div class="t-line"><span class="prompt">$</span> <span class="cmd">echo "Welcome to MentalBlog"</span></div>
            <div class="t-line"><span class="out">Welcome to MentalBlog</span></div>
            <div class="t-line t-spacer"></div>
            <div class="t-line"><span class="prompt">$</span> <span class="cmd">whoami</span></div>
            <div class="t-line"><span class="out">Guest</span></div>
            <div class="t-line t-spacer"></div>
            <div class="t-line"><span class="prompt">$</span> <span class="cmd">ssh mentalblog.com</span></div>
            <div class="t-line"><span class="out">Connecting... [OK]</span></div>
            <div class="t-line t-spacer"></div>
            <div class="t-line"><span class="prompt">$</span> <span class="cursor">▌</span></div>
          </div>
        </div>
        <div class="brand-footer">Record growth · Share technology</div>
      </div>
      <div class="auth-form" ref="formRef">
        <div class="af-container">
          <div v-if="redirectMsg" class="tip-bar">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            {{ redirectMsg }}
          </div>
          <div class="af-header" ref="headerInnerRef">
            <h3>Welcome back</h3>
            <p>Sign in to your account</p>
          </div>
          <form @submit.prevent="handleLogin" ref="formInnerRef">
            <div class="field"><label>Username</label><input v-model="form.username" class="input-cyber" placeholder="Enter username" required /><span v-if="errors.username" class="field-err">{{ errors.username }}</span></div>
            <div class="field"><label>Password</label><input v-model="form.password" type="password" class="input-cyber" placeholder="Enter password" required minlength="6" /><span v-if="errors.password" class="field-err">{{ errors.password }}</span></div>
            <button type="submit" class="btn-cyber-full" :disabled="loading">{{ loading ? 'Signing in...' : 'Sign in' }}</button>
          </form>
          <p class="af-footer">No account? <router-link to="/register">Register</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const us = useUserStore()
const loading = ref(false)
const errors = reactive({ username: '', password: '' })

function validate(): boolean {
  errors.username = ''; errors.password = ''
  if (!form.username.trim()) errors.username = 'Username is required'
  else if (form.username.trim().length < 3) errors.username = 'At least 3 characters'
  if (!form.password) errors.password = 'Password is required'
  else if (form.password.length < 6) errors.password = 'At least 6 characters'
  return !errors.username && !errors.password
}
const form = reactive({ username: '', password: '' })
const redirectMsg = computed(() => (route.query.msg as string) || '')
const brandRef = ref<HTMLElement | null>(null)
const formRef = ref<HTMLElement | null>(null)
const headerInnerRef = ref<HTMLElement | null>(null)
const formInnerRef = ref<HTMLElement | null>(null)

onMounted(() => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })
    tl.from(brandRef.value, { opacity: 0, x: -30, duration: 0.6 }, 0)
    tl.from(formRef.value, { opacity: 0, x: 30, duration: 0.6 }, 0.15)
    tl.from(headerInnerRef.value, { opacity: 0, y: -10, duration: 0.4 }, 0.3)
    tl.from(formInnerRef.value?.querySelectorAll('.field'), { opacity: 0, y: 10, duration: 0.35, stagger: 0.08 }, 0.45)
    tl.from('.btn-cyber-full', { opacity: 0, y: 8, duration: 0.3 }, 0.65)
  })
})

async function handleLogin() {
  if (!validate()) return
  loading.value = true
  try {
    const r = await login(form)
    us.setLogin(r.data)
    ElMessage.success('Login successful')
    router.push((route.query.redirect as string) || '/')
  } catch (e: any) { ElMessage.error(e.message || 'Login failed') }
  loading.value = false
}
</script>
<style scoped>
.page-section { background: var(--bg-deep); min-height: calc(100vh - 64px); display: flex; align-items: center; justify-content: center; padding: 32px; }

.auth-wrapper { display: flex; width: 100%; max-width: 800px; border-radius: 12px; overflow: hidden; border: 1px solid rgba(0,240,255,0.08); min-height: 480px; }

.auth-brand { flex: 1; position: relative; display: none; background: #0A0A0F; align-items: center; justify-content: center; flex-direction: column; gap: 24px; padding: 32px; overflow: hidden; }
@media(min-width: 768px) { .auth-brand { display: flex; } }
.brand-grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(0,240,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,240,255,0.03) 1px, transparent 1px); background-size: 40px 40px; }
.brand-terminal { position: relative; z-index: 1; width: 100%; max-width: 300px; background: rgba(0,0,0,0.5); border: 1px solid rgba(0,240,255,0.1); border-radius: 8px; overflow: hidden; }
.t-bar { display: flex; align-items: center; gap: 8px; padding: 6px 12px; background: rgba(255,255,255,0.02); border-bottom: 1px solid rgba(0,240,255,0.06); }
.t-dots { display: flex; gap: 5px; }
.t-dots span { width: 6px; height: 6px; border-radius: 50%; }
.t-dots span:nth-child(1) { background: #FF5F57; }
.t-dots span:nth-child(2) { background: #FEBC2E; }
.t-dots span:nth-child(3) { background: #28C840; }
.t-title { font-size: 10px; color: rgba(255,255,255,0.25); font-family: var(--font-mono); margin-left: 4px; }
.t-body { padding: 12px 14px; font-family: var(--font-mono); font-size: 12px; line-height: 1.8; }
.t-spacer { height: 6px; }
.prompt { color: var(--cyber-cyan); margin-right: 6px; }
.cmd { color: var(--cyber-cyan); }
.out { color: var(--text-secondary); }
.cursor { color: var(--cyber-cyan); animation: blink 0.8s step-end infinite; }
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0; } }
.brand-footer { position: relative; z-index: 1; font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); letter-spacing: 1px; }

.auth-form { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 32px; background: var(--bg-elevated); }
.af-container { width: 100%; max-width: 340px; }

.tip-bar { display: flex; align-items: center; gap: 8px; padding: 10px 14px; margin-bottom: 20px; font-size: 13px; background: rgba(0,240,255,0.04); color: var(--cyber-cyan); border-radius: 8px; border: 1px solid rgba(0,240,255,0.1); }
.af-header { text-align: center; margin-bottom: 28px; }
.af-header h3 { font-size: 22px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.af-header p { font-size: 14px; color: var(--text-secondary); }

.field { margin-bottom: 18px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--text-secondary); font-family: var(--font-mono); letter-spacing: 0.5px; }
.input-cyber { width: 100%; height: 42px; padding: 0 14px; font-size: 14px; font-family: inherit; color: var(--text-primary); background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; outline: none; transition: all 0.2s; box-sizing: border-box; }
.input-cyber:focus { border-color: rgba(0,240,255,0.2); box-shadow: 0 0 0 3px rgba(0,240,255,0.04); }
.input-cyber::placeholder { color: var(--text-tertiary); }

.btn-cyber-full { display: flex; align-items: center; justify-content: center; width: 100%; padding: 12px; font-size: 14px; font-weight: 600; border-radius: 8px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 0.25s; font-family: var(--font-mono); letter-spacing: 1px; }
.btn-cyber-full:hover { box-shadow: 0 0 24px rgba(0,240,255,0.2); }
.btn-cyber-full:disabled { opacity: 0.4; cursor: not-allowed; }

.af-footer { text-align: center; margin-top: 20px; font-size: 13px; color: var(--text-tertiary); }
.af-footer a { color: var(--cyber-cyan); text-decoration: none; font-weight: 500; }
.af-footer a:hover { text-decoration: underline; }
.field-err { font-size: 12px; color: var(--cyber-red); margin-top: 4px; display: block; }
</style>

