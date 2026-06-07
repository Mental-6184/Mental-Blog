<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">文章管理</h1>
        <p class="page-desc">共 {{ total }} 篇</p>
      </div>
      <button class="btn btn-primary" @click="$router.push('/admin/articles/create')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        写文章
      </button>
    </div>

    <div class="table-card">
      <el-table :data="articles" v-loading="loading" stripe size="small" style="width:100%">
        <el-table-column type="index" label="#" width="48" />
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="article-title">{{ row.title }}</span>
            <span v-if="row.top === 1" class="pin-badge">置顶</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="90" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <span :class="['status-tag', row.status === 1 ? 'published' : 'draft']">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="阅读" width="60" align="center" prop="viewCount" />
        <el-table-column label="时间" width="110">
          <template #default="{ row }">{{ (row.publishedAt || row.createdAt)?.substring(0, 10) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <button class="btn btn-ghost btn-sm" @click="$router.push(`/admin/articles/${row.id}/edit`)">编辑</button>
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row.id)">删除</button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-pagination" v-if="total > pageSize">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="prev, pager, next" background @current-change="loadArticles" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminArticles, deleteArticle } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const articles = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

onMounted(() => loadArticles())

async function loadArticles() {
  loading.value = true
  try {
    const res = await getAdminArticles({ page: page.value, pageSize: pageSize.value })
    articles.value = res.records || []
    total.value = res.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除此文章？此操作不可恢复。', '确认删除', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
    await deleteArticle(id)
    ElMessage.success('已删除')
    loadArticles()
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
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

.table-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}
.table-pagination {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid var(--color-border);
}

.article-title {
  font-weight: 500;
  color: var(--color-text);
}
.pin-badge {
  display: inline-block;
  font-size: 10px;
  font-weight: 600;
  color: var(--color-warning);
  background: var(--color-warning-bg);
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 6px;
  vertical-align: middle;
}
.status-tag {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}
.status-tag.published {
  color: var(--color-success);
  background: var(--color-success-bg);
}
.status-tag.draft {
  color: var(--color-text-tertiary);
  background: var(--color-bg-subtle);
}
</style>
