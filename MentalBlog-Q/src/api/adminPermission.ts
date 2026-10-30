import request from './request'

export function getImageApplies() {
  return request.get('/admin/image-permission')
}

export function reviewImageApply(id: number, status: number, reply?: string) {
  return request.put(`/admin/image-permission/${id}`, { status, reply })
}

export function setUserImagePermission(userId: number, imagePermission: number) {
  return request.put(`/admin/image-permission/user/${userId}`, { imagePermission })
}
