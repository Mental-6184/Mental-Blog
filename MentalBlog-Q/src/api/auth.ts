import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function register(data: { username: string; password: string; email: string; nickname?: string }) {
  return request.post('/auth/register', data)
}

export function sendEmailCode(email: string) { return request.post('/auth/send-email-code', { email }) }
export function resetPassword(data: { email: string; code: string; newPassword: string }) { return request.post('/auth/reset-password', data) }
export function logout() { return request.post('/user/logout') }
