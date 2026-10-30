import request from '@/api/request'

export function getDashboard() {
  return request.get('/admin/dashboard')
}

export function getAdminArticles(params: { page?: number; pageSize?: number; status?: number; categoryId?: number }) {
  return request.get('/admin/articles', { params })
}

export function getAdminArticle(id: number) {
  return request.get(`/admin/articles/${id}`)
}

export function createArticle(data: any) {
  return request.post('/admin/articles', data)
}

export function updateArticle(id: number, data: any) {
  return request.put(`/admin/articles/${id}`, data)
}

export function deleteArticle(id: number) {
  return request.delete(`/admin/articles/${id}`)
}

export function getAdminCategories() {
  return request.get('/admin/categories')
}

export function createCategory(data: any) {
  return request.post('/admin/categories', data)
}

export function updateCategory(id: number, data: any) {
  return request.put(`/admin/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return request.delete(`/admin/categories/${id}`)
}

export function getAdminTags() {
  return request.get('/admin/tags')
}

export function createTag(data: any) {
  return request.post('/admin/tags', data)
}

export function updateTag(id: number, data: any) {
  return request.put(`/admin/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return request.delete(`/admin/tags/${id}`)
}

export function getAdminComments(params: { page?: number; pageSize?: number; status?: number }) {
  return request.get('/admin/comments', { params })
}

export function deleteComment(id: number) {
  return request.delete(`/admin/comments/${id}`)
}

export function updateCommentStatus(id: number, status: number) {
  return request.put(`/admin/comments/${id}/status`, { status })
}

export function getAdminUsers(params: { page?: number; pageSize?: number }) {
  return request.get('/admin/users', { params })
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/admin/users/${id}/status`, { status })
}

export function getSiteConfigs() {
  return request.get('/admin/site-configs')
}

export function updateSiteConfigs(data: Record<string, string>) {
  return request.put('/admin/site-configs', data)
}

export function getFriendLinks() {
  return request.get('/admin/friend-links')
}

export function createFriendLink(data: any) {
  return request.post('/admin/friend-links', data)
}

export function updateFriendLink(id: number, data: any) {
  return request.put(`/admin/friend-links/${id}`, data)
}

export function deleteFriendLink(id: number) {
  return request.delete(`/admin/friend-links/${id}`)
}

// ===== About 管理 =====
export function getAdminSkills() { return request.get('/admin/about/skills') }
export function createSkill(data: any) { return request.post('/admin/about/skills', data) }
export function updateSkill(id: number, data: any) { return request.put(`/admin/about/skills/${id}`, data) }
export function deleteSkill(id: number) { return request.delete(`/admin/about/skills/${id}`) }

export function getAdminProjects() { return request.get('/admin/about/projects') }
export function createProject(data: any) { return request.post('/admin/about/projects', data) }
export function updateProject(id: number, data: any) { return request.put(`/admin/about/projects/${id}`, data) }
export function deleteProject(id: number) { return request.delete(`/admin/about/projects/${id}`) }

export function getAdminMilestones() { return request.get('/admin/about/milestones') }
export function createMilestone(data: any) { return request.post('/admin/about/milestones', data) }
export function updateMilestone(id: number, data: any) { return request.put(`/admin/about/milestones/${id}`, data) }
export function deleteMilestone(id: number) { return request.delete(`/admin/about/milestones/${id}`) }
