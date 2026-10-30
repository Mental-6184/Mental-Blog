<template>
  <div class="page-section"><div class="auth-card">
    <h2>找回密码</h2><p>验证码将在 5 分钟后失效</p>
    <input v-model="email" class="input-cyber" type="email" placeholder="邮箱" />
    <div class="code-row"><input v-model="code" class="input-cyber" placeholder="验证码" /><button class="btn-cyber" :disabled="sending" @click="sendCode">{{ sending ? '发送中' : '获取验证码' }}</button></div>
    <input v-model="newPassword" class="input-cyber" type="password" placeholder="新密码（至少6位）" />
    <button class="btn-cyber-full" :disabled="loading" @click="submit">{{ loading ? '提交中…' : '重置密码' }}</button>
    <router-link to="/login">返回登录</router-link>
  </div></div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendEmailCode, resetPassword } from '@/api/auth'
const router = useRouter(); const email = ref(''); const code = ref(''); const newPassword = ref(''); const sending = ref(false); const loading = ref(false)
async function sendCode() { if (!email.value) return ElMessage.warning('请输入邮箱'); sending.value = true; try { await sendEmailCode(email.value); ElMessage.success('验证码已发送') } catch (e: any) { ElMessage.error(e.message) } finally { sending.value = false } }
async function submit() { if (!email.value || !code.value || newPassword.value.length < 6) return ElMessage.warning('请完整填写信息'); loading.value = true; try { await resetPassword({ email: email.value, code: code.value, newPassword: newPassword.value }); ElMessage.success('密码重置成功'); router.push('/login') } catch (e: any) { ElMessage.error(e.message) } finally { loading.value = false } }
</script>
<style scoped>
.page-section{min-height:60vh;padding:80px 20px;background:var(--bg-deep)}.auth-card{max-width:420px;margin:auto;padding:28px;background:var(--bg-elevated);border:1px solid var(--border-subtle);border-radius:12px;display:flex;flex-direction:column;gap:14px}.auth-card h2{margin:0;color:var(--text-primary)}.auth-card p{margin:0;color:var(--text-tertiary)}.code-row{display:flex;gap:8px}.code-row .input-cyber{flex:1}.auth-card a{color:var(--cyber-cyan);text-align:center}.forgot-link{float:right;font-size:12px;color:var(--cyber-cyan)}
</style>
