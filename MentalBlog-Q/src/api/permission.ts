import request from './request'

export function getImagePermissionStatus() {
  return request.get('/image-permission/status')
}

export function applyImagePermission(reason: string) {
  return request.post('/image-permission/apply', { reason })
}
