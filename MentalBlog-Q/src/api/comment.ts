import request from './request'

export function getComments(articleId: number) {
  return request.get(`/comments/article/${articleId}`)
}

export function createComment(data: { articleId: number; content: string; parentId?: number | null }) {
  return request.post('/comments', data)
}

export function deleteComment(id: number) {
  return request.delete(`/comments/${id}`)
}

export function getMyComments() {
  return request.get('/comments/my')
}
