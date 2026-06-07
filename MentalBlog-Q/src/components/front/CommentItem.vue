<template>
  <div class="comment-wrapper" :style="{ marginLeft: depth * 18 + 'px' }">
    <div class="comment-main">
      <span class="comment-avatar">{{ comment.userNickname?.[0] || '?' }}</span>
      <div class="comment-body">
        <div class="comment-head">
          <span class="comment-user">{{ comment.userNickname || 'User' }}</span>
          <span class="comment-time">{{ comment.createdAt?.substring(0, 10) }}</span>
        </div>
        <div class="comment-text">{{ comment.content }}</div>
        <div class="comment-actions" v-if="showReply">
          <button v-if="userStore.token" class="action-btn" @click="$emit('reply', comment)">Reply</button>
          <button v-if="userStore.userId === comment.userId" class="action-btn danger" @click="handleDelete">Delete</button>
        </div>
      </div>
    </div>
    <template v-if="comment.children?.length">
      <CommentItem v-for="child in comment.children" :key="child.id" :comment="child" :depth="depth + 1" :articleId="articleId" @reply="(c:any)=>$emit('reply',c)" @refresh="$emit('refresh')" />
    </template>
  </div>
</template>
<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { deleteComment } from '@/api/comment'
import { ElMessage } from 'element-plus'
const props = defineProps<{ comment: any; depth: number; articleId: number }>()
const emit = defineEmits<{ reply: [comment: any]; refresh: [] }>()
const userStore = useUserStore()
const showReply = true
async function handleDelete() { try { await deleteComment(props.comment.id); ElMessage.success('Deleted'); emit('refresh') } catch (e) { /* ignore */ } }
</script>
<style scoped>
.comment-wrapper { }
.comment-main { display: flex; gap: 10px; padding: 12px 0; }
.comment-avatar { width: 30px; height: 30px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); color: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 12px; font-weight: 600; font-family: var(--font-mono); }
.comment-body { flex: 1; min-width: 0; }
.comment-head { display: flex; align-items: center; gap: 8px; margin-bottom: 2px; }
.comment-user { font-size: 13px; font-weight: 600; color: var(--text-primary); }
.comment-time { font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); }
.comment-text { font-size: 14px; color: var(--text-secondary); line-height: 1.6; word-break: break-word; }
.comment-actions { margin-top: 4px; display: flex; gap: 10px; }
.action-btn { font-size: 12px; font-weight: 500; border: none; background: none; color: var(--text-tertiary); cursor: pointer; padding: 2px 8px; border-radius: 4px; transition: all 150ms; font-family: inherit; }
.action-btn:hover { color: var(--cyber-cyan); background: rgba(0,240,255,0.04); }
.action-btn.danger:hover { color: var(--cyber-purple); background: rgba(124,58,237,0.04); }
</style>
