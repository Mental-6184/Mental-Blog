import request from './request'

export function getSiteConfig() {
  return request.get('/site/config')
}

export function getFriendLinks() {
  return request.get('/site/friend-links')
}
