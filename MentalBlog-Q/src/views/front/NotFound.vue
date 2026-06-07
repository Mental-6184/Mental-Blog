<template>
  <div class="page-section"><div class="container">
    <div class="nf-wrapper">
      <div class="nf-code">
        <span class="nf-num">4</span>
        <span class="nf-zero">0</span>
        <span class="nf-num">4</span>
      </div>
      <h1 class="nf-title">Page Not Found</h1>
      <p class="nf-desc">The page you are looking for doesn't exist or has been moved.</p>
      <router-link to="/" class="btn btn-primary" style="margin-top:20px">Back to Home</router-link>
    </div>
  </div></div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { gsap } from 'gsap'
onMounted(() => {
  gsap.from('.nf-code', { opacity: 0, y: -20, duration: 0.5, ease: 'power3.out' })
  gsap.from('.nf-title', { opacity: 0, y: 10, duration: 0.5, delay: 0.15, ease: 'power3.out' })
  gsap.from('.nf-desc', { opacity: 0, y: 10, duration: 0.4, delay: 0.3, ease: 'power3.out' })
  gsap.from('.btn', { opacity: 0, y: 10, duration: 0.4, delay: 0.45, ease: 'power3.out' })
})
</script>
<style scoped>
.page-section { background: var(--bg-deep); min-height: calc(100vh - 64px); display: flex; align-items: center; }
.nf-wrapper { text-align: center; padding: 80px 24px; }
.nf-code { display: flex; align-items: center; justify-content: center; gap: 8px; margin-bottom: 20px; }
.nf-num { font-size: 96px; font-weight: 900; color: var(--cyber-cyan); line-height: 1; text-shadow: 0 0 40px rgba(0,240,255,0.2); }
.nf-zero { font-size: 96px; font-weight: 900; color: var(--cyber-purple); line-height: 1; text-shadow: 0 0 40px rgba(124,58,237,0.2); }
.nf-title { font-size: 24px; font-weight: 700; color: #fff; margin-bottom: 8px; }
.nf-desc { font-size: 14px; color: var(--text-tertiary); }
</style>
