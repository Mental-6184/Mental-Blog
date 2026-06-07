import request from './request'

export function getCategories() {
  return request.get('/categories')
}

export function getTags() {
  return request.get('/tags')
}

export function getTagCloud() {
  return request.get('/tags/cloud')
}
