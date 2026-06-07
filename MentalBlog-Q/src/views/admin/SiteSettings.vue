<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">系统设置</h1>
        <p class="page-desc">管理博客的名称、描述等基本配置</p>
      </div>
    </div>

    <div class="settings-card slide-up">
      <div class="settings-section">
        <h3 class="section-title">基本信息</h3>
        <div class="field">
          <label>站点名称</label>
          <input v-model="form.site_name" class="input" placeholder="MentalBlog" />
          <p class="field-hint">显示在浏览器标签和导航栏的博客名称</p>
        </div>
        <div class="field">
          <label>站点描述</label>
          <textarea v-model="form.site_description" class="input" rows="2" placeholder="博客简介"></textarea>
          <p class="field-hint">用于 SEO 和社交分享的简短描述</p>
        </div>
        <div class="field">
          <label>关键词</label>
          <input v-model="form.site_keywords" class="input" placeholder="技术,生活,博客,编程" />
          <p class="field-hint">SEO 关键词，用逗号分隔</p>
        </div>
        <div class="field">
          <label>页脚版权</label>
          <input v-model="form.site_footer" class="input" placeholder="© 2026 MentalBlog" />
        </div>
      </div>

      <div class="settings-divider"></div>

      <div class="settings-section">
        <h3 class="section-title">关于页面</h3>
        <div class="field">
          <label>关于我（Markdown）</label>
          <textarea v-model="form.site_about" class="input code-input" rows="10" placeholder="使用 Markdown 编写你的个人介绍..."></textarea>
          <p class="field-hint">支持 Markdown 格式，将渲染在 /about 页面</p>
        </div>
      </div>

      <div class="settings-actions">
        <button class="btn btn-primary" @click="handleSave">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
          保存设置
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getSiteConfigs, updateSiteConfigs } from '@/api/admin'
import { ElMessage } from 'element-plus'

const form = reactive({
  site_name: '',
  site_description: '',
  site_keywords: '',
  site_footer: '',
  site_about: ''
})

onMounted(async () => {
  try {
    const r = await getSiteConfigs()
    if (r.data) Object.assign(form, r.data)
  } catch (e) { /* ignore */ }
})

async function handleSave() {
  try {
    await updateSiteConfigs(form as any)
    ElMessage.success('设置已保存')
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

.settings-card {
  max-width: 640px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 28px;
}

.settings-section {
  margin-bottom: 8px;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--color-text);
}

.settings-divider {
  height: 1px;
  background: var(--color-border);
  margin: 24px 0;
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

.code-input {
  font-family: var(--font-mono);
  font-size: 13px;
  line-height: 1.5;
}

.settings-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
}
</style>
