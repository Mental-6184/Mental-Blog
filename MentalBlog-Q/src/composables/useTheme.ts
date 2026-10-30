import { ref, onMounted } from 'vue'

type Theme = 'light' | 'dark'

const theme = ref<Theme>('light')

export function useTheme() {
  function applyTheme(t: Theme) {
    document.documentElement.setAttribute('data-theme', t)
    localStorage.setItem('blog-theme', t)
    theme.value = t
  }

  function toggle() {
    applyTheme(theme.value === 'light' ? 'dark' : 'light')
  }

  function init() {
    const saved = localStorage.getItem('blog-theme') as Theme | null
    if (saved) {
      applyTheme(saved)
      return
    }
    // 白色主题作为首次访问的默认主题，不跟随系统深色模式。
    applyTheme('light')
  }

  onMounted(() => {
    init()
  })

  return { theme, toggle, init, applyTheme }
}
