import request from './request'

export function getArticles(params: { page?: number; pageSize?: number; categoryId?: number; tagId?: number; keyword?: string }) {
  return request.get('/articles', { params })
}

export function getArticleById(id: number) {
  return request.get(`/articles/${id}`)
}

export function getArticleBySlug(slug: string) {
  return request.get(`/articles/slug/${slug}`)
}

export function getArchives() {
  return request.get('/articles/archives')
}

export function recordView(id: number) {
  return request.post(`/articles/${id}/view`)
}
