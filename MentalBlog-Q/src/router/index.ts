import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/FrontLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/front/Home.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('@/views/front/ArticleDetail.vue'), meta: { requiresAuth: true } },
      { path: 'category/:slug', name: 'CategoryPage', component: () => import('@/views/front/CategoryPage.vue') },
      { path: 'tags', name: 'Tags', component: () => import('@/views/front/Tags.vue') },
      { path: 'tag/:slug', name: 'TagPage', component: () => import('@/views/front/TagPage.vue') },
      { path: 'archives', name: 'Archives', component: () => import('@/views/front/Archives.vue') },
      { path: 'search', name: 'Search', component: () => import('@/views/front/SearchResult.vue') },
      { path: 'about', name: 'About', component: () => import('@/views/front/About.vue') },
      { path: 'links', name: 'Links', component: () => import('@/views/front/FriendLinks.vue') },
      { path: 'gallery', name: 'Gallery', component: () => import('@/views/front/Gallery.vue'), meta: { requiresAuth: true } },
      { path: 'gallery/upload', name: 'GalleryUpload', component: () => import('@/views/front/GalleryUpload.vue'), meta: { requiresAuth: true } },
      { path: 'gallery/:id', name: 'GalleryDetail', component: () => import('@/views/front/GalleryDetail.vue'), meta: { requiresAuth: true } },
      { path: 'login', name: 'Login', component: () => import('@/views/front/Login.vue') },
      { path: 'register', name: 'Register', component: () => import('@/views/front/Register.vue') },
    ]
  },
  {
    path: '/user',
    component: () => import('@/layouts/FrontLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/user/profile' },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/front/user/Profile.vue') },
      { path: 'comments', name: 'MyComments', component: () => import('@/views/front/user/MyComments.vue') },
      { path: 'favorites', name: 'MyFavorites', component: () => import('@/views/front/user/MyFavorites.vue') },
      { path: 'password', name: 'ChangePassword', component: () => import('@/views/front/user/ChangePassword.vue') },
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'articles', name: 'ArticleList', component: () => import('@/views/admin/ArticleList.vue') },
      { path: 'articles/create', name: 'ArticleCreate', component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'articles/:id/edit', name: 'ArticleEdit', component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'categories', name: 'CategoryManage', component: () => import('@/views/admin/CategoryManage.vue') },
      { path: 'tags', name: 'TagManage', component: () => import('@/views/admin/TagManage.vue') },
      { path: 'comments', name: 'CommentManage', component: () => import('@/views/admin/CommentManage.vue') },
      { path: 'users', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'profile', name: 'AdminProfile', component: () => import('@/views/admin/ProfileEdit.vue') },
      { path: 'settings', name: 'SiteSettings', component: () => import('@/views/admin/SiteSettings.vue') },
      { path: 'links', name: 'FriendLinkManage', component: () => import('@/views/admin/FriendLinkManage.vue') },
      { path: 'about', name: 'AdminAbout', component: () => import('@/views/admin/AdminAboutManage.vue') },
      { path: 'images', name: 'AdminImages', component: () => import('@/views/admin/ImageManage.vue') },
      { path: 'image-permission', name: 'AdminImagePermission', component: () => import('@/views/admin/ImagePermissionManage.vue') },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

const siteTitle = 'MentalBlog'
const pageTitles: Record<string, string> = {
  Home: 'Home', ArticleDetail: 'Article', CategoryPage: 'Category', Tags: 'Tags',
  TagPage: 'Tag', Archives: 'Archives', Search: 'Search', About: 'About',
  Links: 'Links', Gallery: 'Gallery', GalleryUpload: 'Upload', GalleryDetail: 'Gallery',
  Login: 'Login', Register: 'Register',
  UserProfile: 'My Profile', MyComments: 'My Comments', MyFavorites: 'My Favorites',
  ChangePassword: 'Change Password',
  Dashboard: 'Dashboard', ArticleList: 'Articles', ArticleCreate: 'New Article',
  ArticleEdit: 'Edit Article', CategoryManage: 'Categories', TagManage: 'Tags',
  CommentManage: 'Comments', UserManage: 'Users', AdminProfile: 'Profile',
  SiteSettings: 'Settings', FriendLinkManage: 'Links', AdminImages: 'Images',
  AdminImagePermission: 'Permissions', AdminAbout: 'About Me',
}

router.beforeEach((to, _from, next) => {
  document.title = to.name && pageTitles[to.name as string]
    ? `${pageTitles[to.name as string]} — ${siteTitle}`
    : siteTitle

  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  if (to.meta.requiresAdmin && role !== 'ADMIN') {
    next({ name: 'Home' })
    return
  }
  if (token && (to.name === 'Login' || to.name === 'Register')) {
    next({ name: 'Home' })
    return
  }
  next()
})

export default router
