<template><div class="page-section"><div class="uc-card"><div class="head"><h2>通知</h2><button @click="readAll">全部已读</button></div><div v-if="!items.length" class="empty-state">暂无通知</div><div v-for="n in items" :key="n.id" :class="['item', { unread:n.readStatus===0 }]" @click="read(n)"><b>{{ n.title }}</b><p>{{ n.content }}</p><small>{{ n.createdAt?.substring(0,16) }}</small></div></div></div></template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
const items=ref<any[]>([])
async function load(){ const r=await getNotifications({page:1,pageSize:50}); items.value=r.records||[] }
async function read(n:any){ if(n.readStatus===0){await markNotificationRead(n.id);n.readStatus=1} }
async function readAll(){await markAllNotificationsRead();items.value.forEach(n=>n.readStatus=1)}
onMounted(load)
</script>
<style scoped>.page-section{min-height:60vh;padding:32px 20px;background:var(--bg-deep)}.uc-card{max-width:680px;margin:auto;padding:24px;background:var(--bg-elevated);border:1px solid var(--border-subtle);border-radius:10px}.head{display:flex;justify-content:space-between;align-items:center}.head button{border:0;background:none;color:var(--cyber-cyan);cursor:pointer}.item{padding:14px 0;border-bottom:1px solid var(--border-subtle);cursor:pointer}.item.unread b{color:var(--cyber-cyan)}.item p{margin:6px 0;color:var(--text-secondary)}.item small{color:var(--text-tertiary)}</style>
