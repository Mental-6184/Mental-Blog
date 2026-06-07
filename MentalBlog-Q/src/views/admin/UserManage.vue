<template>
  <div class="fade-in">
    <div class="page-header">
      <div>
        <h1 class="page-title">用户管理</h1>
        <p class="page-desc">共 {{ users.length }} 个用户</p>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="users" size="small" style="width:100%">
        <el-table-column label="头像" width="54" align="center">
          <template #default="{ row }">
            <span class="user-avatar">{{ (row.nickname || row.username || 'U')[0] }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" width="80" align="center">
          <template #default="{ row }">
            <span :class="['role-badge', row.role === 'ADMIN' ? 'admin' : 'user']">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <span :class="['status-badge', row.status === 0 ? 'normal' : 'banned']">
              {{ row.status === 0 ? '正常' : '封禁' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <button v-if="row.role !== 'ADMIN'"
              class="btn btn-ghost btn-sm"
              :style="{ color: row.status === 0 ? 'var(--color-warning)' : 'var(--color-success)' }"
              @click="handleToggleStatus(row)">
              {{ row.status === 0 ? '封禁' : '解封' }}
            </button>
            <span v-else class="no-op">—</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminUsers, updateUserStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const users = ref<any[]>([])

onMounted(async () => {
  try {
    const r = await getAdminUsers({ page: 1, pageSize: 100 })
    users.value = r.records || []
  } catch (e) { /* ignore */ }
})

async function handleToggleStatus(user: any) {
  const ns = user.status === 0 ? 1 : 0
  try {
    await updateUserStatus(user.id, ns)
    user.status = ns
    ElMessage.success(ns === 0 ? '已解封' : '已封禁')
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

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary));
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
}

.role-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}
.role-badge.admin {
  color: var(--color-primary);
  background: var(--color-primary-light);
}
.role-badge.user {
  color: var(--color-text-tertiary);
  background: var(--color-bg-subtle);
}

.status-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}
.status-badge.normal {
  color: var(--color-success);
  background: var(--color-success-bg);
}
.status-badge.banned {
  color: var(--color-danger);
  background: var(--color-danger-bg);
}

.no-op {
  color: var(--color-text-tertiary);
  font-size: 12px;
}
</style>
