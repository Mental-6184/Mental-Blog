<template>
  <div class="detail-pg"><div class="container">
    <div v-if="loading" class="empty-state"><p>Loading...</p></div>
    <div v-else-if="!image" class="empty-state"><p>Image not found</p></div>
    <div v-else class="id-layout">
      <div class="id-img-col" ref="imgColRef">
        <img :src="image.url" :alt="image.filename" />
      </div>
      <div class="id-info-col" ref="infoColRef">
        <div class="id-head"><h2>{{ image.description || image.filename }}</h2></div>
        <div class="id-tags" v-if="image.tags?.length"><span class="id-tag" v-for="t in image.tags" :key="t.id">{{ t.name }}</span></div>
        <div class="id-meta">
          <div class="id-row"><span>Filename</span><span>{{ image.filename }}</span></div>
          <div class="id-row"><span>Size</span><span>{{ formatSize(image.size) }}</span></div>
          <div class="id-row"><span>Type</span><span>{{ image.mimeType }}</span></div>
          <div class="id-row"><span>Uploaded</span><span>{{ image.createdAt?.substring(0,10) }}</span></div>
        </div>
        <div class="id-actions">
          <a :href="image.url" target="_blank" download class="btn btn-primary btn-sm" style="width:100%;justify-content:center">Download</a>
          <button v-if="isOwner" class="btn btn-ghost btn-sm" style="width:100%;justify-content:center;color:var(--color-danger)" @click="handleDelete">Delete</button>
        </div>

        <div class="id-comments">
          <div class="id-cm-head"><h3>Comments</h3><span class="badge badge-neutral">{{ comments.length }}</span></div>
          <div v-if="userStore.token" class="id-cm-form">
            <el-input v-model="commentContent" type="textarea" :rows="2" :placeholder="replyTo ? 'Reply @' + replyTo.userNickname + '...' : 'Write a comment...'" />
            <div class="id-cf-actions"><button v-if="replyTo" class="btn btn-ghost btn-sm" @click="cancelReply">Cancel</button><button class="btn btn-primary btn-sm" @click="submitComment" :disabled="!commentContent.trim()">{{ replyTo ? 'Reply' : 'Submit' }}</button></div>
          </div>
          <div v-else class="id-cm-login"><router-link to="/login" class="btn btn-primary btn-sm">Login</router-link><span>to comment</span></div>
          <div v-if="comments.length === 0" class="empty-state" style="padding:20px 0"><p>No comments yet</p></div>
          <div v-else class="id-cm-list">
            <div v-for="c in comments" :key="c.id" class="id-cm-item">
              <div class="id-cm-top"><span class="id-cm-avatar">{{ c.userNickname?.[0] || '?' }}</span><span class="id-cm-user">{{ c.userNickname || 'User' }}</span><span class="id-cm-time">{{ c.createdAt?.substring(0,10) }}</span></div>
              <div class="id-cm-text">{{ c.content }}</div>
              <button class="id-cm-reply" @click="onReply(c)">Reply</button>
              <div v-if="c.children?.length" class="id-cm-nested">
                <div v-for="child in c.children" :key="child.id" class="id-cm-item id-cm-child"><div class="id-cm-top"><span class="id-cm-avatar">{{ child.userNickname?.[0] || '?' }}</span><span class="id-cm-user">{{ child.userNickname || 'User' }}</span><span class="id-cm-time">{{ child.createdAt?.substring(0,10) }}</span></div><div class="id-cm-text">{{ child.content }}</div></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div></div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getImageById, deleteImage } from '@/api/image'
import { createImageComment, getImageComments } from '@/api/imageComment'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const image = ref<any>(null); const loading = ref(false); const comments = ref<any[]>([])
const commentContent = ref(''); const replyTo = ref<{ id: number; userNickname: string } | null>(null)
const imgId = computed(() => Number(route.params.id))
const isOwner = computed(() => image.value && userStore.userId === image.value.userId)
const imgColRef = ref<HTMLElement | null>(null)
const infoColRef = ref<HTMLElement | null>(null)

onMounted(async () => {
  loading.value = true
  try { const r = await getImageById(imgId.value); image.value = r.data; const cr = await getImageComments(imgId.value); comments.value = cr.data || [] } catch (e) { /* ignore */ }
  loading.value = false
  nextTick(() => {
    gsap.from(imgColRef.value, { opacity: 0, x: -30, duration: 0.6, ease: 'power3.out' })
    gsap.from(infoColRef.value, { opacity: 0, x: 30, duration: 0.6, delay: 0.1, ease: 'power3.out' })
  })
})

function formatSize(bytes: number) { if (!bytes) return '0 B'; if (bytes < 1024) return bytes + ' B'; if (bytes < 1024*1024) return (bytes/1024).toFixed(1) + ' KB'; return (bytes/(1024*1024)).toFixed(1) + ' MB' }

async function handleDelete() { if (!confirm('Delete this image?')) return; try { await deleteImage(imgId.value); ElMessage.success('Deleted'); router.push('/gallery') } catch (e: any) { ElMessage.error(e.message) } }

function onReply(c: any) { replyTo.value = { id: c.id, userNickname: c.userNickname }; commentContent.value = '' }
function cancelReply() { replyTo.value = null; commentContent.value = '' }

async function submitComment() { if (!commentContent.value.trim()) return; try { await createImageComment(imgId.value, { content: commentContent.value, parentId: replyTo.value?.id || null }); commentContent.value = ''; replyTo.value = null; const cr = await getImageComments(imgId.value); comments.value = cr.data || []; ElMessage.success('Comment posted') } catch (e: any) { ElMessage.error(e.message) } }
</script>

<style scoped>
.detail-pg { background: var(--bg-deep); padding: 28px 0 80px; min-height: 60vh; }
.id-layout { display: flex; gap: 32px; align-items: flex-start; }
.id-img-col { flex: 1; min-width: 0; border-radius: 12px; overflow: hidden; border: 1px solid var(--border-subtle); }
.id-img-col img { width: 100%; height: auto; display: block; }
.id-info-col { width: 320px; flex-shrink: 0; }
.id-head { margin-bottom: 12px; }
.id-head h2 { font-size: 18px; font-weight: 700; color: #fff; }
.id-tags { display: flex; gap: 4px; flex-wrap: wrap; margin-bottom: 16px; }
.id-tag { font-size: 12px; font-weight: 500; color: var(--cyber-cyan); background: rgba(0,240,255,0.06); padding: 2px 10px; border-radius: 4px; }
.id-meta { margin-bottom: 20px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 12px 16px; }
.id-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 13px; border-bottom: 1px solid var(--border-subtle); }
.id-row:last-child { border-bottom: none; }
.id-row span:first-child { color: var(--text-tertiary); }
.id-actions { display: flex; flex-direction: column; gap: 8px; margin-bottom: 24px; }
.id-comments { border-top: 1px solid var(--border-subtle); padding-top: 16px; }
.id-cm-head { display: flex; align-items: center; gap: 8px; margin-bottom: 14px; }
.id-cm-head h3 { font-size: 15px; font-weight: 600; color: #fff; }
.id-cm-form { margin-bottom: 16px; }
.id-cf-actions { display: flex; justify-content: flex-end; gap: 6px; margin-top: 6px; }
.id-cm-login { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--text-tertiary); margin-bottom: 14px; }
.id-cm-list { display: flex; flex-direction: column; gap: 8px; max-height: 400px; overflow-y: auto; }
.id-cm-list::-webkit-scrollbar { width: 3px; }
.id-cm-item { padding: 8px 0; border-bottom: 1px solid var(--border-subtle); }
.id-cm-top { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.id-cm-avatar { width: 24px; height: 24px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; color: #fff; flex-shrink: 0; }
.id-cm-user { font-size: 12px; font-weight: 600; color: var(--text-primary); }
.id-cm-time { font-size: 11px; color: var(--text-tertiary); margin-left: auto; font-family: var(--font-mono); }
.id-cm-text { font-size: 13px; color: var(--text-secondary); line-height: 1.5; margin-bottom: 4px; word-break: break-word; }
.id-cm-reply { font-size: 11px; font-weight: 500; color: var(--cyber-cyan); border: none; background: none; cursor: pointer; padding: 0; font-family: inherit; }
.id-cm-reply:hover { text-decoration: underline; }
.id-cm-nested { margin-left: 20px; margin-top: 6px; }
.id-cm-child { border-bottom: none; padding: 6px 0; }
@media (max-width: 768px) { .id-layout { flex-direction: column; } .id-info-col { width: 100%; } }
</style>


