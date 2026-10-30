import request from './request'

export function getUserInfo() {
  return request.get('/user/info')
}

export function updateProfile(data: { nickname?: string; bio?: string; avatar?: string }) {
  return request.put('/user/profile', data)
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put('/user/password', data)
}
