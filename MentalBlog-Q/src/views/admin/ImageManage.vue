<template>
  <div class="anim-fade-in">
    <div class="page-header">
      <div><h1 class="page-title">图库管理</h1><p class="page-desc">共 {{ total }} 张图片</p></div>
    </div>

    <!-- Search + Tags -->
    <div class="toolbar-row">
      <div class="search-box">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
        <input v-model="keyword" placeholder="搜索文件名或描述..." @keyup.enter="doSearch" />
      </div>
    </div>
    <div class="tags-bar">
      <span :class="['tb-item', { active: currentTag === null }]" @click="filterByTag(null)">全部</span>
      <span v-for="t in tags" :key="t.id" :class="['tb-item', { active: currentTag === t.id }]" @click="filterByTag(t.id)">{{ t.name }}</span>
    </div>

    <div class="table-card">
      <el-table :data="images" v-loading="loading" size="small" style="width:100%">
        <el-table-column label="预览" width="80" align="center">
          <template #default="{ row }">
            <img :src="row.url" class="preview-img" />
          </template>
        </el-table-column>
        <el-table-column prop="filename" label="文件名" min-width="160" show-overflow-tooltip />
        <el-table-column label="标签" min-width="120">
          <template #default="{ row }">
            <span v-for="t in row.tags||[]" :key="t.id" class="tag-badge">{{ t.name }}</span>
            <span v-if="!row.tags?.length" class="none">—</span>
          </template>
        </el-table-column>
        <el-table-column label="大小" width="80" align="right">
          <template #default="{ row }">{{ formatSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="100">
          <template #default="{ row }">{{ row.createdAt?.substring(0,10) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row)">删除</button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-pagination" v-if="total > pageSize">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="prev, pager, next" background @current-change="loadImages" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getImages, getImageTags, deleteImage } from '@/api/image'
import { ElMessage } from 'element-plus'

const images = ref<any[]>([])
const tags = ref<any[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const currentTag = ref<number | null>(null)
const keyword = ref('')
const loading = ref(false)

onMounted(async () => {
  try { const r = await getImageTags(); tags.value = r.data || [] } catch (e) { /* ignore */ }
  loadImages()
})

function filterByTag(id: number | null) { currentTag.value = id; page.value = 1; loadImages() }
function doSearch() { page.value = 1; loadImages() }

async function loadImages() {
  loading.value = true
  try {
    const r = await getImages({ page: page.value, pageSize: pageSize.value, tagId: currentTag.value || undefined, q: keyword.value || undefined })
    images.value = r.records || []
    total.value = r.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024*1024) return (bytes/1024).toFixed(1) + ' KB'
  return (bytes/(1024*1024)).toFixed(1) + ' MB'
}

async function handleDelete(row: any) {
  if (!confirm('确定删除这张图片？')) return
  try { await deleteImage(row.id); ElMessage.success('已删除'); loadImages() } catch (e: any) { ElMessage.error(e.message) }
}
</script>

<style scoped>
.toolbar-row { display: flex; gap: 10px; margin-bottom: 12px; }
.search-box { display: flex; align-items: center; gap: 8px; padding: 0 12px; border: 1.5px solid var(--color-border); border-radius: var(--radius-lg); background: var(--color-bg-card); color: var(--color-text-tertiary); max-width: 360px; }
.search-box:focus-within { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--color-primary-ring); }
.search-box input { flex: 1; height: 36px; border: none; background: none; font-size: 13px; font-family: inherit; color: var(--color-text); outline: none; }
.tags-bar { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 16px; }
.tb-item { padding: 4px 12px; font-size: 12px; font-weight: 500; border-radius: var(--radius-full); background: var(--color-bg-subtle); color: var(--color-text-secondary); cursor: pointer; transition: all 150ms; }
.tb-item:hover { color: var(--color-primary); background: var(--color-primary-light); }
.tb-item.active { background: var(--color-primary); color: #fff; }
.preview-img { width: 48px; height: 48px; border-radius: 6px; object-fit: cover; border: 1px solid var(--color-border); }
.tag-badge { display: inline-block; font-size: 11px; font-weight: 500; color: var(--color-primary); background: var(--color-primary-light); padding: 1px 6px; border-radius: 4px; margin-right: 4px; }
.none { color: var(--color-text-tertiary); font-size: 12px; }
</style>
