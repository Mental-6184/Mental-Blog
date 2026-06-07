<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <router-link to="/" class="sidebar-brand">
        <div class="brand-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7l10 5 10-5-10-5z" fill="var(--color-primary)"/>
            <path d="M2 17l10 5 10-5" stroke="var(--color-primary)" stroke-width="2" fill="none"/>
            <path d="M2 12l10 5 10-5" stroke="var(--color-primary)" stroke-width="2" fill="none"/>
          </svg>
        </div>
        <span class="brand-text">{{ siteName }}</span>
      </router-link>
      <nav class="sidebar-nav">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to" class="nav-item" active-class="active">
          <span class="nav-icon" v-html="item.icon"></span>
          <span>{{ item.label }}</span>
        </router-link>
        <div class="nav-divider"></div>
        <router-link to="/" class="nav-item nav-back">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5"/><polyline points="12 19 5 12 12 5"/></svg>
          <span>返回前台</span>
        </router-link>
      </nav>
    </aside>
    <div class="main-area">
      <header class="topbar">
        <h1 class="topbar-title">{{ pageTitle }}</h1>
        <div class="topbar-right">
          <button class="icon-btn" @click="toggleDark" :aria-label="isDark ? '亮色模式' : '深色模式'">
            <svg v-if="!isDark" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
          </button>
          <el-dropdown trigger="click" placement="bottom-end">
            <button class="user-btn">
              <span class="avatar avatar-sm">{{ displayName[0] }}</span>
              <span class="user-name">{{ displayName }}</span>
              <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor"><path d="M3 4.5l3 3 3-3" stroke="currentColor" stroke-width="1.5" fill="none"/></svg>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useTheme } from '@/composables/useTheme'
import { getSiteConfig } from '@/api/site'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { theme, toggle: toggleDark } = useTheme()

const siteName = ref('MentalBlog')
const displayName = computed(() => userStore.nickname || userStore.username || 'U')
const isDark = computed(() => theme.value === 'dark')

const pageTitle = computed(() => {
  const m: Record<string, string> = {
    Dashboard: '仪表盘', ArticleList: '文章管理', ArticleCreate: '写文章', ArticleEdit: '编辑文章',
    CategoryManage: '分类管理', TagManage: '标签管理', CommentManage: '评论管理',
    UserManage: '用户管理', AdminProfile: '个人资料', SiteSettings: '系统设置', FriendLinkManage: '友链管理', AdminImages: '图库管理', AdminImagePermission: '图库审批', AdminAbout: '关于我管理'
  }
  return m[route.name as string] || ''
})

const navItems = [
  { to: '/admin', label: '仪表盘', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>' },
  { to: '/admin/articles', label: '文章', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
  { to: '/admin/categories', label: '分类', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>' },
  { to: '/admin/tags', label: '标签', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2H2v10l9.29 9.29c.94.94 2.48.94 3.42 0l6.58-6.58c.94-.94.94-2.48 0-3.42L12 2z"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>' },
  { to: '/admin/comments', label: '评论', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>' },
  { to: '/admin/users', label: '用户', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>' },
  { to: '/admin/links', label: '友链', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>' },
  { to: '/admin/images', label: '图库', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5-5 5"/><path d="m11 17-2-2-6 6"/></svg>' },
  { to: '/admin/image-permission', label: '图库审批', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>' },
  { to: '/admin/settings', label: '设置', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>' },
  { to: '/admin/profile', label: '资料', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="12" r="4"/></svg>' },
  { to: '/admin/about', label: '关于我', icon: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>' },
]

onMounted(async () => { try { const r = await getSiteConfig(); if (r.data?.site_name) siteName.value = r.data.site_name } catch (e) { /* ignore */ } })
function handleLogout() { userStore.logout(); router.push('/login') }
</script>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; background: var(--color-bg); }
.sidebar { width: 240px; flex-shrink: 0; background: var(--color-bg-card); border-right: 1px solid var(--color-border); display: flex; flex-direction: column; position: sticky; top: 0; height: 100vh; }
.sidebar-brand { display: flex; align-items: center; gap: 10px; padding: 20px 24px; font-size: 16px; font-weight: 700; color: var(--color-text); text-decoration: none; border-bottom: 1px solid var(--color-border); }
.brand-text { background: linear-gradient(135deg, var(--color-text), var(--color-primary)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.sidebar-nav { flex: 1; padding: 12px; overflow-y: auto; }
.nav-item { display: flex; align-items: center; gap: 10px; padding: 10px 14px; font-size: 14px; font-weight: 500; color: var(--color-text-secondary); text-decoration: none; border-radius: var(--radius-lg); transition: all var(--transition-fast) var(--ease-out); margin-bottom: 2px; position: relative; overflow: hidden; }
.nav-item:hover { background: var(--color-bg-subtle); color: var(--color-text); }
.nav-item.active { background: var(--color-primary-light); color: var(--color-primary); font-weight: 600; }
.nav-icon { width: 20px; height: 20px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.nav-divider { height: 1px; background: var(--color-border); margin: 8px 14px; }
.nav-back { color: var(--color-text-tertiary); font-size: 13px; }
.nav-back:hover { color: var(--color-primary); }
.main-area { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.topbar { display: flex; align-items: center; justify-content: space-between; padding: 0 32px; height: 64px; border-bottom: 1px solid var(--color-border); background: var(--color-bg-glass); backdrop-filter: blur(16px); position: sticky; top: 0; z-index: 50; }
.topbar-title { font-size: 18px; font-weight: 700; letter-spacing: -0.3px; }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.icon-btn { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border: none; background: transparent; color: var(--color-text-secondary); border-radius: var(--radius-lg); cursor: pointer; transition: all var(--transition-fast) var(--ease-out); }
.icon-btn:hover { background: var(--color-bg-subtle); color: var(--color-text); }
.user-btn { display: flex; align-items: center; gap: 8px; padding: 6px 12px; border: 1px solid var(--color-border); border-radius: var(--radius-lg); background: var(--color-bg-card); cursor: pointer; transition: all var(--transition-fast) var(--ease-out); font-family: inherit; }
.user-btn:hover { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-ring); }
.user-name { font-size: 13px; font-weight: 500; color: var(--color-text); }
.content-area { padding: 28px 32px; flex: 1; }
@media (max-width: 768px) {
  .sidebar { width: 64px; }
  .sidebar .brand-text, .sidebar .nav-item span:not(.nav-icon), .nav-divider, .nav-back span { display: none; }
  .sidebar-brand { justify-content: center; padding: 16px; }
  .nav-item { justify-content: center; padding: 10px; }
  .topbar { padding: 0 16px; }
  .content-area { padding: 16px; }
}
</style>
