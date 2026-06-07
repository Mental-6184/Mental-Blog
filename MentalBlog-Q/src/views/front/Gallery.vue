<template>
  <div class="page-section">
    <div class="container">
      <div class="page-header gp-header" ref="headerRef">
        <div><h1>Gallery</h1><p>{{ total }} images total</p></div>
        <div class="gp-header-actions">
          <button v-if="permStatus === 0" class="btn-cyber-sm" @click="showApplyDialog = true">Request Access</button>
          <span v-else-if="permStatus === 1" class="perm-badge pending">PENDING</span>
          <span v-else-if="permStatus === 3" class="perm-badge rejected">REJECTED</span>
          <router-link v-else-if="permStatus === 2" to="/gallery/upload" class="btn-cyber-sm">+ Upload</router-link>
        </div>
      </div>

      <div v-if="permStatus === 0" class="no-perm-tip" ref="permRef"><svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg><h3>No access</h3><p>Request permission from the admin to access the gallery</p><button class="btn-cyber" @click="showApplyDialog = true">Request Permission</button></div>
      <div v-else-if="permStatus === 1" class="no-perm-tip" ref="permRef"><svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><h3>Pending review</h3><p>Your application is being reviewed by the admin</p></div>
      <div v-else-if="permStatus === 3" class="no-perm-tip rejected-tip" ref="permRef"><svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg><h3>Application rejected</h3><button class="btn-cyber" @click="showApplyDialog = true">Re-apply</button></div>

      <el-dialog v-model="showApplyDialog" title="Request Gallery Access" width="480px" top="25vh" :close-on-click-modal="false"><div class="field-dialog"><label>Reason <span class="req">*</span></label><textarea v-model="applyReason" class="input-cyber" rows="4" placeholder="Please explain why you need access..." maxlength="200"></textarea><p class="char-count">{{ applyReason.length }} / 200</p></div><template #footer><button class="btn-ghost" @click="showApplyDialog=false">Cancel</button><button class="btn-cyber" @click="handleApply" :disabled="!applyReason.trim() || applying">{{ applying ? 'Submitting...' : 'Submit' }}</button></template></el-dialog>

      <template v-if="permStatus === 2">
        <div class="carousel-section" v-if="carouselImages.length > 0" ref="carouselRef">
          <div class="carousel-wrapper" @mouseenter="pauseCarousel" @mouseleave="resumeCarousel">
            <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
              <div v-for="img in carouselImages" :key="img.id" class="carousel-slide" @click="$router.push(`/gallery/${img.id}`)">
                <div class="carousel-blur" :style="{ backgroundImage: `url(${img.url})` }"></div>
                <img :src="img.url" :alt="img.filename" loading="lazy" />
                <div class="carousel-overlay"><div class="carousel-info"><h3>{{ img.description || img.filename }}</h3><div class="carousel-tags" v-if="img.tags?.length"><span v-for="t in img.tags.slice(0,3)" :key="t.id">{{ t.name }}</span><span v-if="img.tags.length > 3">+{{ img.tags.length - 3 }}</span></div></div></div>
              </div>
            </div>
            <div class="carousel-dots"><span v-for="(_, i) in carouselImages" :key="i" :class="['dot', { active: i === currentSlide }]" @click="goToSlide(i)"></span></div>
            <button class="carousel-arrow prev" @click="prevSlide">&lsaquo;</button>
            <button class="carousel-arrow next" @click="nextSlide">&rsaquo;</button>
          </div>
        </div>

        <div class="gp-toolbar"><div class="gp-search"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg><input v-model="keyword" placeholder="Search images..." @keyup.enter="doSearch" /></div></div>
        <div class="gp-tags"><span :class="['gp-tag', { active: currentTag === null }]" @click="filterByTag(null)">All</span><span v-for="t in tags" :key="t.id" :class="['gp-tag', { active: currentTag === t.id }]" @click="filterByTag(t.id)">{{ t.name }}</span></div>

        <div v-if="loading" class="empty-state"><p>Loading...</p></div>
        <div v-else-if="images.length === 0" class="empty-state"><p>No images yet</p></div>
        <div v-else class="masonry-grid" ref="masonryRef">
          <div v-for="(img, idx) in images" :key="img.id" class="masonry-item" :ref="el => setMasonryRef(el, idx)" @click="$router.push(`/gallery/${img.id}`)">
            <div class="masonry-img"><img :src="img.url" :alt="img.filename" loading="lazy" /><div class="masonry-overlay"><div class="masonry-info"><p class="masonry-desc">{{ img.description || img.filename }}</p><div class="masonry-tags" v-if="img.tags?.length"><span v-for="t in img.tags.slice(0,2)" :key="t.id">{{ t.name }}</span></div></div></div></div>
          </div>
        </div>

        <div class="gp-pagination" v-if="total > pageSize"><el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="prev, pager, next" background @current-change="loadImages" /></div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getImages, getImageTags } from '@/api/image'
import { getImagePermissionStatus, applyImagePermission } from '@/api/permission'
import { ElMessage } from 'element-plus'

gsap.registerPlugin(ScrollTrigger)

const images = ref<any[]>([]); const tags = ref<any[]>([])
const page = ref(1); const pageSize = ref(20); const total = ref(0)
const currentTag = ref<number | null>(null); const keyword = ref('')
const loading = ref(false); const permStatus = ref<number | null>(null)
const showApplyDialog = ref(false); const applyReason = ref(''); const applying = ref(false)
const carouselImages = ref<any[]>([]); const currentSlide = ref(0)
let carouselTimer: ReturnType<typeof setInterval> | null = null
let imageRequestId = 0
const headerRef = ref<HTMLElement | null>(null)
const permRef = ref<HTMLElement | null>(null)
const carouselRef = ref<HTMLElement | null>(null)
const masonryRef = ref<HTMLElement | null>(null)
const masonryRefs: (HTMLElement | null)[] = []
const setMasonryRef = (el: any, i: number) => { masonryRefs[i] = el as HTMLElement }

onMounted(async () => {
  try {
    const r = await getImagePermissionStatus()
    permStatus.value = r.data?.imagePermission || 0
  } catch (e) {
    permStatus.value = 0
  }

  if (permStatus.value === 2) {
    await Promise.all([loadTags(), loadImages()])
    updateCarousel()
  }

  nextTick(() => {
    gsap.from(headerRef.value, { opacity: 0, y: -20, duration: 0.5, ease: 'power3.out' })
    if (permRef.value) gsap.from(permRef.value, { opacity: 0, y: 20, duration: 0.5, delay: 0.1, ease: 'power3.out' })
    if (carouselRef.value) gsap.from(carouselRef.value, { opacity: 0, y: 20, duration: 0.6, delay: 0.15, ease: 'power3.out' })
    animateMasonry()
  })
})
onUnmounted(() => {
  stopCarousel()
  ScrollTrigger.getAll().forEach(t => t.kill())
})

async function loadTags() {
  try {
    const r = await getImageTags()
    tags.value = r.data || []
  } catch (e) {
    tags.value = []
  }
}

function updateCarousel() {
  stopCarousel()
  currentSlide.value = 0
  carouselImages.value = images.value.length > 0
    ? [...images.value].sort(() => Math.random() - 0.5).slice(0, 6)
    : []
  if (carouselImages.value.length > 0) startCarousel()
}

function animateMasonry() {
  masonryRefs.forEach((c, i) => {
    if (!c) return
    gsap.from(c, { opacity: 0, y: 30, scale: 0.95, duration: 0.5, delay: 0.1 + i * 0.05, ease: 'power3.out',
      scrollTrigger: { trigger: c, start: 'top 92%' } })
  })
}

function startCarousel() { carouselTimer = setInterval(() => { currentSlide.value = (currentSlide.value + 1) % carouselImages.value.length }, 4000) }
function stopCarousel() { if (carouselTimer) { clearInterval(carouselTimer); carouselTimer = null } }
function pauseCarousel() { stopCarousel() }
function resumeCarousel() { if (carouselImages.value.length > 0) startCarousel() }
function goToSlide(i: number) { currentSlide.value = i }
function nextSlide() { currentSlide.value = (currentSlide.value + 1) % carouselImages.value.length }
function prevSlide() { currentSlide.value = (currentSlide.value - 1 + carouselImages.value.length) % carouselImages.value.length }

async function filterByTag(id: number | null) {
  currentTag.value = id
  page.value = 1
  await loadImages()
}

async function doSearch() {
  page.value = 1
  await loadImages()
}

async function handleApply() { if (!applyReason.value.trim()) return; applying.value = true; try { await applyImagePermission(applyReason.value); ElMessage.success('Application submitted'); showApplyDialog.value = false; permStatus.value = 1 } catch (e: any) { ElMessage.error(e.message) }; applying.value = false }

async function loadImages() {
  const requestId = ++imageRequestId
  loading.value = true
  masonryRefs.length = 0
  try {
    const params: { page: number; pageSize: number; tagId?: number; q?: string } = {
      page: page.value,
      pageSize: pageSize.value,
    }
    if (currentTag.value !== null) params.tagId = currentTag.value
    if (keyword.value.trim()) params.q = keyword.value.trim()

    const r = await getImages(params)
    if (requestId !== imageRequestId) return

    images.value = r.records || []
    total.value = r.total || 0
    await nextTick()
    animateMasonry()
  } catch (e) {
    if (requestId === imageRequestId) {
      images.value = []
      total.value = 0
    }
  } finally {
    if (requestId === imageRequestId) loading.value = false
  }
}
</script>

<style scoped>
.page-section { background: var(--bg-deep); min-height: 60vh; padding: 32px 0 80px; }
.gp-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.gp-header h1 { font-size: 26px; font-weight: 800; color: #fff; margin-bottom: 2px; letter-spacing: -0.3px; }
.gp-header p { font-size: 14px; color: var(--text-secondary); font-family: var(--font-mono); }

.btn-cyber-sm { display: inline-flex; align-items: center; gap: 6px; padding: 8px 18px; font-size: 12px; font-weight: 600; border-radius: 6px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 0.25s; font-family: var(--font-mono); text-decoration: none; }
.btn-cyber-sm:hover { box-shadow: 0 0 20px rgba(0,240,255,0.2); }
.btn-cyber { display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 0.25s; font-family: var(--font-mono); }
.btn-cyber:hover { box-shadow: 0 0 20px rgba(0,240,255,0.2); }
.btn-cyber:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-ghost { padding: 8px 18px; font-size: 13px; font-weight: 500; border-radius: 8px; border: 1px solid var(--border-subtle); background: transparent; color: var(--text-secondary); cursor: pointer; transition: all 0.15s; font-family: inherit; }
.btn-ghost:hover { border-color: rgba(0,240,255,0.15); color: var(--cyber-cyan); }

.carousel-section { margin-bottom: 28px; border-radius: 12px; overflow: hidden; border: 1px solid var(--border-subtle); background: #0A0A0F; }
.carousel-wrapper { position: relative; overflow: hidden; }
.carousel-track { display: flex; transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94); }
.carousel-slide { min-width: 100%; position: relative; cursor: pointer; aspect-ratio: 21/9; max-height: 420px; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.carousel-blur { position: absolute; inset: -20px; background-size: cover; background-position: center; filter: blur(30px) brightness(0.35); z-index: 0; }
.carousel-slide img { position: relative; z-index: 1; width: auto; height: 80%; max-width: 88%; object-fit: contain; border-radius: 8px; }
.carousel-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 40px 36px 20px; background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.05) 55%, transparent 100%); z-index: 2; }
.carousel-info h3 { color: #fff; font-size: 18px; font-weight: 700; margin-bottom: 4px; }
.carousel-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.carousel-tags span { font-size: 11px; font-weight: 500; color: #fff; background: rgba(0,240,255,0.15); padding: 3px 12px; border-radius: 4px; }
.carousel-dots { position: absolute; bottom: 14px; right: 36px; display: flex; gap: 7px; z-index: 3; }
.carousel-dots .dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.5); cursor: pointer; transition: all 0.3s; }
.carousel-dots .dot.active { width: 26px; border-radius: 4px; background: var(--cyber-cyan); }
.carousel-arrow { position: absolute; top: 50%; transform: translateY(-50%); z-index: 3; width: 40px; height: 40px; border: none; border-radius: 50%; background: rgba(0,0,0,0.6); backdrop-filter: blur(8px); border: 1px solid rgba(0,240,255,0.12); color: var(--cyber-cyan); font-size: 24px; cursor: pointer; display: flex; align-items: center; justify-content: center; opacity: 0; transition: all 0.3s; line-height: 1; font-weight: 300; }
.carousel-wrapper:hover .carousel-arrow { opacity: 1; }
.carousel-arrow:hover { background: rgba(0,240,255,0.1); border-color: rgba(0,240,255,0.2); }
.carousel-arrow.prev { left: 14px; } .carousel-arrow.next { right: 14px; }

.gp-toolbar { display: flex; gap: 12px; margin-bottom: 12px; }
.gp-search { flex: 1; max-width: 360px; display: flex; align-items: center; gap: 8px; padding: 0 12px; border: 1px solid var(--border-subtle); border-radius: 8px; background: var(--bg-elevated); color: var(--text-tertiary); }
.gp-search:focus-within { border-color: rgba(0,240,255,0.2); }
.gp-search input { flex: 1; height: 38px; border: none; background: none; font-size: 14px; font-family: inherit; color: var(--text-primary); outline: none; }
.gp-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 24px; }
.gp-tag { padding: 5px 14px; font-size: 13px; font-weight: 500; border-radius: 6px; background: rgba(255,255,255,0.03); border: 1px solid var(--border-subtle); color: var(--text-secondary); cursor: pointer; transition: all 0.15s; }
.gp-tag:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.1); }
.gp-tag.active { background: rgba(0,240,255,0.06); border-color: rgba(0,240,255,0.15); color: var(--cyber-cyan); }

.masonry-grid { columns: 3; column-gap: 14px; }
@media (max-width: 1000px) { .masonry-grid { columns: 2; } }
@media (max-width: 600px) { .masonry-grid { columns: 1; } }
.masonry-item { break-inside: avoid; margin-bottom: 14px; border-radius: 8px; overflow: hidden; cursor: pointer; position: relative; }
.masonry-img { position: relative; border-radius: 8px; overflow: hidden; background: var(--bg-surface); border: 1px solid var(--border-subtle); }
.masonry-img img { width: 100%; display: block; transition: transform 0.4s; }
.masonry-item:hover .masonry-img img { transform: scale(1.06) rotate(-1deg); }
.masonry-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 20px 14px 12px; background: linear-gradient(transparent, rgba(0,0,0,0.6)); opacity: 0; transition: opacity 0.3s; }
.masonry-item:hover .masonry-overlay { opacity: 1; }
.masonry-desc { color: #fff; font-size: 13px; font-weight: 500; margin-bottom: 4px; }
.masonry-tags { display: flex; gap: 4px; }
.masonry-tags span { font-size: 10px; font-weight: 500; color: var(--cyber-cyan); background: rgba(0,240,255,0.1); padding: 1px 8px; border-radius: 4px; }
.gp-pagination { display: flex; justify-content: center; margin-top: 28px; }

.no-perm-tip { text-align: center; padding: 64px 24px; border: 1px solid var(--border-subtle); border-radius: 12px; margin-bottom: 24px; background: var(--bg-elevated); }
.no-perm-tip svg { color: var(--text-tertiary); margin-bottom: 12px; }
.no-perm-tip h3 { font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 8px; }
.no-perm-tip p { font-size: 14px; color: var(--text-tertiary); margin-bottom: 16px; }
.no-perm-tip.rejected-tip svg { color: var(--cyber-purple); }
.perm-badge { font-size: 11px; font-weight: 600; font-family: var(--font-mono); padding: 5px 14px; border-radius: 6px; letter-spacing: 0.5px; }
.perm-badge.pending { background: rgba(0,240,255,0.06); border: 1px solid rgba(0,240,255,0.1); color: var(--cyber-cyan); }
.perm-badge.rejected { background: rgba(124,58,237,0.06); border: 1px solid rgba(124,58,237,0.1); color: var(--cyber-purple); }
.field-dialog { margin-bottom: 16px; }
.field-dialog label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; color: var(--text-secondary); }
.field-dialog .req { color: var(--cyber-purple); }
.char-count { font-size: 11px; color: var(--text-tertiary); text-align: right; margin-top: 4px; }
.input-cyber { width: 100%; padding: 10px 14px; font-size: 14px; font-family: inherit; color: var(--text-primary); background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; outline: none; transition: all 0.2s; box-sizing: border-box; }
.input-cyber:focus { border-color: rgba(0,240,255,0.2); }
</style>

