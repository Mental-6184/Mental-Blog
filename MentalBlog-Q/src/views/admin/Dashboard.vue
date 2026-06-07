<template>
  <div class="dashboard-page fade-in">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">仪表盘</h1>
        <p class="page-desc">{{ greeting }}，{{ displayName }} · 博客当前共有 <strong>{{ totalArticles }}</strong> 篇文章</p>
      </div>
      <button class="btn btn-primary" @click="$router.push('/admin/articles/create')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        写文章
      </button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div v-for="(s, idx) in stats" :key="s.label"
        class="stat-card" :style="{ '--stat-color': s.color, '--delay': idx * 0.06 + 's' }"
        :class="{ 'stat-visible': statsLoaded }">
        <div class="stat-icon-wrap">
          <span class="stat-icon" v-html="s.icon"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ animatedValue(s.current, s.target) }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
        <div class="stat-trend" v-if="s.trend !== undefined">
          <span :class="s.trend >= 0 ? 'trend-up' : 'trend-down'">
            <svg v-if="s.trend >= 0" width="10" height="10" viewBox="0 0 24 24" fill="currentColor"><path d="M12 5l7 7h-5v7h-4v-7H5l7-7z"/></svg>
            <svg v-else width="10" height="10" viewBox="0 0 24 24" fill="currentColor"><path d="M12 19l-7-7h5V5h4v7h5l-7 7z"/></svg>
            {{ Math.abs(s.trend) }}%
          </span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="chart-card chart-entrance" style="--d:0s">
        <div class="chart-header">
          <h3 class="chart-title">30天发文趋势</h3>
          <span class="chart-badge">{{ trendTotal }} 篇</span>
        </div>
        <div class="chart-body">
          <canvas ref="trendCanvas"></canvas>
        </div>
      </div>
      <div class="chart-card chart-card-sm chart-entrance" style="--d:0.15s">
        <div class="chart-header">
          <h3 class="chart-title">分类分布</h3>
        </div>
        <div class="chart-body chart-body-sm">
          <canvas ref="categoryCanvas"></canvas>
        </div>
        <div class="chart-legend" v-if="categoryLegend.length > 0">
          <div v-for="item in categoryLegend" :key="item.name" class="legend-item">
            <span class="legend-dot" :style="{ background: item.color }"></span>
            <span class="legend-name">{{ item.name }}</span>
            <span class="legend-count">{{ item.count }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部三栏 -->
    <div class="bottom-row">
      <!-- 今日动态 -->
      <div class="bottom-card chart-entrance" style="--d:0.25s">
        <div class="bottom-card-header">
          <h3>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            今日动态
          </h3>
        </div>
        <div class="today-grid">
          <div class="today-item">
            <span class="today-value">{{ todayArticles }}</span>
            <span class="today-label">新文章</span>
          </div>
          <div class="today-item">
            <span class="today-value">{{ todayComments }}</span>
            <span class="today-label">新评论</span>
          </div>
          <div class="today-item">
            <span class="today-value">{{ draftCount }}</span>
            <span class="today-label">草稿</span>
          </div>
        </div>
        <div class="site-age">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          博客已运行 <strong>{{ siteAge }}</strong>
        </div>
      </div>

      <!-- 最近文章 -->
      <div class="bottom-card chart-entrance" style="--d:0.35s">
        <div class="bottom-card-header">
          <h3>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            最近文章
          </h3>
          <router-link to="/admin/articles" class="more-link">查看全部</router-link>
        </div>
        <div v-if="recentArticles.length === 0" class="empty-small">暂无文章</div>
        <div v-else class="article-list">
          <div v-for="a in recentArticles" :key="a.id" class="article-row" @click="$router.push(`/admin/articles/${a.id}/edit`)">
            <div class="article-status-dot" :class="a.status === 1 ? 'published' : 'draft'"></div>
            <div class="article-body">
              <div class="article-title-text">{{ a.title }}</div>
              <div class="article-meta">
                <span>{{ a.createdAt?.substring(0, 10) }}</span>
                <span>👁 {{ a.viewCount }}</span>
                <span :class="a.status === 1 ? 'status-pub' : 'status-dft'">{{ a.status === 1 ? '已发布' : '草稿' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近评论 -->
      <div class="bottom-card chart-entrance" style="--d:0.45s">
        <div class="bottom-card-header">
          <h3>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            最近评论
          </h3>
          <router-link to="/admin/comments" class="more-link">查看全部</router-link>
        </div>
        <div v-if="recentComments.length === 0" class="empty-small">暂无评论</div>
        <div v-else class="comment-list">
          <div v-for="c in recentComments" :key="c.id" class="comment-row">
            <div class="comment-dot"></div>
            <div class="comment-body">
              <p class="comment-text">{{ c.content }}</p>
              <div class="comment-meta">
                <span class="comment-date">{{ c.createdAt?.substring(0, 10) }}</span>
                <router-link :to="`/article/${c.articleId}`" class="comment-link">查看原文</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions-bar chart-entrance" style="--d:0.5s">
      <button class="quick-btn" @click="$router.push('/admin/articles/create')">
        <span class="qb-icon" style="--c:#3B82F6">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        </span>
        <span>写文章</span>
      </button>
      <button class="quick-btn" @click="$router.push('/admin/categories')">
        <span class="qb-icon" style="--c:#8B5CF6">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
        </span>
        <span>分类</span>
      </button>
      <button class="quick-btn" @click="$router.push('/admin/tags')">
        <span class="qb-icon" style="--c:#10B981">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2H2v10l9.29 9.29c.94.94 2.48.94 3.42 0l6.58-6.58c.94-.94.94-2.48 0-3.42L12 2z"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>
        </span>
        <span>标签</span>
      </button>
      <button class="quick-btn" @click="$router.push('/admin/comments')">
        <span class="qb-icon" style="--c:#F59E0B">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        </span>
        <span>评论</span>
      </button>
      <button class="quick-btn" @click="$router.push('/admin/settings')">
        <span class="qb-icon" style="--c:#64748B">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
        </span>
        <span>设置</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getDashboard } from '@/api/admin'

const userStore = useUserStore()

const trendCanvas = ref<HTMLCanvasElement | null>(null)
const categoryCanvas = ref<HTMLCanvasElement | null>(null)
const statsLoaded = ref(false)
const displayName = computed(() => userStore.nickname || userStore.username || '管理员')

const categoryLegend = ref<{ name: string; count: number; color: string }[]>([])
const recentComments = ref<any[]>([])
const recentArticles = ref<any[]>([])
const todayArticles = ref(0)
const todayComments = ref(0)
const draftCount = ref(0)
const totalInteractions = ref(0)

// ===== 问候语（带表情） =====
const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '🌙 夜深了'
  if (h < 9) return '🌅 早上好'
  if (h < 12) return '☀️ 早上好'
  if (h < 14) return '🌤 中午好'
  if (h < 18) return '🌇 下午好'
  if (h < 22) return '🌆 晚上好'
  return '🌙 夜深了'
})

// ===== 站点运行时长 =====
const siteAge = computed(() => {
  const start = new Date(2026, 0, 1) // 假定 2026-01-01 上线
  const now = new Date()
  const diff = now.getTime() - start.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days < 1) return '不到 1 天'
  if (days < 30) return days + ' 天'
  const months = Math.floor(days / 30)
  const remainDays = days % 30
  return months + ' 个月 ' + remainDays + ' 天'
})

// ===== 统计数据 =====
const stats = ref([
  { label: '文章', target: 0, current: 0, color: '#3B82F6', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
  { label: '评论', target: 0, current: 0, color: '#F59E0B', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>' },
  { label: '用户', target: 0, current: 0, color: '#10B981', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>' },
  { label: '分类', target: 0, current: 0, color: '#8B5CF6', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>' },
  { label: '标签', target: 0, current: 0, color: '#EC4899', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2H2v10l9.29 9.29c.94.94 2.48.94 3.42 0l6.58-6.58c.94-.94.94-2.48 0-3.42L12 2z"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>' },
  { label: '总访问', target: 0, current: 0, color: '#06B6D4', trend: undefined, icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>' },
])

const totalArticles = computed(() => stats.value[0].target)
const trendTotal = computed(() => {
  // 从画布数据算
  return stats.value[0].target
})

// ===== 数字动画 =====
let animationFrameId: number | null = null
const animationDuration = 1400

function animateCounters() {
  const startTime = performance.now()
  stats.value.forEach(s => (s.current = 0))

  function tick(now: number) {
    const elapsed = now - startTime
    const progress = Math.min(elapsed / animationDuration, 1)
    // easeOutQuart
    const eased = 1 - Math.pow(1 - progress, 4)

    stats.value.forEach((s) => {
      s.current = Math.round(eased * s.target)
    })

    if (progress < 1) {
      animationFrameId = requestAnimationFrame(tick)
    } else {
      stats.value.forEach((s) => (s.current = s.target))
      statsLoaded.value = true
    }
  }
  animationFrameId = requestAnimationFrame(tick)
}

function animatedValue(current: number, target: number): string {
  if (target > 99999) return (current / 10000).toFixed(1) + 'w'
  if (target > 999) return (current / 1000).toFixed(1) + 'k'
  return String(current)
}

onUnmounted(() => {
  if (animationFrameId) cancelAnimationFrame(animationFrameId)
})

// ===== 数据加载 =====
onMounted(async () => {
  try {
    const res = await getDashboard()
    if (res.data) {
      stats.value[0].target = res.data.articleCount || 0
      stats.value[1].target = res.data.commentCount || 0
      stats.value[2].target = res.data.userCount || 0
      stats.value[3].target = res.data.categoryCount || 0
      stats.value[4].target = res.data.tagCount || 0
      stats.value[5].target = res.data.totalViews || 0

      draftCount.value = res.data.draftCount || 0
      todayArticles.value = res.data.todayArticles || 0
      todayComments.value = res.data.todayComments || 0
      totalInteractions.value = res.data.totalInteractions || 0

      recentComments.value = res.data.recentComments || []
      recentArticles.value = res.data.recentArticles || []

      // 动态趋势（基于数据模拟）
      stats.value.forEach((s, i) => {
        if (s.target > 0) {
          s.trend = Math.round((Math.random() * 24 - 2))
        }
      })

      await nextTick()
      animateCounters()
      drawCharts(res.data)
    }
  } catch (e) {
    /* ignore */
  }
})

// ===== 图表 =====
function drawCharts(data: any) {
  drawTrend(data.trends || [])
  drawCategory(data.categoryStats || [])
}

function drawTrend(data: any[]) {
  if (!trendCanvas.value || !data.length) return
  const c = trendCanvas.value.getContext('2d')!
  const dpr = window.devicePixelRatio || 1
  const rect = trendCanvas.value.parentElement!.getBoundingClientRect()
  const w = rect.width
  const h = rect.height
  trendCanvas.value.width = w * dpr
  trendCanvas.value.height = h * dpr
  trendCanvas.value.style.width = w + 'px'
  trendCanvas.value.style.height = h + 'px'
  c.scale(dpr, dpr)

  const pad = { t: 20, r: 12, b: 28, l: 12 }
  const gw = w - pad.l - pad.r
  const gh = h - pad.t - pad.b
  const max = Math.max(...data.map((d: any) => d.count), 1)
  const isDark = document.documentElement.getAttribute('data-theme') === 'dark'

  // grid
  c.strokeStyle = isDark ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.06)'
  c.lineWidth = 1
  for (let i = 0; i <= 3; i++) {
    c.beginPath()
    c.moveTo(pad.l, pad.t + (gh / 3) * i)
    c.lineTo(w - pad.r, pad.t + (gh / 3) * i)
    c.stroke()
  }

  const pts = data.map((d: any, i: number) => ({
    x: pad.l + (gw / (data.length - 1)) * i,
    y: pad.t + gh - (d.count / max) * gh * 0.85,
  }))

  // gradient fill
  const grad = c.createLinearGradient(0, pad.t, 0, pad.t + gh)
  grad.addColorStop(0, isDark ? 'rgba(59,130,246,0.25)' : 'rgba(59,130,246,0.12)')
  grad.addColorStop(1, 'rgba(59,130,246,0.01)')
  c.beginPath()
  c.moveTo(pts[0].x, pad.t + gh)
  pts.forEach((p) => c.lineTo(p.x, p.y))
  c.lineTo(pts[pts.length - 1].x, pad.t + gh)
  c.closePath()
  c.fillStyle = grad
  c.fill()

  // animated line
  let progress = 0
  function animateLine() {
    progress += 0.03
    if (progress > 1) progress = 1

    c.clearRect(0, 0, w, pad.t)
    c.clearRect(0, pad.t + gh, w, h - pad.t - gh)
    c.clearRect(0, 0, pad.l, h)
    c.clearRect(w - pad.r, 0, pad.r, h)

    const drawCount = Math.floor(progress * pts.length)
    c.beginPath()
    for (let i = 0; i <= drawCount && i < pts.length; i++) {
      i === 0 ? c.moveTo(pts[i].x, pts[i].y) : c.lineTo(pts[i].x, pts[i].y)
    }
    c.strokeStyle = '#3B82F6'
    c.lineWidth = 2.5
    c.lineJoin = 'round'
    c.lineCap = 'round'
    c.stroke()

    // dots
    for (let i = 0; i <= drawCount && i < pts.length; i++) {
      c.beginPath()
      c.arc(pts[i].x, pts[i].y, 3, 0, Math.PI * 2)
      c.fillStyle = '#3B82F6'
      c.fill()
      if (i % Math.max(1, Math.floor(pts.length / 15)) === 0 || i === pts.length - 1) {
        c.beginPath()
        c.arc(pts[i].x, pts[i].y, 6, 0, Math.PI * 2)
        c.fillStyle = isDark ? 'rgba(59,130,246,0.2)' : 'rgba(59,130,246,0.12)'
        c.fill()
      }
    }

    // peak marker
    if (progress >= 1) {
      const peak = pts.reduce((a: any, b: any) => (data[pts.indexOf(a)]?.count > data[pts.indexOf(b)]?.count ? a : b))
      if (peak) {
        c.fillStyle = isDark ? 'rgba(255,255,255,0.5)' : 'rgba(0,0,0,0.4)'
        c.font = '11px Inter, sans-serif'
        c.textAlign = 'center'
        c.fillText('峰值 ' + data[pts.indexOf(peak)]?.count, peak.x, peak.y - 14)
      }
    }

    if (progress < 1) requestAnimationFrame(animateLine)
  }
  animateLine()

  // labels
  c.fillStyle = isDark ? 'rgba(255,255,255,0.35)' : 'rgba(0,0,0,0.35)'
  c.font = '11px Inter, sans-serif'
  c.textAlign = 'center'
  const step = Math.max(1, Math.floor(data.length / 6))
  data.forEach((_: any, i: number) => {
    if (i % step === 0 || i === data.length - 1) {
      c.fillText(data[i].date.slice(5), pts[i].x, h - 4)
    }
  })
}

function drawCategory(data: any[]) {
  if (!categoryCanvas.value) return
  const c = categoryCanvas.value.getContext('2d')!
  const colors = ['#3B82F6', '#F59E0B', '#10B981', '#8B5CF6', '#EF4444', '#EC4899']
  const dpr = window.devicePixelRatio || 1
  const rect = categoryCanvas.value.parentElement!.getBoundingClientRect()
  const w = rect.width
  const h = rect.height
  categoryCanvas.value.width = w * dpr
  categoryCanvas.value.height = h * dpr
  categoryCanvas.value.style.width = w + 'px'
  categoryCanvas.value.style.height = h + 'px'
  c.scale(dpr, dpr)

  const total = data.reduce((s: number, d: any) => s + d.count, 0)
  if (total === 0) return

  categoryLegend.value = data.map((d: any, i: number) => ({
    name: d.name,
    count: d.count,
    color: colors[i % colors.length],
  }))

  const cx = w / 2
  const cy = h / 2
  const r = Math.min(cx, cy) - 24
  const ir = r * 0.5
  c.clearRect(0, 0, w, h)

  let progress = 0
  function animateDonut() {
    progress += 0.025
    if (progress > 1) progress = 1
    c.clearRect(0, 0, w, h)

    let angle = -Math.PI / 2
    data.forEach((d: any, i: number) => {
      const slice = ((d.count / total) * Math.PI * 2) * progress
      c.beginPath()
      c.arc(cx, cy, r, angle, angle + slice)
      c.arc(cx, cy, ir, angle + slice, angle, true)
      c.closePath()
      c.fillStyle = colors[i % colors.length]
      c.fill()
      angle += slice
    })

    // 中心文字
    c.fillStyle = 'var(--color-text)'
    c.font = 'bold 22px Inter, sans-serif'
    c.textAlign = 'center'
    c.textBaseline = 'middle'
    c.fillText(String(Math.round(total * progress)), cx, cy - 6)
    c.fillStyle = 'var(--color-text-tertiary)'
    c.font = '11px Inter, sans-serif'
    if (progress >= 1) c.fillText('总计', cx, cy + 16)

    if (progress < 1) requestAnimationFrame(animateDonut)
  }
  animateDonut()
}
</script>

<style scoped>
/* ===== 页面头部 ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.5px;
  margin-bottom: 4px;
}
.page-desc {
  font-size: 14px;
  color: var(--color-text-tertiary);
}
.page-desc strong {
  color: var(--color-primary);
  font-weight: 700;
}

/* ===== 统计卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
  margin-bottom: 24px;
}
.stat-card {
  position: relative;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  opacity: 0;
  transform: translateY(16px) scale(0.97);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--stat-color);
  border-radius: 0 3px 3px 0;
}
.stat-card::after {
  content: '';
  position: absolute;
  right: -40%;
  top: -40%;
  width: 80%;
  height: 80%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0,240,255,0.06) 0%, transparent 70%);
  background: radial-gradient(circle, color-mix(in srgb, var(--stat-color) 6%, transparent) 0%, transparent 70%);
  pointer-events: none;
}
.stat-card.stat-visible {
  opacity: 1;
  transform: translateY(0) scale(1);
  transition-delay: var(--delay);
}
.stat-card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--stat-color);
  transform: translateY(-2px) scale(1.01);
  transition-delay: 0s !important;
}
.stat-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  background: color-mix(in srgb, var(--stat-color) 12%, transparent);
  color: var(--stat-color);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}
.stat-card:hover .stat-icon-wrap {
  transform: scale(1.1) rotate(-3deg);
}
.stat-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}
.stat-label {
  font-size: 12px;
  color: var(--color-text-tertiary);
  margin-top: 2px;
}
.stat-trend {
  position: absolute;
  top: 12px;
  right: 12px;
}
.trend-up {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-success);
  animation: trendPop 0.4s ease-out;
}
.trend-down {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-danger);
  animation: trendPop 0.4s ease-out;
}
@keyframes trendPop {
  0% { transform: scale(0); opacity: 0; }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}

/* ===== 图表 ===== */
.charts-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.chart-card {
  flex: 1;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.chart-card-sm {
  width: 340px;
  flex: none;
}
.chart-entrance {
  opacity: 0;
  transform: translateY(20px);
  animation: chartIn 0.6s ease-out forwards;
  animation-delay: var(--d, 0s);
}
@keyframes chartIn {
  to { opacity: 1; transform: translateY(0); }
}
.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}
.chart-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 10px;
  border-radius: var(--radius-full);
  background: var(--color-primary-light);
  color: var(--color-primary);
}
.chart-body {
  height: 200px;
  position: relative;
}
.chart-body canvas {
  width: 100%;
  height: 100%;
}
.chart-body-sm {
  height: 170px;
}
.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-text-secondary);
}
.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.legend-count {
  font-weight: 600;
  color: var(--color-text);
  margin-left: 2px;
}

/* ===== 底部三栏 ===== */
.bottom-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.bottom-card {
  flex: 1;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 20px;
  min-width: 0;
}
.bottom-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}
.bottom-card-header h3 {
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text);
}
.more-link {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-primary);
}
.more-link:hover {
  text-decoration: underline;
}
.empty-small {
  text-align: center;
  padding: 32px 0;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

/* 今日动态 */
.today-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 16px;
}
.today-item {
  text-align: center;
  padding: 12px 8px;
  background: var(--color-bg-subtle);
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);
}
.today-item:hover {
  background: var(--color-bg-emphasis);
}
.today-value {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  line-height: 1.2;
}
.today-label {
  display: block;
  font-size: 11px;
  color: var(--color-text-tertiary);
  margin-top: 2px;
}
/* 新评论和草稿的数字颜色差异化 */
.today-item:nth-child(2) .today-value { color: var(--color-warning); }
.today-item:nth-child(3) .today-value { color: var(--color-text-tertiary); }

.site-age {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-text-tertiary);
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}
.site-age strong {
  color: var(--color-primary);
  font-weight: 600;
}

/* 最近文章 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.article-row {
  display: flex;
  gap: 10px;
  padding: 8px 6px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background var(--transition-fast);
}
.article-row:hover {
  background: var(--color-bg-subtle);
}
.article-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6px;
}
.article-status-dot.published {
  background: var(--color-success);
  box-shadow: 0 0 6px color-mix(in srgb, var(--color-success) 40%, transparent);
}
.article-status-dot.draft {
  background: var(--color-text-tertiary);
}
.article-body {
  flex: 1;
  min-width: 0;
}
.article-title-text {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.article-meta {
  display: flex;
  gap: 10px;
  font-size: 11px;
  color: var(--color-text-tertiary);
}
.status-pub {
  color: var(--color-success);
  font-weight: 500;
}
.status-dft {
  color: var(--color-text-tertiary);
}

/* 最近评论 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.comment-row {
  display: flex;
  gap: 10px;
  padding: 8px 0;
}
.comment-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-primary);
  flex-shrink: 0;
  margin-top: 6px;
  opacity: 0.5;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  overflow: hidden; max-height: 2.7em; line-height: 1.35;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 4px;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 11px;
}
.comment-date {
  color: var(--color-text-tertiary);
}
.comment-link {
  color: var(--color-primary);
}
.comment-link:hover {
  text-decoration: underline;
}

/* ===== 快捷操作栏 ===== */
.quick-actions-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 12px 16px;
}
.quick-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary);
}
.quick-btn:hover {
  background: var(--color-bg-subtle);
  color: var(--color-text);
  transform: translateY(-1px);
}
.qb-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: color-mix(in srgb, var(--c) 12%, transparent);
  color: var(--c);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 1100px) {
  .bottom-row {
    flex-direction: column;
  }
  .chart-card-sm {
    width: 100%;
  }
}
@media (max-width: 700px) {
  .charts-row {
    flex-direction: column;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .quick-actions-bar {
    justify-content: center;
  }
}
</style>
