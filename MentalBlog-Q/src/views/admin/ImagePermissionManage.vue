<template>
  <div class="anim-fade-in">
    <div class="page-header">
      <h1 class="page-title">图片权限管理</h1>
      <p class="page-desc">审核用户的图库申请，管理用户图库权限</p>
    </div>

    <!-- 申请审核 -->
    <div class="sec-card"><div class="sec-h"><h3>📩 申请审核</h3></div>
      <el-table :data="applies" v-loading="loading" size="small" style="width:100%" :header-cell-style="{background:'var(--color-bg-subtle)', color:'var(--color-text-secondary)'}" :cell-style="{background:'var(--color-bg-card)', color:'var(--color-text)', borderBottom:'1px solid var(--color-border)'}">
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="nickname" label="昵称" width="100" />
        <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span :class="['status-badge', row.status === 0 ? 'pending' : row.status === 1 ? 'approved' : 'rejected']">
              {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="100">
          <template #default="{ row }">{{ row.createdAt?.substring(0,10) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <button v-if="row.status === 0" class="btn btn-ghost btn-sm" style="color:var(--color-success)" @click="handleReview(row, 1)">通过</button>
            <button v-if="row.status === 0" class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="showRejectDialog(row)">拒绝</button>
            <span v-else class="none">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 权限管理 -->
    <div class="sec-card"><div class="sec-h"><h3>🔐 用户权限管理</h3></div>
      <el-table :data="users" v-loading="loadingUsers" size="small" style="width:100%" :header-cell-style="{background:'var(--color-bg-subtle)', color:'var(--color-text-secondary)'}" :cell-style="{background:'var(--color-bg-card)', color:'var(--color-text)', borderBottom:'1px solid var(--color-border)'}">
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="nickname" label="昵称" width="100" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="图库权限" width="100">
          <template #default="{ row }">
            <span :class="['perm-badge', row.imagePermission === 2 ? 'on' : row.imagePermission === 1 ? 'pending' : 'off']">
              {{ row.imagePermission === 2 ? '已开通' : row.imagePermission === 1 ? '审核中' : '未开通' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <button v-if="row.role === 'ADMIN'" class="none" style="cursor:default">—</button>
            <button v-else-if="row.imagePermission !== 2" class="btn btn-ghost btn-sm" style="color:var(--color-success)" @click="togglePermission(row, 2)">开通</button>
            <button v-else class="btn btn-ghost btn-sm" style="color:var(--color-danger)" @click="togglePermission(row, 0)">剥夺</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectDlg" title="拒绝申请" width="460px" top="25vh" :close-on-click-modal="false">
      <div class="fld"><label>拒绝原因</label><textarea v-model="rejectReason" class="input" rows="3" placeholder="填写拒绝原因（可选）" maxlength="200"></textarea><p class="char-count">{{ rejectReason.length }} / 200</p></div>
      <template #footer><button class="btn btn-ghost" @click="rejectDlg=false">取消</button><button class="btn btn-primary" @click="doReject">确认拒绝</button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getImageApplies, reviewImageApply, setUserImagePermission } from '@/api/adminPermission'
import { getAdminUsers, updateUserStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const applies = ref<any[]>([])
const users = ref<any[]>([])
const loading = ref(false)
const loadingUsers = ref(false)
const rejectDlg = ref(false)
const rejectTarget = ref<any>(null)
const rejectReason = ref('')

onMounted(() => { loadApplies(); loadUsers() })

async function loadApplies() {
  loading.value = true
  try { const r = await getImageApplies(); applies.value = r.data || [] } catch (e) { /* ignore */ }
  loading.value = false
}

async function loadUsers() {
  loadingUsers.value = true
  try { const r = await getAdminUsers({ page: 1, pageSize: 999 }); users.value = r.records || [] } catch (e) { /* ignore */ }
  loadingUsers.value = false
}

async function handleReview(row: any, status: number) {
  try { await reviewImageApply(row.id, status, ''); ElMessage.success('已通过'); loadApplies(); loadUsers() } catch (e: any) { ElMessage.error(e.message) }
}

function showRejectDialog(row: any) { rejectTarget.value = row; rejectReason.value = ''; rejectDlg.value = true }

async function doReject() {
  if (!rejectTarget.value) return
  try { await reviewImageApply(rejectTarget.value.id, 2, rejectReason.value); ElMessage.success('已拒绝'); rejectDlg.value = false; loadApplies(); loadUsers() } catch (e: any) { ElMessage.error(e.message) }
}

async function togglePermission(row: any, targetStatus: number) {
  const label = targetStatus === 2 ? '开通' : '剥夺'
  if (!confirm(`确定${label}用户 "${row.username}" 的图库权限？`)) return
  try {
    await setUserImagePermission(row.id, targetStatus)
    row.imagePermission = targetStatus
    ElMessage.success(`已${label}`)
    loadUsers()
  } catch (e: any) { ElMessage.error(e.message) }
}
</script>

<script lang="ts">
// 扩展 updateUserStatus 不支持，需要额外接口，临时方案
</script>

<style scoped>
.sec-card { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: var(--radius-xl); padding: 20px; margin-bottom: 20px; }
.sec-h { margin-bottom: 16px; }
.sec-h h3 { font-size: 15px; font-weight: 600; display: flex; align-items: center; gap: 6px; }
.status-badge { font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: var(--radius-full); }
.status-badge.pending { color: var(--color-warning); background: var(--color-warning-bg); }
.status-badge.approved { color: var(--color-success); background: var(--color-success-bg); }
.status-badge.rejected { color: var(--color-danger); background: var(--color-danger-bg); }
.perm-badge { font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: var(--radius-full); }
.perm-badge.on { color: var(--color-success); background: var(--color-success-bg); }
.perm-badge.pending { color: var(--color-warning); background: var(--color-warning-bg); }
.perm-badge.off { color: var(--color-text-tertiary); background: var(--color-bg-subtle); }
.none { color: var(--color-text-tertiary); font-size: 12px; }
.fld { margin-bottom: 16px; }
.fld label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--color-text-secondary); }
.fld .char-count { font-size: 11px; color: var(--color-text-tertiary); text-align: right; margin-top: 4px; }
</style>
