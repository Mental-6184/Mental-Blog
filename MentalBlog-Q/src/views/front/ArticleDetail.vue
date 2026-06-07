<template>
  <div class="page-section">
    <div class="detail-wrapper">
      <!-- ＝＝ Reading Progress Bar (top) ＝＝ -->
      <div class="read-progress" :style="{ width: readProgress + '%' }"></div>

      <!-- ＝＝ Floating Action Bar ＝＝ -->
      <div class="float-bar" v-if="article && !isMobile">
        <button :class="['float-btn', liked && 'active']" @click="handleLike" :disabled="!userStore.token" aria-label="Like this article" :title="liked ? 'Unlike' : 'Like'">
          <svg width="20" height="20" viewBox="0 0 24 24" :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
          <span class="fb-count">{{ article.likeCount }}</span>
        </button>
        <button :class="['float-btn', favorited && 'active']" @click="handleFavorite" :disabled="!userStore.token" aria-label="Favorite this article" :title="favorited ? 'Unfavorite' : 'Favorite'">
          <svg width="20" height="20" viewBox="0 0 24 24" :fill="favorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
          <span class="fb-count">{{ article.favoriteCount }}</span>
        </button>
        <div class="float-divider"></div>
        <button class="float-btn" @click="copyLink" aria-label="Copy link" title="Copy link">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
        </button>
        <button class="float-btn" @click="scrollToComments" aria-label="Jump to comments" title="Comments">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        </button>
        <div class="float-divider"></div>
        <button class="float-btn" @click="scrollToTop" aria-label="Back to top" title="Top">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="18 15 12 9 6 15"/></svg>
        </button>
      </div>

      <div class="detail-main container">
        <!-- ＝＝ Content Column ＝＝ -->
        <div class="content-col" v-if="article">
          <!-- Breadcrumb -->
          <nav class="breadcrumb" aria-label="Breadcrumb">
            <router-link to="/" class="bc-link">Home</router-link>
            <span class="bc-sep">/</span>
            <router-link v-if="article.categoryName" :to="`/category/${encodeURIComponent(article.categoryName)}`" class="bc-link">{{ article.categoryName }}</router-link>
            <span v-if="article.categoryName" class="bc-sep">/</span>
            <span class="bc-current">{{ article.title }}</span>
          </nav>

          <!-- ＝＝ Article Header ＝＝ -->
          <header class="article-header">
            <div class="ah-meta-row">
              <span v-if="article.categoryName" class="ah-cat" @click="goToCategory(article.categoryName)">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                {{ article.categoryName }}
              </span>
              <span class="ah-dot" v-if="article.categoryName">·</span>
              <time class="ah-date" :datetime="article.publishedAt">{{ formatDate(article.publishedAt) }}</time>
              <span class="ah-dot">·</span>
              <span class="ah-stat">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                {{ formatNumber(article.viewCount) }} views
              </span>
            </div>

            <h1 class="ah-title">{{ article.title }}</h1>

            <div class="ah-tags" v-if="article.tags?.length">
              <span class="ah-tag" v-for="t in article.tags" :key="t" @click.stop="goToTag(t)"># {{ t }}</span>
            </div>

            <div class="ah-author-bar">
              <div class="ah-author">
                <span class="ah-avatar">{{ (article.authorName || 'U')[0].toUpperCase() }}</span>
                <div>
                  <div class="ah-author-name">{{ article.authorName || 'Anonymous' }}</div>
                </div>
              </div>
              <div class="ah-stats">
                <span class="ahs-item" title="Word count">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                  {{ wordCount }} words
                </span>
                <span class="ahs-item" title="Reading time">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  {{ readingTime }} min read
                </span>
                <span class="ahs-item" title="Comments">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                  {{ article.commentCount }}
                </span>
              </div>
            </div>
          </header>

          <!-- ＝＝ Cover Image ＝＝ -->
          <div class="article-cover" v-if="article.coverImage" @click="openCoverLightbox">
            <img :src="article.coverImage" :alt="article.title" loading="eager" />
            <div class="cover-zoom-hint">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/><line x1="11" y1="8" x2="11" y2="14"/><line x1="8" y1="11" x2="14" y2="11"/></svg>
            </div>
          </div>

          <!-- ＝＝ Article Body ＝＝ -->
          <div v-if="loading" class="skeleton-body">
            <div class="sk-line" v-for="i in 12" :key="i" :style="{ width: (60 + Math.random() * 40) + '%' }"></div>
          </div>
          <article v-else class="article-body article-content" v-html="renderedContent" ref="bodyRef"></article>

          <!-- ＝＝ Article Footer Actions ＝＝ -->
          <div class="article-footer-actions">
            <div class="afa-left">
              <button :class="['afa-btn', liked && 'afa-active']" @click="handleLike" :disabled="!userStore.token" :aria-label="liked ? 'Unlike' : 'Like'">
                <svg width="20" height="20" viewBox="0 0 24 24" :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
                <span>{{ liked ? 'Liked' : 'Like' }}</span>
                <span v-if="article.likeCount > 0" class="afa-num">{{ article.likeCount }}</span>
              </button>
              <button :class="['afa-btn', favorited && 'afa-active']" @click="handleFavorite" :disabled="!userStore.token" :aria-label="favorited ? 'Unfavorite' : 'Favorite'">
                <svg width="20" height="20" viewBox="0 0 24 24" :fill="favorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                <span>{{ favorited ? 'Saved' : 'Save' }}</span>
              </button>
            </div>
            <div class="afa-right">
              <button class="afa-btn" @click="copyLink" aria-label="Copy link">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
                <span class="copy-feedback" v-if="linkCopied">Copied!</span>
              </button>
            </div>
          </div>

          <!-- ＝＝ Author Card ＝＝ -->
          <div class="author-card-full">
            <div class="acf-top">
              <span class="acf-avatar">{{ (article.authorName || 'U')[0].toUpperCase() }}</span>
              <div class="acf-info">
                <div class="acf-name">{{ article.authorName || 'Anonymous' }}</div>
                <div class="acf-role">Author</div>
              </div>
            </div>
            <p class="acf-bio">{{ article.authorBio || 'Write something about yourself. It will appear here on every article you publish.' }}</p>
          </div>

          <!-- ＝＝ Comments Section ＝＝ -->
          <div class="comments-section" ref="commentsRef">
            <div class="comments-head">
              <h3 id="comments-title">Comments</h3>
              <span class="comments-count-badge">{{ article.commentCount }}</span>
            </div>

            <div v-if="userStore.token" class="comment-form-card">
              <div class="cfc-top">
                <span class="cfc-avatar">{{ (userStore.nickname || userStore.username || 'U')[0].toUpperCase() }}</span>
                <div class="cfc-body">
                  <div v-if="replyTo" class="cfc-replying">
                    Replying to <strong>@{{ replyTo.userNickname }}</strong>
                    <button class="cfc-cancel-reply" @click="cancelReply" aria-label="Cancel reply">&times;</button>
                  </div>
                  <textarea
                    v-model="commentContent"
                    class="cfc-textarea"
                    :placeholder="replyTo ? 'Write your reply...' : 'Share your thoughts...'"
                    :rows="replyTo ? 2 : 3"
                    ref="commentInput"
                    @keydown.ctrl.enter="submitComment"
                    @keydown.meta.enter="submitComment"
                  ></textarea>
                  <div class="cfc-actions">
                    <span class="cfc-hint">Ctrl+Enter to submit</span>
                    <button v-if="replyTo" class="btn-ghost-sm" @click="cancelReply">Cancel reply</button>
                    <button class="btn-cyber" @click="submitComment" :disabled="!commentContent.trim()">
                      {{ replyTo ? 'Reply' : 'Submit' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="login-prompt-card">
              <div class="lpc-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg>
              </div>
              <p>Sign in to join the conversation</p>
              <router-link to="/login" class="btn btn-primary btn-sm">Login to Comment</router-link>
            </div>

            <div class="comment-sort" v-if="comments.length > 1">
              <button :class="['cs-opt', sortBy === 'newest' && 'cs-active']" @click="sortBy = 'newest'">Newest</button>
              <button :class="['cs-opt', sortBy === 'oldest' && 'cs-active']" @click="sortBy = 'oldest'">Oldest</button>
            </div>

            <div v-if="sortedComments.length === 0 && !loading" class="empty-state">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.3"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
              <p>No comments yet. Be the first to share your thoughts!</p>
            </div>
            <template v-for="c in sortedComments" :key="c.id">
              <CommentItem :comment="c" :depth="0" :articleId="article?.id" @reply="onReply" @refresh="loadComments" />
            </template>

            <div class="comments-load-more" v-if="hasMoreComments">
              <button class="btn-ghost-sm" @click="loadMoreComments" :disabled="loadingMore">
                {{ loadingMore ? 'Loading...' : 'Load more comments' }}
              </button>
            </div>
          </div>
        </div>

        <!-- ＝＝ Empty State ＝＝ -->
        <div v-if="!article && !loading" class="empty-state" style="padding: 100px 0">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" opacity="0.3"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          <p>Article not found</p>
          <router-link to="/" class="btn btn-ghost btn-sm" style="margin-top:12px">Back to Home</router-link>
        </div>

        <!-- ＝＝ TOC Sidebar ＝＝ -->
        <aside class="toc-col" v-if="tocItems.length > 1">
          <div class="toc-sticky">
            <div class="toc-card">
              <div class="toc-title">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                CONTENTS
              </div>
              <div class="toc-body" ref="tocBody">
                <div
                  v-for="item in tocItems"
                  :key="item.id"
                  :class="['toc-link', 'tl-' + item.level, { active: activeToc === item.id }]"
                  @click="scrollToHeading(item.id)"
                  :title="item.text"
                >
                  <span class="toc-dot"></span>
                  <span class="toc-text">{{ item.text }}</span>
                </div>
              </div>
            </div>
            <div class="toc-progress-text">
              <span>{{ Math.round(readProgress) }}% read</span>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- ＝＝ Cover Lightbox ＝＝ -->
    <Teleport to="body">
      <div v-if="showCoverLightbox" class="lightbox-overlay" @click.self="showCoverLightbox = false" @keydown.esc="showCoverLightbox = false">
        <button class="lightbox-close" @click="showCoverLightbox = false" aria-label="Close">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
        <img :src="article?.coverImage" :alt="article?.title" class="lightbox-img" />
      </div>
    </Teleport>

    <!-- ＝＝ Mobile Bottom Action Bar ＝＝ -->
    <div class="mobile-actions" v-if="isMobile && article">
      <button :class="['ma-btn', liked && 'ma-active']" @click="handleLike" :disabled="!userStore.token">
        <svg width="22" height="22" viewBox="0 0 24 24" :fill="liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
        <span>{{ article.likeCount }}</span>
      </button>
      <button :class="['ma-btn', favorited && 'ma-active']" @click="handleFavorite" :disabled="!userStore.token">
        <svg width="22" height="22" viewBox="0 0 24 24" :fill="favorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
        <span>{{ article.favoriteCount }}</span>
      </button>
      <div class="ma-divider"></div>
      <button class="ma-btn" @click="scrollToComments">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        <span>{{ article.commentCount }}</span>
      </button>
      <button class="ma-btn" @click="copyLink">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getArticleById, recordView } from '@/api/article'
import { getComments, createComment } from '@/api/comment'
import { toggleLikeArticle, checkLikeArticle, toggleFavoriteArticle, checkFavoriteArticle } from '@/api/interact'
import CommentItem from '@/components/front/CommentItem.vue'
import { gsap } from 'gsap'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// ── State ──
const article = ref<any>(null)
const comments = ref<any[]>([])
const loading = ref(false)
const commentContent = ref('')
const replyTo = ref<{ id: number; userNickname: string } | null>(null)
const liked = ref(false)
const favorited = ref(false)
const tocItems = ref<{ id: string; text: string; level: number }[]>([])
const activeToc = ref('')
const readProgress = ref(0)
const linkCopied = ref(false)
const showCoverLightbox = ref(false)
const sortBy = ref<'newest' | 'oldest'>('newest')
const commentPage = ref(1)
const hasMoreComments = ref(true)
const loadingMore = ref(false)
const bodyRef = ref<HTMLElement | null>(null)
const commentsRef = ref<HTMLElement | null>(null)
const commentInput = ref<HTMLTextAreaElement | null>(null)
const tocBody = ref<HTMLElement | null>(null)
let tocObserver: IntersectionObserver | null = null
let progressObserver: IntersectionObserver | null = null

const isMobile = ref(false)

// ── Computed ──
const wordCount = computed(() => {
  if (!article.value?.content) return 0
  const text = article.value.content.replace(/[#*\->\[\]()`!|~]/g, ' ').replace(/\s+/g, ' ').trim()
  return text.split(/\s+/).filter(Boolean).length
})

const readingTime = computed(() => {
  if (!wordCount.value) return 0
  return Math.max(1, Math.ceil(wordCount.value / 250))
})

const sortedComments = computed(() => {
  const list = [...comments.value]
  if (sortBy.value === 'newest') return list.reverse()
  return list
})

// ── Marked setup ──
marked.use({ breaks: true, gfm: true })
const renderer = new marked.Renderer()

renderer.heading = ({ text, depth }: { text: string; depth: number }) => {
  const id = text
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^a-z0-9一-鿿-]/g, '')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
  tocItems.value.push({ id, text, level: depth })
  return `<h${depth} id="${id}">${text}</h${depth}>`
}

// Add language labels and copy buttons to code blocks
renderer.code = ({ text, lang }: { text: string; lang?: string }) => {
  const langLabel = lang || 'code'
  const escaped = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
  return `<div class="code-block-wrapper">
    <div class="code-block-header">
      <span class="code-lang-label">${langLabel}</span>
      <button class="code-copy-btn" onclick="(function(btn){var code=btn.parentElement.parentElement.querySelector('code');var text=code.textContent||code.innerText;navigator.clipboard.writeText(text).then(function(){btn.textContent='Copied!';setTimeout(function(){btn.textContent='Copy'},2000)}).catch(function(){btn.textContent='Failed';setTimeout(function(){btn.textContent='Copy'},2000)})})(event.target)" aria-label="Copy code">Copy</button>
    </div>
    <pre><code class="language-${lang || ''}">${escaped}</code></pre>
  </div>`
}

marked.use({ renderer })

const renderedContent = computed(() => {
  tocItems.value = []
  if (!article.value?.content) return ''
  return marked.parse(article.value.content) as string
})

// ── Methods ──
function formatDate(s: string) {
  if (!s) return ''
  const d = new Date(s)
  return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

function formatNumber(n: number) {
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

function goToCategory(name: string) { router.push(`/category/${encodeURIComponent(name)}`) }
function goToTag(name: string) { router.push(`/tag/${encodeURIComponent(name)}`) }

function scrollToHeading(id: string) {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }

function scrollToComments() {
  commentsRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function openCoverLightbox() {
  if (article.value?.coverImage) showCoverLightbox.value = true
}

async function copyLink() {
  try {
    await navigator.clipboard.writeText(window.location.href)
    linkCopied.value = true
    setTimeout(() => (linkCopied.value = false), 2000)
  } catch {
    linkCopied.value = true
    setTimeout(() => (linkCopied.value = false), 2000)
  }
}

async function loadComments() {
  try {
    commentPage.value = 1
    const r = await getComments(Number(route.params.id))
    comments.value = r.data || []
    hasMoreComments.value = (r.data?.length || 0) >= 50
  } catch (e) { /* ignore */ }
}

async function loadMoreComments() {
  loadingMore.value = true
  try {
    commentPage.value++
    await getComments(Number(route.params.id))
    hasMoreComments.value = false
  } catch (e) { /* ignore */ }
  loadingMore.value = false
}

function onReply(c: any) {
  replyTo.value = { id: c.id, userNickname: c.userNickname }
  commentContent.value = ''
  nextTick(() => {
    commentInput.value?.focus()
    commentInput.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
}

function cancelReply() { replyTo.value = null; commentContent.value = '' }

async function submitComment() {
  if (!commentContent.value.trim()) return
  try {
    await createComment({
      articleId: Number(route.params.id),
      content: commentContent.value.trim(),
      parentId: replyTo.value?.id || null,
    })
    commentContent.value = ''
    replyTo.value = null
    if (article.value) article.value.commentCount = (article.value.commentCount || 0) + 1
    await loadComments()
  } catch (e) { /* ignore */ }
}

async function handleLike() {
  if (!userStore.token) return
  try {
    const res = await toggleLikeArticle(Number(route.params.id))
    if (res.data) {
      liked.value = !!res.data.liked
      article.value.likeCount += liked.value ? 1 : -1
    }
  } catch (e) { /* ignore */ }
}

async function handleFavorite() {
  if (!userStore.token) return
  try {
    const res = await toggleFavoriteArticle(Number(route.params.id))
    if (res.data) {
      favorited.value = !!res.data.favorited
      article.value.favoriteCount += favorited.value ? 1 : -1
    }
  } catch (e) { /* ignore */ }
}

function recordViewCount(id: number) {
  const key = `v_${id}`
  const last = localStorage.getItem(key)
  const now = Date.now()
  if (last && now - Number(last) < 86400000) return
  localStorage.setItem(key, String(now))
  recordView(id).catch(() => {})
}

function highlightCode() {
  document.querySelectorAll('.article-content pre code').forEach((b) => {
    try { hljs.highlightElement(b as HTMLElement) } catch { /* ignore */ }
  })
}

function setupObservers() {
  const hs = document.querySelectorAll('.article-content h1, .article-content h2, .article-content h3')
  if (hs.length && !tocObserver) {
    tocObserver = new IntersectionObserver(
      (entries) => { for (const e of entries) { if (e.isIntersecting) activeToc.value = e.target.id } },
      { rootMargin: '-80px 0px -60% 0px' },
    )
    hs.forEach((h) => tocObserver!.observe(h))
  }

  if (bodyRef.value && !progressObserver) {
    progressObserver = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          const rect = entry.boundingClientRect
          const total = entry.target.scrollHeight
          const visible = window.innerHeight - rect.top
          const raw = Math.min(1, visible / total)
          const scrolled = Math.min(1, window.scrollY / (document.documentElement.scrollHeight - window.innerHeight))
          readProgress.value = Math.max(readProgress.value, Math.round(Math.max(raw, scrolled) * 100))
        }
      },
      { threshold: Array.from({ length: 20 }, (_, i) => i / 20) },
    )
    progressObserver.observe(bodyRef.value)
  }
}

function handleScroll() {
  const total = document.documentElement.scrollHeight - window.innerHeight
  if (total > 0) readProgress.value = Math.round((window.scrollY / total) * 100)
}

function handleResize() { isMobile.value = window.innerWidth < 768 }

function handleKeyboard(e: KeyboardEvent) {
  if (e.key === 'Escape' && showCoverLightbox.value) { showCoverLightbox.value = false; return }
  if (e.key === 'c' && !e.ctrlKey && !e.metaKey && !e.altKey && document.activeElement?.tagName !== 'INPUT' && document.activeElement?.tagName !== 'TEXTAREA') {
    scrollToComments()
  }
}

// ── Lifecycle ──
onMounted(async () => {
  handleResize()
  window.addEventListener('resize', handleResize)
  window.addEventListener('scroll', handleScroll, { passive: true })
  window.addEventListener('keydown', handleKeyboard)

  loading.value = true
  try {
    const artRes = await getArticleById(Number(route.params.id))
    article.value = artRes.data
    document.title = `${article.value.title} — MentalBlog`
    recordViewCount(Number(route.params.id))

    if (userStore.token) {
      const [lr, fr] = await Promise.all([
        checkLikeArticle(Number(route.params.id)),
        checkFavoriteArticle(Number(route.params.id)),
      ])
      liked.value = !!lr.data?.liked
      favorited.value = !!fr.data?.favorited
    }
    await loadComments()
  } catch (e) { /* ignore */ }
  loading.value = false
  nextTick(() => { highlightCode(); setupObservers() })
})

onUnmounted(() => {
  if (tocObserver) tocObserver.disconnect()
  if (progressObserver) progressObserver.disconnect()
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('keydown', handleKeyboard)
  document.title = 'MentalBlog'
})

watch(
  () => route.params.id,
  async (newId) => {
    if (!newId) return
    loading.value = true
    tocItems.value = []
    readProgress.value = 0
    activeToc.value = ''
    if (tocObserver) tocObserver.disconnect()
    if (progressObserver) progressObserver.disconnect()
    try {
      const artRes = await getArticleById(Number(newId))
      article.value = artRes.data
      document.title = `${article.value.title} — MentalBlog`
      recordViewCount(Number(newId))
      if (userStore.token) {
        const [lr, fr] = await Promise.all([checkLikeArticle(Number(newId)), checkFavoriteArticle(Number(newId))])
        liked.value = !!lr.data?.liked
        favorited.value = !!fr.data?.favorited
      }
      await loadComments()
    } catch (e) { /* ignore */ }
    loading.value = false
    nextTick(() => { highlightCode(); setupObservers(); gsap.from(".ah-title", { opacity: 0, y: 15, duration: 0.4, ease: "power3.out" }); window.scrollTo({ top: 0, behavior: 'instant' as ScrollBehavior }) })
  },
)
</script>

<style scoped>
/* ════════════════════════════════════════════
   Article Detail — Cyberpunk Dark Tech
   ════════════════════════════════════════════ */

.page-section { background: var(--bg-deep); padding-bottom: 48px; min-height: 100vh; }
.detail-wrapper { position: relative; max-width: 1200px; margin: 0 auto; }

/* ── Reading Progress Bar ── */
.read-progress {
  position: fixed; top: 0; left: 0; height: 3px;
  background: linear-gradient(90deg, var(--cyber-cyan), var(--cyber-purple));
  z-index: 1002; transition: width 0.15s linear;
  box-shadow: 0 0 12px var(--cyber-cyan-glow);
}

/* ── Floating Action Bar ── */
.float-bar {
  position: fixed; left: max(16px, calc((100vw - 1200px) / 2 - 64px)); top: 50%;
  transform: translateY(-50%); display: flex; flex-direction: column; align-items: center;
  gap: 6px; padding: 10px 6px;
  background: var(--bg-elevated); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(0,240,255,0.08); border-radius: 12px; z-index: 50;
  box-shadow: 0 4px 24px rgba(0,0,0,0.4);
}
.float-btn {
  width: 42px; height: 42px; display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 1px; border: none;
  background: transparent; color: var(--text-tertiary); border-radius: 8px;
  cursor: pointer; transition: all 150ms var(--ease-out); font-family: inherit; position: relative;
}
.float-btn:hover { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); }
.float-btn.active { color: var(--cyber-cyan); }
.float-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.fb-count { font-size: 10px; font-weight: 600; font-family: var(--font-mono); line-height: 1; }
.float-divider { width: 24px; height: 1px; background: rgba(255,255,255,0.06); margin: 4px 0; }

/* ── Main Layout ── */
.detail-main { display: flex; gap: 40px; align-items: flex-start; padding-top: 16px; padding-left: 72px; padding-right: 24px; }
.content-col { flex: 1; min-width: 0; max-width: 740px; }

/* ── Breadcrumb ── */
.breadcrumb { display: flex; align-items: center; gap: 6px; padding: 12px 0; font-size: 13px; font-family: var(--font-mono); overflow: hidden; }
.bc-link { color: var(--text-tertiary); text-decoration: none; transition: color 150ms; white-space: nowrap; }
.bc-link:hover { color: var(--cyber-cyan); }
.bc-sep { color: var(--text-tertiary); opacity: 0.4; }
.bc-current { color: var(--text-secondary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

/* ═══ Article Header ═══ */
.article-header { margin-bottom: 28px; }
.ah-meta-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; font-size: 12px; font-family: var(--font-mono); }
.ah-cat {
  display: inline-flex; align-items: center; gap: 5px; padding: 3px 10px; border-radius: 4px;
  background: rgba(0,240,255,0.06); border: 1px solid rgba(0,240,255,0.12);
  color: var(--cyber-cyan); font-weight: 600; font-size: 11px; cursor: pointer;
  transition: all 150ms; text-transform: uppercase; letter-spacing: 0.5px;
}
.ah-cat:hover { background: rgba(0,240,255,0.12); }
.ah-dot { color: var(--text-tertiary); opacity: 0.4; }
.ah-date { color: var(--text-tertiary); }
.ah-stat { display: inline-flex; align-items: center; gap: 4px; color: var(--text-tertiary); }

.ah-title { font-size: clamp(26px, 4vw, 38px); font-weight: 800; color: #fff; letter-spacing: -0.6px; line-height: 1.25; margin-bottom: 16px; }

.ah-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 20px; }
.ah-tag {
  font-size: 12px; font-weight: 500; color: var(--text-secondary); padding: 3px 12px;
  border-radius: 4px; background: rgba(255,255,255,0.03); border: 1px solid var(--border-subtle);
  cursor: pointer; transition: all 150ms; font-family: var(--font-mono); font-size: 11px;
}
.ah-tag:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.04); }

.ah-author-bar { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 16px 0; border-top: 1px solid var(--border-subtle); border-bottom: 1px solid var(--border-subtle); flex-wrap: wrap; }
.ah-author { display: flex; align-items: center; gap: 10px; }
.ah-avatar { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 16px; font-weight: 700; flex-shrink: 0; font-family: var(--font-mono); }
.ah-author-name { font-size: 14px; font-weight: 600; color: var(--text-primary); }

.ah-stats { display: flex; align-items: center; gap: 16px; }
.ahs-item { display: inline-flex; align-items: center; gap: 5px; font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); }

/* ═══ Cover ═══ */
.article-cover {
  position: relative; margin-bottom: 32px; border-radius: 12px; overflow: hidden;
  border: 1px solid var(--border-subtle); cursor: zoom-in; transition: all 250ms;
}
.article-cover:hover { border-color: rgba(0,240,255,0.2); }
.article-cover:hover .cover-zoom-hint { opacity: 1; }
.article-cover img { width: 100%; max-height: 460px; object-fit: cover; display: block; }
.cover-zoom-hint {
  position: absolute; bottom: 12px; right: 12px; width: 36px; height: 36px;
  border-radius: 8px; background: rgba(0,0,0,0.6); backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center; color: #fff;
  opacity: 0; transition: opacity 200ms; pointer-events: none;
}

/* ═══ Skeleton ═══ */
.skeleton-body { margin-bottom: 40px; }
.sk-line { height: 16px; background: var(--bg-elevated); border-radius: 4px; margin-bottom: 12px; animation: skPulse 1.5s ease-in-out infinite; }
@keyframes skPulse { 0%, 100% { opacity: 0.3; } 50% { opacity: 0.6; } }

/* ═══ Article Body ═══ */
.article-body { margin-bottom: 40px; }

/* ═══ Footer Actions ═══ */
.article-footer-actions { display: flex; align-items: center; justify-content: space-between; padding: 28px 0; border-top: 1px solid var(--border-subtle); border-bottom: 1px solid var(--border-subtle); margin-bottom: 28px; gap: 12px; }
.afa-left, .afa-right { display: flex; gap: 8px; }
.afa-btn {
  display: inline-flex; align-items: center; gap: 7px; padding: 10px 22px;
  font-size: 14px; font-weight: 600; border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px; background: var(--bg-elevated); color: var(--text-secondary);
  cursor: pointer; transition: all 200ms var(--ease-out); font-family: inherit; position: relative;
}
.afa-btn:hover:not(:disabled) { border-color: var(--cyber-cyan); color: var(--cyber-cyan); background: rgba(0,240,255,0.04); }
.afa-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.afa-btn:active:not(:disabled) { transform: scale(0.96); }
.afa-active { border-color: var(--cyber-cyan) !important; color: var(--cyber-cyan) !important; background: rgba(0,240,255,0.06) !important; }
.afa-num { font-family: var(--font-mono); font-size: 12px; opacity: 0.8; }
.copy-feedback { position: absolute; top: -28px; left: 50%; transform: translateX(-50%); font-size: 11px; font-weight: 500; color: var(--cyber-cyan); white-space: nowrap; font-family: var(--font-mono); }

/* ═══ Author Card ═══ */
.author-card-full { padding: 24px; margin-bottom: 32px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 12px; transition: border-color 250ms; }
.author-card-full:hover { border-color: rgba(0,240,255,0.12); }
.acf-top { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; }
.acf-avatar { width: 52px; height: 52px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 700; flex-shrink: 0; font-family: var(--font-mono); }
.acf-name { font-size: 16px; font-weight: 700; color: var(--text-primary); }
.acf-role { font-size: 12px; color: var(--text-tertiary); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.5px; }
.acf-bio { font-size: 13px; color: var(--text-tertiary); line-height: 1.6; margin: 0; }

/* ═══ Comments ═══ */
.comments-section { scroll-margin-top: 80px; }
.comments-head { display: flex; align-items: center; gap: 10px; font-size: 20px; font-weight: 700; color: #fff; margin-bottom: 24px; }
.comments-count-badge { font-size: 12px; font-weight: 600; color: var(--cyber-cyan); background: rgba(0,240,255,0.06); border: 1px solid rgba(0,240,255,0.12); padding: 2px 12px; border-radius: 4px; font-family: var(--font-mono); }

/* Comment form card */
.comment-form-card { background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 12px; padding: 18px; margin-bottom: 24px; transition: border-color 250ms; }
.comment-form-card:focus-within { border-color: rgba(0,240,255,0.2); }
.cfc-top { display: flex; gap: 12px; }
.cfc-avatar { width: 34px; height: 34px; border-radius: 50%; background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; flex-shrink: 0; font-family: var(--font-mono); }
.cfc-body { flex: 1; min-width: 0; }
.cfc-replying { font-size: 12px; color: var(--text-tertiary); margin-bottom: 8px; display: flex; align-items: center; gap: 8px; }
.cfc-replying strong { color: var(--cyber-cyan); }
.cfc-cancel-reply { background: none; border: none; color: var(--text-tertiary); cursor: pointer; font-size: 16px; line-height: 1; padding: 0 2px; transition: color 150ms; }
.cfc-cancel-reply:hover { color: var(--cyber-red); }
.cfc-textarea { width: 100%; padding: 10px 14px; font-size: 14px; font-family: var(--font-sans); line-height: 1.6; border: 1px solid var(--border-subtle); border-radius: 8px; background: var(--bg-surface); color: var(--text-primary); resize: vertical; min-height: 44px; outline: none; transition: border-color 150ms; caret-color: var(--cyber-cyan); }
.cfc-textarea::placeholder { color: var(--text-tertiary); }
.cfc-textarea:focus { border-color: rgba(0,240,255,0.3); }
.cfc-actions { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-top: 10px; }
.cfc-hint { font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); margin-right: auto; }

.btn-ghost-sm { padding: 6px 14px; font-size: 12px; font-weight: 500; border-radius: 6px; border: 1px solid var(--border-subtle); background: transparent; color: var(--text-secondary); cursor: pointer; transition: all 150ms; font-family: inherit; }
.btn-ghost-sm:hover { border-color: rgba(0,240,255,0.15); color: var(--cyber-cyan); }
.btn-cyber { display: inline-flex; align-items: center; justify-content: center; padding: 8px 20px; font-size: 13px; font-weight: 600; border-radius: 8px; border: none; background: var(--cyber-cyan); color: #0A0A0F; cursor: pointer; transition: all 200ms; font-family: var(--font-mono); letter-spacing: 0.5px; }
.btn-cyber:hover:not(:disabled) { box-shadow: 0 0 24px rgba(0,240,255,0.25); transform: translateY(-1px); }
.btn-cyber:disabled { opacity: 0.4; cursor: not-allowed; }
.btn-cyber:active:not(:disabled) { transform: scale(0.96); }

.login-prompt-card { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 32px; margin-bottom: 24px; background: var(--bg-elevated); border: 1px solid var(--border-subtle); border-radius: 12px; text-align: center; }
.lpc-icon { color: var(--text-tertiary); margin-bottom: 4px; }
.login-prompt-card p { font-size: 14px; color: var(--text-tertiary); }

.comment-sort { display: flex; gap: 6px; margin-bottom: 16px; }
.cs-opt { padding: 4px 14px; font-size: 12px; font-weight: 500; font-family: var(--font-mono); border: 1px solid var(--border-subtle); border-radius: 6px; background: transparent; color: var(--text-tertiary); cursor: pointer; transition: all 150ms; }
.cs-opt:hover { color: var(--text-primary); border-color: var(--border-default); }
.cs-active { color: var(--cyber-cyan) !important; border-color: rgba(0,240,255,0.2) !important; background: rgba(0,240,255,0.04) !important; }

.comments-load-more { display: flex; justify-content: center; padding: 16px 0; }

/* ═══ TOC Sidebar ═══ */
.toc-col { width: 220px; flex-shrink: 0; display: none; position: relative; }
@media (min-width: 1024px) { .toc-col { display: block; } }
.toc-sticky { position: fixed; top: 80px; width: 220px; max-height: calc(100vh - 140px); overflow-y: auto; z-index: 10; }
.toc-sticky::-webkit-scrollbar { width: 3px; }
.toc-sticky::-webkit-scrollbar-thumb { background: var(--border-subtle); border-radius: 2px; }
.toc-card { background: var(--bg-elevated); border: 1px solid rgba(0,240,255,0.06); border-radius: 12px; padding: 4px 0; backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); }
.toc-title { display: flex; align-items: center; gap: 6px; font-size: 11px; font-weight: 700; padding: 12px 14px 10px; border-bottom: 1px solid var(--border-subtle); margin-bottom: 6px; color: var(--cyber-cyan); font-family: var(--font-mono); letter-spacing: 1.5px; }
.toc-body { display: flex; flex-direction: column; padding: 0 6px 8px; }
.toc-link { display: flex; align-items: center; gap: 8px; padding: 5px 10px; font-size: 13px; color: var(--text-secondary); cursor: pointer; border-radius: 6px; transition: all 150ms; line-height: 1.4; }
.toc-link:hover { background: rgba(0,240,255,0.04); color: var(--text-primary); }
.toc-link.active { color: var(--cyber-cyan); background: rgba(0,240,255,0.06); font-weight: 500; }
.toc-dot { width: 4px; height: 4px; border-radius: 50%; background: var(--border-default); flex-shrink: 0; transition: all 150ms; }
.toc-link.active .toc-dot { background: var(--cyber-cyan); box-shadow: 0 0 6px var(--cyber-cyan-glow); }
.toc-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.tl-2 { padding-left: 10px; }
.tl-3 { padding-left: 24px; font-size: 12px; }
.toc-progress-text { text-align: center; font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono); padding: 10px 0 4px; }

/* ═══ Lightbox ═══ */
.lightbox-overlay { position: fixed; inset: 0; z-index: 10000; background: rgba(0,0,0,0.92); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; cursor: zoom-out; animation: fadeIn 200ms ease-out; }
.lightbox-close { position: absolute; top: 20px; right: 20px; width: 44px; height: 44px; display: flex; align-items: center; justify-content: center; border: 1px solid rgba(255,255,255,0.15); border-radius: 8px; background: rgba(0,0,0,0.4); color: #fff; cursor: pointer; transition: all 150ms; z-index: 1; }
.lightbox-close:hover { background: rgba(255,255,255,0.1); border-color: rgba(255,255,255,0.3); }
.lightbox-img { max-width: 90vw; max-height: 90vh; object-fit: contain; border-radius: 4px; cursor: default; }

/* ═══ Mobile Bottom Action Bar ═══ */
.mobile-actions { display: flex; align-items: center; justify-content: center; gap: 4px; position: fixed; bottom: 0; left: 0; right: 0; z-index: 99; background: var(--bg-elevated); border-top: 1px solid var(--border-subtle); padding: 6px 16px; padding-bottom: max(6px, env(safe-area-inset-bottom)); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px); }
.ma-btn { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 2px; padding: 6px 4px; border: none; background: transparent; color: var(--text-tertiary); border-radius: 8px; cursor: pointer; transition: all 150ms; font-family: inherit; font-size: 10px; font-weight: 500; }
.ma-btn:hover { color: var(--cyber-cyan); background: rgba(0,240,255,0.04); }
.ma-btn:disabled { opacity: 0.3; cursor: not-allowed; }
.ma-active { color: var(--cyber-cyan) !important; }
.ma-divider { width: 1px; height: 28px; background: var(--border-subtle); }

/* ═══ Responsive ═══ */
@media (max-width: 1023px) { .detail-main { padding-left: 24px; } .float-bar { display: none; } .toc-col { display: none; } }
@media (max-width: 767px) {
  .page-section { padding-bottom: 80px; }
  .detail-main { padding-left: 16px; padding-right: 16px; }
  .ah-author-bar { flex-direction: column; align-items: flex-start; gap: 12px; }
  .ah-stats { gap: 12px; }
  .article-footer-actions { flex-direction: column; align-items: stretch; }
  .afa-left, .afa-right { justify-content: center; }
  .afa-btn { flex: 1; justify-content: center; }
  .author-card-full { padding: 18px; }
  .comment-form-card { padding: 14px; }
  .breadcrumb { font-size: 11px; }
}
</style>

<!-- ═══ Global code block styles (unscoped, rendered in v-html) ═══ -->
<style>
.code-block-wrapper { margin: 1.4em 0; border-radius: var(--radius-md); overflow: hidden; border: 1px solid rgba(255,255,255,0.06); background: #0D1117; }
.code-block-header { display: flex; align-items: center; justify-content: space-between; padding: 6px 14px; background: rgba(255,255,255,0.03); border-bottom: 1px solid rgba(255,255,255,0.04); }
.code-lang-label { font-size: 11px; font-weight: 600; color: var(--text-tertiary); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.5px; }
.code-copy-btn { padding: 2px 10px; font-size: 11px; font-weight: 500; font-family: var(--font-mono); border: 1px solid rgba(255,255,255,0.08); border-radius: 4px; background: transparent; color: var(--text-tertiary); cursor: pointer; transition: all 150ms; }
.code-copy-btn:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.04); }
.code-block-wrapper pre { margin: 0 !important; padding: 16px 18px !important; background: transparent !important; border: none !important; border-radius: 0 !important; font-size: 13px !important; line-height: 1.6 !important; }
</style>
