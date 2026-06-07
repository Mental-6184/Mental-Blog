import request from './request'

export function getImageComments(imageId: number) {
  return request.get(`/images/${imageId}/comments`)
}

export function createImageComment(imageId: number, data: { content: string; parentId?: number | null }) {
  return request.post(`/images/${imageId}/comments`, data)
}

export function deleteImageComment(imageId: number, commentId: number) {
  return request.delete(`/images/${imageId}/comments/${commentId}`)
}
