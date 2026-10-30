<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">标签管理</h1>
        <p class="page-desc">共 {{ tags.length }} 个标签</p>
      </div>
      <button class="btn btn-primary" @click="showDialog(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建标签
      </button>
    </div>

    <div v-if="loading" class="empty-state"><p>Loading...</p></div>
    <div v-else class="table-card">
      <el-table :data="tags" size="small" style="width:100%">
        <el-table-column prop="name" label="名称" min-width="150">
          <template #default="{ row }"><span class="tag-name">{{ row.name }}</span></template>
        </el-table-column>
        <el-table-column prop="slug" label="别名" min-width="200" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <button class="btn btn-ghost btn-sm" @click="showDialog(row)">编辑</button>
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row.id)">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑标签' : '新建标签'" width="480px" :close-on-click-modal="false" top="15vh">
      <div class="dialog-body">
        <div class="field">
          <label>名称</label>
          <input v-model="form.name" class="input" placeholder="标签名称" />
        </div>
        <div class="field">
          <label>别名 (slug)</label>
          <input v-model="form.slug" class="input" placeholder="url-friendly-name" />
        </div>
      </div>
      <template #footer>
        <button class="btn btn-ghost" @click="dialogVisible = false">取消</button>
        <button class="btn btn-primary" @click="handleSave">{{ editingId ? '保存' : '创建' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminTags, createTag, updateTag, deleteTag } from '@/api/admin'
import { ElMessage } from 'element-plus'

const tags = ref<any[]>([])
const loading = ref(true)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ name: '', slug: '' })

onMounted(async () => { loading.value = true; try { const r = await getAdminTags(); tags.value = r.data || [] } catch (e) { /* ignore */ } finally { loading.value = false } })

function showDialog(row: any) {
  if (row) {
    editingId.value = row.id
    Object.assign(form, { name: row.name, slug: row.slug })
  } else {
    editingId.value = null
    Object.assign(form, { name: '', slug: '' })
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) {
      await updateTag(editingId.value, form)
    } else {
      await createTag(form)
    }
    ElMessage.success(editingId.value ? '已更新' : '已创建')
    dialogVisible.value = false
    const r = await getAdminTags()
    tags.value = r.data || []
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteTag(id)
    ElMessage.success('已删除')
    const r = await getAdminTags()
    tags.value = r.data || []
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
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

.tag-name {
  font-weight: 600;
  color: var(--color-primary);
}

.dialog-body {
  padding: 8px 0;
}
.field {
  margin-bottom: 16px;
}
.field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--color-text-secondary);
}
</style>
