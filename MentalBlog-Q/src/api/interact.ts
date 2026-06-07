import request from './request'

export function toggleLikeArticle(articleId: number) {
  return request.post('/likes/article', { articleId })
}

export function checkLikeArticle(articleId: number) {
  return request.get(`/likes/article/check/${articleId}`)
}

export function toggleFavoriteArticle(articleId: number) {
  return request.post('/favorites', { articleId })
}

export function checkFavoriteArticle(articleId: number) {
  return request.get(`/favorites/check/${articleId}`)
}

export function getMyFavorites() {
  return request.get('/favorites')
}
