import request from './request'

export function getImages(params: { page?: number; pageSize?: number; tagId?: number; q?: string }) {
  return request.get('/images', { params })
}

export function getImageById(id: number) {
  return request.get(`/images/${id}`)
}

export function getImageTags() {
  return request.get('/images/tags')
}

export function uploadImage(formData: FormData) {
  return request.post('/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteImage(id: number) {
  return request.delete(`/images/${id}`)
}

export function updateImage(id: number, data: any) {
  return request.put(`/images/${id}`, data)
}
