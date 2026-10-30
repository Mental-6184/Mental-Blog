import request from './request'

export function toggleLikeArticle(articleId: number) {
  return request.post('/likes/article', { articleId })
}

export function checkLikeArticle(articleId: number) {
  return request.get(`/likes/article/check/${articleId}`)
}

export function toggleLikeComment(commentId: number) {
  return request.post('/likes/comment', { commentId })
}

export function checkLikeComment(commentId: number) {
  return request.get(`/likes/comment/check/${commentId}`)
}

export function toggleFavoriteArticle(articleId: number) {
  return request.post('/favorites', { articleId })
}

export function checkFavoriteArticle(articleId: number) {
  return request.get(`/favorites/check/${articleId}`)
}

export function getMyFavorites(params?: { page?: number; pageSize?: number }) {
  return request.get('/favorites', { params })
}
