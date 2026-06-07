<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">友链管理</h1>
        <p class="page-desc">共 {{ links.length }} 个友链</p>
      </div>
      <button class="btn btn-primary" @click="showDialog(null)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建友链
      </button>
    </div>

    <div v-if="loading" class="empty-state"><p>Loading...</p></div>
    <div v-else class="table-card">
      <el-table :data="links" size="small" style="width:100%">
        <el-table-column prop="name" label="名称" min-width="150">
          <template #default="{ row }"><span class="link-name">{{ row.name }}</span></template>
        </el-table-column>
        <el-table-column prop="url" label="链接" min-width="220" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <span :class="['status-badge', row.status === 1 ? 'visible' : 'hidden']">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <button class="btn btn-ghost btn-sm" @click="showDialog(row)">编辑</button>
            <button class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="handleDelete(row.id)">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑友链' : '新建友链'" width="520px" :close-on-click-modal="false" top="12vh">
      <div class="dialog-body">
        <div class="field">
          <label>站点名称 <span class="req">*</span></label>
          <input v-model="form.name" class="input" placeholder="站点名称" />
        </div>
        <div class="field">
          <label>链接 URL <span class="req">*</span></label>
          <input v-model="form.url" class="input" placeholder="https://example.com" />
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="form.description" class="input" rows="3" placeholder="站点简短介绍" maxlength="200"></textarea>
          <p class="field-hint">{{ form.description.length }} / 200</p>
        </div>
        <div class="field">
          <label>排序</label>
          <input v-model.number="form.sort" class="input" placeholder="数字越小越靠前" type="number" />
          <p class="field-hint">数字越小越靠前显示</p>
        </div>
        <div class="field">
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.status" :true-value="1" :false-value="0" />
            <span>在前台显示此友链</span>
          </label>
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
import { getFriendLinks as apiLinks, createFriendLink, updateFriendLink, deleteFriendLink } from '@/api/admin'
import { ElMessage } from 'element-plus'

const links = ref<any[]>([])
const loading = ref(true)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ name: '', url: '', description: '', sort: 0, status: 1, logo: '' })

onMounted(async () => { loading.value = true; try { const r = await apiLinks(); links.value = r.data || [] } catch (e) { /* ignore */ } finally { loading.value = false } })

function showDialog(row: any) {
  if (row) {
    editingId.value = row.id
    Object.assign(form, { name: row.name, url: row.url, description: row.description || '', status: row.status, sort: row.sort || 0 })
  } else {
    editingId.value = null
    Object.assign(form, { name: '', url: '', description: '', status: 1, sort: 0 })
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editingId.value) await updateFriendLink(editingId.value, form)
    else await createFriendLink(form)
    ElMessage.success(editingId.value ? '已更新' : '已创建')
    dialogVisible.value = false
    const r = await apiLinks()
    links.value = r.data || []
  } catch (e: any) { ElMessage.error(e.message || '操作失败') }
}

async function handleDelete(id: number) {
  try { await deleteFriendLink(id); ElMessage.success('已删除'); const r = await apiLinks(); links.value = r.data || [] }
  catch (e: any) { ElMessage.error(e.message || '删除失败') }
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; letter-spacing: -0.3px; margin-bottom: 2px; }
.page-desc { font-size: 13px; color: var(--color-text-tertiary); }
.table-card { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: var(--radius-lg); overflow: hidden; }
.link-name { font-weight: 600; color: var(--color-text); }
.status-badge { font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: var(--radius-full); }
.status-badge.visible { color: var(--color-success); background: var(--color-success-bg); }
.status-badge.hidden { color: var(--color-text-tertiary); background: var(--color-bg-subtle); }
.dialog-body { padding: 0; }
.field { margin-bottom: 18px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--color-text-secondary); }
.req { color: var(--color-danger); }
.field-hint { font-size: 11px; color: var(--color-text-tertiary); margin-top: 4px; }
.checkbox-label { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 13px; }
</style>
