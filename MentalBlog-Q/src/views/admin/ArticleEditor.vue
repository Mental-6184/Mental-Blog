<template>
  <div class="editor-page fade-in">
    <div class="page-header">
      <h1 class="page-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
    </div>

    <div class="editor-layout">
      <!-- 左侧编辑器 -->
      <div class="editor-main">
        <div class="editor-card">
          <div class="field">
            <label>标题</label>
            <input v-model="form.title" class="input" placeholder="输入文章标题..." />
          </div>
          <div class="field">
            <label>摘要</label>
            <textarea v-model="form.summary" class="input" rows="2" placeholder="文章摘要（可选）"></textarea>
          </div>
          <div class="field">
            <label>内容</label>
            <div ref="editorRef" class="vditor-wrap"></div>
          </div>
        </div>
      </div>

      <!-- 右侧设置面板 -->
      <div class="editor-sidebar">
        <div class="editor-card">
          <h3 class="side-title">发布设置</h3>
          <div class="side-actions">
            <button class="btn btn-primary" style="width:100%;justify-content:center" @click="handleSave(1)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
              发布
            </button>
            <button class="btn btn-outline" style="width:100%;justify-content:center" @click="handleSave(0)">
              保存草稿
            </button>
            <button class="btn btn-ghost" style="width:100%;justify-content:center;color:var(--color-text-tertiary)" @click="$router.push('/admin/articles')">
              取消
            </button>
          </div>
        </div>

        <div class="editor-card">
          <h3 class="side-title">URL 别名</h3>
          <input v-model="form.slug" class="input" placeholder="url-friendly-slug" />
          <p class="side-hint">用于生成文章链接，如 /article/slug</p>
        </div>

        <div class="editor-card">
          <h3 class="side-title">分类</h3>
          <select v-model="form.categoryId" class="input">
            <option :value="null">— 选择分类 —</option>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>

        <!-- 封面图片 -->
        <div class="editor-card">
          <h3 class="side-title">封面图片</h3>
          <div class="cover-upload" @click="triggerCoverInput">
            <input ref="coverInput" type="file" accept="image/*" style="display:none" @change="handleCoverUpload" />
            <div v-if="!form.coverImage && !coverPreview" class="cover-placeholder">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5-5 5"/><path d="m11 17-2-2-6 6"/></svg>
              <span>点击上传封面图</span>
              <p>不传则根据分类自动生成</p>
            </div>
            <div v-else class="cover-preview-wrap">
              <img :src="coverPreview || form.coverImage" class="cover-preview-img" />
              <button class="cover-remove" @click.stop="removeCover">✕</button>
            </div>
          </div>
        </div>

        <div class="editor-card">
          <h3 class="side-title">标签</h3>
          <div class="tag-list">
            <div v-for="tag in tags" :key="tag.id"
              :class="['tag-chip', { active: selectedTagIds.includes(tag.id) }]"
              @click="toggleTag(tag.id)">
              {{ tag.name }}
            </div>
          </div>
        </div>

        <div class="editor-card">
          <h3 class="side-title">选项</h3>
          <label class="option-row">
            <input type="checkbox" v-model="form.top" :true-value="1" :false-value="0" />
            <span>置顶文章</span>
          </label>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAdminArticle, createArticle, updateArticle, getAdminCategories, getAdminTags } from '@/api/admin'
import { uploadImage } from '@/api/upload'
import { ElMessage } from 'element-plus'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

const route = useRoute()
const router = useRouter()
const categories = ref<any[]>([])
const tags = ref<any[]>([])
const editorRef = ref<HTMLDivElement | null>(null)
const coverInput = ref<HTMLInputElement | null>(null)
const coverPreview = ref('')
let vditor: Vditor | null = null

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '', summary: '', content: '', coverImage: '', slug: '',
  status: 0, top: 0, categoryId: null as number | null, tagIds: [] as number[]
})

// 分类对应的默认封面图
const defaultCovers: Record<string, string> = {
  '1': '/default-covers/tech.jpg',
  '2': '/default-covers/life.jpg',
  '3': '/default-covers/project.jpg',
  '4': '/default-covers/reading.jpg',
  '5': '/default-covers/essay.jpg',
}
const defaultCoverFallback = '/default-covers/default.jpg'

const selectedTagIds = ref<number[]>([])

onMounted(async () => {
  try {
    const [catRes, tagRes] = await Promise.all([getAdminCategories(), getAdminTags()])
    categories.value = catRes.data || []
    tags.value = tagRes.data || []
  } catch (e) { /* ignore */ }

  let initialContent = ''
  if (isEdit.value) {
    try {
      const res = await getAdminArticle(Number(route.params.id))
      if (res.data) {
        Object.assign(form, res.data)
        selectedTagIds.value = res.data.tagIds || []
        form.tagIds = selectedTagIds.value
        initialContent = form.content
      }
    } catch (e) { /* ignore */ }
  }

  await nextTick()
  if (editorRef.value) {
    vditor = new Vditor(editorRef.value, {
      value: initialContent,
      height: 520,
      mode: "wysiwyg",
      toolbarConfig: { pin: true },
      cache: { enable: false },
      placeholder: "开始写文章...",
      toolbar: [
        "headings", "bold", "italic", "strike", "|",
        "line", "quote", "list", "ordered-list", "|",
        "code", "inline-code", "|", "table", "link", "|",
        "undo", "redo", "fullscreen"
      ],
      upload: {
        url: "/api/admin/upload",
        accept: "image/*,.jpg,.jpeg,.png,.gif,.webp",
        max: 10 * 1024 * 1024,
        filename: "file",
        success: function(editor: any, msg: string) {
          try {
            const data = JSON.parse(msg)
            if (data.code === 200 && data.data?.url) {
              editor.insertValue("![](" + data.data.url + ")")
            }
          } catch(e) {}
        }
      },
      theme: document.documentElement.getAttribute("data-theme") === "dark" ? "dark" : "classic"
    })
  }
})

onBeforeUnmount(() => vditor?.destroy())

function triggerCoverInput() { coverInput.value?.click() }

async function handleCoverUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  // 先预览
  const reader = new FileReader()
  reader.onload = (ev) => { coverPreview.value = ev.target?.result as string }
  reader.readAsDataURL(file)
  try {
    const res = await uploadImage(file)
    form.coverImage = res.data?.url || ''
    ElMessage.success('封面已上传')
  } catch (e: any) {
    ElMessage.error('封面上传失败')
  }
}

function removeCover() { form.coverImage = ''; coverPreview.value = ''; if (coverInput.value) coverInput.value.value = '' }

// 自动填充默认封面（根据分类）
function getDefaultCover(): string {
  if (form.coverImage || coverPreview.value) return form.coverImage
  if (form.categoryId) return defaultCovers[String(form.categoryId)] || defaultCoverFallback
  return defaultCoverFallback
}

// 当分类变化时，如果没有手动上传封面则用默认的
watch(() => form.categoryId, () => {
  if (!form.coverImage && !coverPreview.value) {
    form.coverImage = getDefaultCover()
  }
})

function toggleTag(tagId: number) {
  const idx = selectedTagIds.value.indexOf(tagId)
  if (idx >= 0) {
    selectedTagIds.value.splice(idx, 1)
  } else {
    selectedTagIds.value.push(tagId)
  }
  form.tagIds = [...selectedTagIds.value]
}

async function handleSave(status: number) {
  if (vditor) form.content = vditor.getValue()
  if (!form.content) { ElMessage.warning('请输入内容'); return }
  form.status = status
  try {
    if (isEdit.value) {
      await updateArticle(Number(route.params.id), form)
      ElMessage.success('已更新')
    } else {
      await createArticle(form)
      ElMessage.success('已创建')
    }
    router.push('/admin/articles')
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
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
}

.editor-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.editor-main {
  flex: 1;
  min-width: 0;
}
.editor-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 84px;
}

.editor-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.field {
  margin-bottom: 16px;
}
.field:last-child {
  margin-bottom: 0;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--color-text-secondary);
}

.vditor-wrap {
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}
.vditor-wrap:focus-within {
  border-color: var(--color-primary);
}

.side-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--color-text);
}
.side-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.side-hint {
  font-size: 11px;
  color: var(--color-text-tertiary);
  margin-top: 6px;
}

/* 封面上传 */
.cover-upload { cursor: pointer; }
.cover-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; padding: 24px 16px; border: 2px dashed var(--color-border); border-radius: var(--radius-lg); background: var(--color-bg-subtle); transition: all 150ms; }
.cover-placeholder:hover { border-color: var(--color-primary); background: var(--color-primary-light); }
.cover-placeholder svg { color: var(--color-text-tertiary); }
.cover-placeholder span { font-size: 13px; font-weight: 500; color: var(--color-text-secondary); }
.cover-placeholder p { font-size: 11px; color: var(--color-text-tertiary); margin: 0; }
.cover-preview-wrap { position: relative; border-radius: var(--radius-md); overflow: hidden; }
.cover-preview-img { width: 100%; max-height: 140px; object-fit: cover; border-radius: var(--radius-md); border: 1px solid var(--color-border); display: block; }
.cover-remove { position: absolute; top: 6px; right: 6px; width: 24px; height: 24px; border-radius: 50%; border: none; background: rgba(0,0,0,0.5); color: #fff; font-size: 12px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.cover-remove:hover { background: rgba(0,0,0,0.7); }

/* 标签选择 */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tag-chip {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-secondary);
  background: var(--color-bg-subtle);
  padding: 4px 12px;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;
}
.tag-chip:hover {
  border-color: var(--color-primary);
}
.tag-chip.active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

/* 选项 */
.option-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
}

@media (max-width: 900px) {
  .editor-layout {
    flex-direction: column-reverse;
  }
  .editor-sidebar {
    width: 100%;
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .editor-sidebar .editor-card {
    flex: 1;
    min-width: 200px;
  }
}
</style>
