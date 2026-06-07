<template>
  <div class="upload-page"><div class="container">
    <div class="up-header" ref="headerRef"><h1>Upload Image</h1><router-link to="/gallery" class="btn btn-ghost btn-sm">Back to Gallery</router-link></div>
    <div class="up-card" ref="cardRef">
      <div class="drop-zone" :class="{ 'dragover': isDragover }" @dragenter.prevent="isDragover=true" @dragover.prevent="isDragover=true" @dragleave.prevent="isDragover=false" @drop.prevent="handleDrop" @click="triggerInput">
        <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFile" />
        <template v-if="!preview">
          <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5-5 5"/><path d="m11 17-2-2-6 6"/></svg>
          <p>Drag & drop an image here, or click to select</p>
          <span>Supports JPG / PNG / GIF / WebP, up to 10MB</span>
        </template>
        <div v-else class="up-preview-wrap"><img :src="preview" class="up-preview" /><p class="up-file-name">{{ file?.name }}</p></div>
      </div>
      <div class="up-form" v-if="preview" ref="formBlockRef">
        <div class="fld"><label>Description (optional)</label><input v-model="description" class="input-cyber" placeholder="Add a description..." /></div>
        <div class="fld"><label>Tags (select at least one)</label><div class="tag-picker"><span v-for="t in tags" :key="t.id" :class="['tp-item', { active: selectedTags.includes(t.id) }]" @click="toggleTag(t.id)">{{ t.name }}</span></div><p v-if="tagError" class="err">{{ tagError }}</p></div>
        <button class="btn btn-primary" @click="handleUpload" :disabled="uploading || selectedTags.length===0">{{ uploading ? 'Uploading...' : 'Upload' }}</button>
      </div>
    </div>
  </div></div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getImageTags, uploadImage } from '@/api/image'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'

const router = useRouter(); const tags = ref<any[]>([]); const selectedTags = ref<number[]>([])
const fileInput = ref<HTMLInputElement | null>(null); const preview = ref(''); const file = ref<File | null>(null)
const description = ref(''); const isDragover = ref(false); const uploading = ref(false); const tagError = ref('')
const headerRef = ref<HTMLElement | null>(null); const cardRef = ref<HTMLElement | null>(null)
const formBlockRef = ref<HTMLElement | null>(null)

onMounted(async () => {
  try { const r = await getImageTags(); tags.value = r.data || [] } catch (e) { /* ignore */ }
  nextTick(() => {
    gsap.from(headerRef.value, { opacity: 0, y: -20, duration: 0.5, ease: 'power3.out' })
    gsap.from(cardRef.value, { opacity: 0, y: 30, duration: 0.5, delay: 0.1, ease: 'power3.out' })
  })
})

function triggerInput() { if (!preview.value) fileInput.value?.click() }
function handleFile(e: Event) { const f = (e.target as HTMLInputElement).files?.[0]; if (f) processFile(f) }
function handleDrop(e: DragEvent) { isDragover.value = false; const f = e.dataTransfer?.files?.[0]; if (f) processFile(f) }

function processFile(f: File) {
  if (!['image/jpeg','image/png','image/gif','image/webp'].includes(f.type)) { ElMessage.warning('Only JPG/PNG/GIF/WebP supported'); return }
  if (f.size > 10 * 1024 * 1024) { ElMessage.warning('File must be under 10MB'); return }
  file.value = f; const reader = new FileReader(); reader.onload = e => { preview.value = e.target?.result as string; nextTick(() => { if (formBlockRef.value) gsap.from(formBlockRef.value, { opacity: 0, y: 20, duration: 0.4, ease: 'power3.out' }) }) }; reader.readAsDataURL(f)
}

function toggleTag(id: number) { tagError.value = ''; const idx = selectedTags.value.indexOf(id); if (idx >= 0) selectedTags.value.splice(idx, 1); else selectedTags.value.push(id) }

async function handleUpload() { if (!file.value) return; if (selectedTags.value.length === 0) { tagError.value = 'Select at least one tag'; return }; uploading.value = true; try { const fd = new FormData(); fd.append('file', file.value); fd.append('tagIds', selectedTags.value.join(',')); fd.append('description', description.value); await uploadImage(fd); ElMessage.success('Uploaded'); router.push('/gallery') } catch (e: any) { ElMessage.error(e.message) }; uploading.value = false }
</script>

<style scoped>
.upload-page { background: var(--bg-deep); padding: 32px 0 80px; max-width: 640px; margin: 0 auto; min-height: 60vh; }
.up-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.up-header h1 { font-size: 24px; font-weight: 800; color: #fff; }
.up-card { background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 12px; padding: 28px; }
.drop-zone { border: 2px dashed rgba(0,240,255,0.12); border-radius: 12px; padding: 48px 24px; text-align: center; cursor: pointer; transition: all 0.25s cubic-bezier(0.23,1,0.32,1); margin-bottom: 24px; position: relative; overflow: hidden; }
.drop-zone:hover { border-color: rgba(0,240,255,0.25); background: rgba(0,240,255,0.02); }
.drop-zone.dragover { border-color: var(--cyber-cyan); background: rgba(0,240,255,0.06); transform: scale(1.015); box-shadow: 0 0 30px rgba(0,240,255,0.08); }
.drop-zone svg { color: var(--text-tertiary); margin-bottom: 12px; transition: all 0.3s; }
.drop-zone.dragover svg { color: var(--cyber-cyan); transform: scale(1.15); }
.drop-zone p { font-size: 15px; color: var(--text-secondary); margin-bottom: 4px; }
.drop-zone span { font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); }
.up-preview { max-width: 100%; max-height: 300px; border-radius: 8px; }
.fld { margin-bottom: 18px; }
.fld label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--text-secondary); }
.tag-picker { display: flex; flex-wrap: wrap; gap: 6px; }
.tp-item { padding: 5px 14px; font-size: 13px; font-weight: 500; border-radius: 6px; background: rgba(255,255,255,0.03); border: 1px solid var(--border-subtle); color: var(--text-secondary); cursor: pointer; transition: all 0.15s; }
.tp-item:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.1); }
.tp-item.active { background: var(--cyber-cyan); color: #0A0A0F; border-color: var(--cyber-cyan); }
.up-preview-wrap { position: relative; display: inline-block; }
.up-file-name { font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); margin-top: 8px; word-break: break-all; }
.err { font-size: 12px; color: var(--cyber-purple); margin-top: 4px; }
.input-cyber { width: 100%; height: 42px; padding: 0 14px; font-size: 14px; font-family: inherit; color: var(--text-primary); background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; outline: none; transition: all 0.2s; box-sizing: border-box; }
.input-cyber:focus { border-color: rgba(0,240,255,0.2); }
</style>

