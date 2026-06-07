<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">评论管理</h1>
        <p class="page-desc">共 {{ total }} 条</p>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="comments" v-loading="loading" size="small" style="width:100%">
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="comment-content">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span :class="['status-badge', statusClass(row.status)]">{{ statusLabel(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="100">
          <template #default="{ row }">{{ row.createdAt?.substring(0, 10) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <button v-if="row.status !== 1" class="btn btn-ghost btn-sm" style="color:var(--color-success)" @click="handleStatus(row.id, 1)">通过</button>
            <button v-if="row.status !== 2" class="btn btn-ghost btn-sm" style="color:var(--color-warning)" @click="handleStatus(row.id, 2)">拒绝</button>
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row.id)">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminComments, deleteComment, updateCommentStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const comments = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

onMounted(() => loadComments())

async function loadComments() {
  loading.value = true
  try {
    const r = await getAdminComments({ page: 1, pageSize: 100 })
    comments.value = r.records || []
    total.value = r.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleStatus(id: number, s: number) {
  try {
    await updateCommentStatus(id, s)
    ElMessage.success(s === 1 ? '已通过' : '已拒绝')
    loadComments()
  } catch (e) { /* ignore */ }
}

async function handleDelete(id: number) {
  try {
    await deleteComment(id)
    ElMessage.success('已删除')
    loadComments()
  } catch (e) { /* ignore */ }
}

function statusClass(s: number) {
  return s === 1 ? 'approved' : s === 2 ? 'rejected' : 'pending'
}
function statusLabel(s: number) {
  return s === 1 ? '已通过' : s === 2 ? '已拒绝' : '待审核'
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

.comment-content {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
  color: var(--color-text);
}

.status-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 10px;
  border-radius: var(--radius-full);
}
.status-badge.approved {
  color: var(--color-success);
  background: var(--color-success-bg);
}
.status-badge.pending {
  color: var(--color-warning);
  background: var(--color-warning-bg);
}
.status-badge.rejected {
  color: var(--color-danger);
  background: var(--color-danger-bg);
}
</style>
