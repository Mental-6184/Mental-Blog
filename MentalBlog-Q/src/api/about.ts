import request from './request'

export function getAboutData() {
  return request.get('/about')
}
