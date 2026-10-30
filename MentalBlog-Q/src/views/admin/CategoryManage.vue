<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">分类管理</h1>
        <p class="page-desc">共 {{ categories.length }} 个分类</p>
      </div>
      <button class="btn btn-primary" @click="showDialog(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建分类
      </button>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="table-skeleton">
      <div class="sk-row" v-for="i in 5" :key="i">
        <div class="sk-cell" style="width:35%"></div>
        <div class="sk-cell" style="width:25%"></div>
        <div class="sk-cell" style="width:30%"></div>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="loadError" class="empty-state" style="padding:40px 0">
      <p>{{ loadError }}</p>
      <button class="btn btn-ghost btn-sm" style="margin-top:12px" @click="loadData">重试</button>
    </div>

    <!-- Table -->
    <div v-else class="table-card">
      <el-table :data="categories" size="small" style="width:100%">
        <el-table-column prop="name" label="名称" min-width="150">
          <template #default="{ row }"><span class="cat-name">{{ row.name }}</span></template>
        </el-table-column>
        <el-table-column prop="slug" label="别名" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <button class="btn btn-ghost btn-sm" @click="showDialog(row)">编辑</button>
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row.id)">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新建分类'" width="480px" :close-on-click-modal="false" top="15vh">
      <div class="dialog-body">
        <div class="field">
          <label>名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="分类名称" />
        </div>
        <div class="field">
          <label>别名 (slug)</label>
          <input v-model="form.slug" class="input" placeholder="url-friendly-name" />
          <p class="field-hint">用于 URL 中的唯一标识，如 "front-end"</p>
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="form.description" class="input" rows="3" placeholder="可选描述" maxlength="200"></textarea>
          <p class="field-hint">{{ form.description?.length || 0 }} / 200</p>
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
import { getAdminCategories, createCategory, updateCategory, deleteCategory } from '@/api/admin'
import { ElMessage } from 'element-plus'

const categories = ref<any[]>([])
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const loading = ref(true)
const loadError = ref('')
const form = reactive({ name: '', slug: '', description: '' })

async function loadData() {
  loading.value = true
  loadError.value = ''
  try {
    const r = await getAdminCategories()
    categories.value = r.data || []
  } catch (e: any) {
    loadError.value = e.message || '加载失败，请刷新重试'
  }
  loading.value = false
}

onMounted(() => loadData())

function showDialog(row: any) {
  if (row) {
    editingId.value = row.id
    Object.assign(form, { name: row.name, slug: row.slug, description: row.description || '' })
  } else {
    editingId.value = null
    Object.assign(form, { name: '', slug: '', description: '' })
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) await updateCategory(editingId.value, form)
    else await createCategory(form)
    ElMessage.success(editingId.value ? '已更新' : '已创建')
    dialogVisible.value = false
    await loadData()
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function handleDelete(id: number) {
  try { await deleteCategory(id); ElMessage.success('已删除'); await loadData() }
  catch (e: any) { ElMessage.error(e.message || '删除失败') }
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; letter-spacing: -0.3px; margin-bottom: 2px; }
.page-desc { font-size: 13px; color: var(--color-text-tertiary); }
.table-card { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: var(--radius-lg); overflow: hidden; }
.cat-name { font-weight: 600; color: var(--color-text); }
.dialog-body { padding: 0; }
.field { margin-bottom: 18px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--color-text-secondary); }
.req { color: var(--color-danger); }
.field-hint { font-size: 11px; color: var(--color-text-tertiary); margin-top: 4px; }
.table-skeleton { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: var(--radius-lg); overflow: hidden; padding: 8px 0; }
.sk-row { display: flex; gap: 0; padding: 8px 16px; }
.sk-cell { height: 18px; background: var(--color-bg-subtle); border-radius: 4px; margin-right: 20px; animation: skPulse 1.5s ease-in-out infinite; }
@keyframes skPulse { 0%,100% { opacity: 0.3; } 50% { opacity: 0.6; } }
</style>
