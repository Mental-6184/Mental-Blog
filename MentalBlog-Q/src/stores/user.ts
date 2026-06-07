import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId') || 0))
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const role = ref(localStorage.getItem('role') || '')

  function setLogin(data: { token: string; userId: number; username: string; nickname: string; avatar: string; role: string }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    nickname.value = data.nickname
    avatar.value = data.avatar
    role.value = data.role

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname)
    localStorage.setItem('avatar', data.avatar || '')
    localStorage.setItem('role', data.role)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    nickname.value = ''
    avatar.value = ''
    role.value = ''

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('avatar')
    localStorage.removeItem('role')
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.data) {
        nickname.value = res.data.nickname
        avatar.value = res.data.avatar
        localStorage.setItem('nickname', res.data.nickname || '')
        localStorage.setItem('avatar', res.data.avatar || '')
      }
    } catch (e) {
      // ignore
    }
  }

  return { token, userId, username, nickname, avatar, role, setLogin, logout, fetchUserInfo }
})
