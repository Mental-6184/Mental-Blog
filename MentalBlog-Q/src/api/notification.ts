import request from './request'
export function getNotifications(params?: { page?: number; pageSize?: number }) { return request.get('/notifications', { params }) }
export function getUnreadNotificationCount() { return request.get('/notifications/unread-count') }
export function markNotificationRead(id: number) { return request.put(`/notifications/${id}/read`) }
export function markAllNotificationsRead() { return request.put('/notifications/read-all') }
