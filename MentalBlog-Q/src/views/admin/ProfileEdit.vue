<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">个人资料</h1>
        <p class="page-desc">修改你的昵称、简介和头像</p>
      </div>
    </div>

    <div class="profile-card slide-up">
      <div class="profile-avatar-section">
        <div class="profile-avatar">{{ (form.nickname || 'U')[0] }}</div>
        <div>
          <h3 class="profile-username">{{ userStore.username }}</h3>
          <p class="profile-role">{{ userStore.role === 'ADMIN' ? '管理员' : '用户' }}</p>
        </div>
      </div>

      <div class="settings-divider"></div>

      <div class="field">
        <label>昵称</label>
        <input v-model="form.nickname" class="input" placeholder="你的昵称" />
        <p class="field-hint">显示在文章和评论中的名称</p>
      </div>
      <div class="field">
        <label>个人简介</label>
        <textarea v-model="form.bio" class="input" rows="3" placeholder="简单介绍一下自己..."></textarea>
      </div>
      <div class="field">
        <label>头像 URL</label>
        <input v-model="form.avatar" class="input" placeholder="https://example.com/avatar.jpg" />
        <p class="field-hint">输入图片链接地址，留空则使用首字母</p>
      </div>

      <div class="settings-actions">
        <button class="btn btn-primary" @click="handleSave">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
          保存
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const form = reactive({ nickname: '', bio: '', avatar: '' })

onMounted(() => {
  form.nickname = userStore.nickname
  form.bio = ''
  form.avatar = ''
})

async function handleSave() {
  try {
    await updateProfile(form)
    ElMessage.success('保存成功')
    userStore.nickname = form.nickname
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 24px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.3px;
  margin-bottom: 2px;
}
.page-desc {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.profile-card {
  max-width: 520px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 28px;
}

.profile-avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}
.profile-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px var(--primary-glow);
}
.profile-username {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 2px;
}
.profile-role {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.settings-divider {
  height: 1px;
  background: var(--color-border);
  margin: 20px 0;
}

.field {
  margin-bottom: 20px;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--color-text-secondary);
}
.field-hint {
  font-size: 11px;
  color: var(--color-text-tertiary);
  margin-top: 4px;
}

.settings-actions {
  margin-top: 8px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
}
</style>
