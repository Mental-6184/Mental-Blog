<template>
  <!-- ═══════════════════════════════
       SIDEBAR — Context-aware
       ═══════════════════════════════ -->

  <!-- ＝＝ HOME MODE ＝＝ -->
  <aside class="sb" v-if="context === 'home'">
    <!-- Author Card (redesigned) -->
    <div class="sb-card author-card">
      <div class="ac-cover"></div>
      <div class="ac-body">
        <span class="ac-avatar">{{ siteName[0] }}</span>
        <div class="ac-name">{{ siteName }}</div>
        <div class="ac-role">
          <span class="ac-role-dot"></span>
          Full Stack Developer
        </div>
        <p class="ac-bio">用代码构建数字世界 · 记录技术与思考</p>
        <div class="ac-stats-row">
          <div class="ac-stat">
            <span class="acs-num">{{ articleCount }}</span>
            <span class="acs-lbl">Posts</span>
          </div>
          <div class="ac-stat">
            <span class="acs-num">{{ catCount }}</span>
            <span class="acs-lbl">Cats</span>
          </div>
          <div class="ac-stat">
            <span class="acs-num">{{ tagCount }}</span>
            <span class="acs-lbl">Tags</span>
          </div>
        </div>
        <div class="ac-socials">
          <a href="https://github.com" target="_blank" aria-label="GitHub" class="acs-link">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/></svg>
          </a>
          <a href="mailto:admin@mentalblog.com" aria-label="Email" class="acs-link">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          </a>
          <a href="/api/rss" target="_blank" aria-label="RSS" class="acs-link">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 11a9 9 0 0 1 9 9"/><path d="M4 4a16 16 0 0 1 16 16"/><circle cx="5" cy="19" r="1"/></svg>
          </a>
        </div>
      </div>
    </div>

    <!-- Hot Articles (redesigned) -->
    <div class="sb-card" v-if="hotArticles?.length">
      <div class="sb-card-head">
        <div class="sch-icon sch-icon-fire">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></svg>
        </div>
        <span class="sch-label">Hot Articles</span>
      </div>
      <div class="hot-list">
        <div v-for="(a, i) in hotArticles.slice(0, 5)" :key="a.id" class="hot-item" @click="$router.push(`/article/${a.id}`)">
          <span class="hot-rank" :class="{ 'hot-top': i < 3 }">{{ String(i + 1).padStart(2, '0') }}</span>
          <div class="hot-body">
            <div class="hot-title">{{ a.title }}</div>
            <div class="hot-meta">
              <span class="hot-views">
                <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                {{ fmtNum(a.viewCount) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Tag Cloud (redesigned) -->
    <div class="sb-card" v-if="tags.length">
      <div class="sb-card-head">
        <div class="sch-icon sch-icon-tag">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
        </div>
        <span class="sch-label">Explore Tags</span>
      </div>
      <div class="tag-cloud">
        <span
          v-for="t in tags.slice(0, 18)"
          :key="t.id"
          class="tc-pill"
          :class="'tc-size-' + (t.articleCount > 10 ? 'l' : t.articleCount > 5 ? 'm' : 's')"
          @click="$router.push(`/tag/${t.slug}`)"
        >{{ t.name }}</span>
      </div>
    </div>

    <!-- View all tags -->
    <router-link to="/tags" class="sb-link-card">
      <span>View all tags</span>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
    </router-link>
  </aside>

  <!-- ═══════════════════════════════
       CATEGORY PAGE MODE
       ═══════════════════════════════ -->
  <aside class="sb" v-else-if="context === 'category'">
    <!-- Category Info Card -->
    <div class="sb-card cat-info-card">
      <div class="cic-icon">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
      </div>
      <div class="cic-name">{{ contextName }}</div>
      <div class="cic-count">{{ articleCount }} articles</div>
    </div>

    <!-- All Categories -->
    <div class="sb-card" v-if="categories.length">
      <div class="sb-card-head">
        <span class="sch-label">All Categories</span>
      </div>
      <div class="cat-nav-list">
        <div
          v-for="c in categories"
          :key="c.id"
          :class="['cn-item', { 'cn-active': c.slug === contextSlug }]"
          @click="$router.push(`/category/${c.slug}`)"
        >
          <span class="cn-name">{{ c.name }}</span>
          <span class="cn-count">{{ c.count || c.articleCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- Tags used in this category -->
    <div class="sb-card" v-if="tags.length">
      <div class="sb-card-head">
        <span class="sch-label">Tags</span>
      </div>
      <div class="tag-cloud">
        <span v-for="t in tags.slice(0, 14)" :key="t.id" class="tc-pill tc-size-s" @click="$router.push(`/tag/${t.slug}`)">{{ t.name }}</span>
      </div>
    </div>

    <router-link to="/tags" class="sb-link-card">
      <span>Browse all tags</span>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
    </router-link>
  </aside>

  <!-- ═══════════════════════════════
       TAG PAGE MODE
       ═══════════════════════════════ -->
  <aside class="sb" v-else-if="context === 'tag'">
    <!-- Tag Info Card -->
    <div class="sb-card tag-info-card">
      <div class="tic-badge">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
      </div>
      <div class="tic-name">#{{ contextName }}</div>
      <div class="tic-count">{{ articleCount }} articles</div>
    </div>

    <!-- Related Tags -->
    <div class="sb-card" v-if="tags.length">
      <div class="sb-card-head">
        <span class="sch-label">Related Tags</span>
      </div>
      <div class="tag-cloud">
        <span v-for="t in tags.slice(0, 16)" :key="t.id" class="tc-pill tc-size-s" @click="$router.push(`/tag/${t.slug}`)">{{ t.name }}</span>
      </div>
    </div>

    <!-- Recent in this tag -->
    <div class="sb-card" v-if="hotArticles?.length">
      <div class="sb-card-head">
        <span class="sch-label">Recent</span>
      </div>
      <div class="hot-list">
        <div v-for="a in hotArticles.slice(0, 5)" :key="a.id" class="hot-item" @click="$router.push(`/article/${a.id}`)">
          <div class="hot-body" style="padding-left:0">
            <div class="hot-title">{{ a.title }}</div>
            <div class="hot-meta">
              <span class="hot-views">{{ a.publishedAt?.substring(0, 10) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <router-link to="/tags" class="sb-link-card">
      <span>All tags</span>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
    </router-link>
  </aside>

  <!-- ＝＝ Fallback (compact) ＝＝ -->
  <aside class="sb" v-else>
    <div class="sb-card" v-if="tags.length">
      <div class="sb-card-head"><span class="sch-label">Tags</span></div>
      <div class="tag-cloud">
        <span v-for="t in tags.slice(0, 12)" :key="t.id" class="tc-pill tc-size-s" @click="$router.push(`/tag/${t.slug}`)">{{ t.name }}</span>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getTags, getCategories } from '@/api/category'
import { getSiteConfig } from '@/api/site'
import { getArticles } from '@/api/article'

const route = useRoute()

const props = defineProps<{
  hotArticles?: any[]
  context?: 'home' | 'category' | 'tag'
  contextName?: string
  contextSlug?: string
}>()

const siteName = ref('MentalBlog')
const tags = ref<any[]>([])
const categories = ref<any[]>([])
const articleCount = ref(0)
const catCount = ref(0)
const tagCount = ref(0)

function fmtNum(n: number) {
  if (!n) return '0'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return String(n)
}

onMounted(async () => {
  try {
    const [s, t, c] = await Promise.all([getSiteConfig(), getTags(), getCategories()])
    if (s.data?.site_name) siteName.value = s.data.site_name
    tags.value = (t as any).data || []
    categories.value = (c as any).data || []
    tagCount.value = tags.value.length
    catCount.value = categories.value.length

    // Fetch article count for home
    try {
      const ar = await getArticles({ page: 1, pageSize: 1 })
      articleCount.value = (ar as any).total || 0
    } catch (e) { /* ignore */ }
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
/* ═══════════════════════════════
   SIDEBAR — Cyberpunk Design v2
   ═══════════════════════════════ */
.sb {
  display: flex; flex-direction: column; gap: 14px;
  width: 280px; flex-shrink: 0;
}

/* ── Base Card ── */
.sb-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 0;
  overflow: hidden;
  transition: border-color 0.25s;
}
.sb-card:hover { border-color: rgba(0,240,255,0.1); }

/* Card header */
.sb-card-head {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 16px 10px;
}
.sch-icon {
  width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;
  border-radius: 6px;
}
.sch-icon-fire { background: rgba(245,158,11,0.12); color: var(--cyber-amber); }
.sch-icon-tag { background: rgba(0,240,255,0.1); color: var(--cyber-cyan); }
.sch-label {
  font-size: 12px; font-weight: 700; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.6px;
}

/* ═══ AUTHOR CARD ═══ */
.author-card {
  border: 1px solid rgba(0,240,255,0.08);
}
.ac-cover {
  height: 56px;
  background: linear-gradient(135deg, rgba(0,240,255,0.08) 0%, rgba(124,58,237,0.08) 50%, rgba(236,72,153,0.04) 100%);
  position: relative;
}
.ac-cover::after {
  content: '';
  position: absolute; bottom: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0,240,255,0.2), transparent);
}
.ac-body {
  padding: 0 18px 18px; text-align: center; position: relative;
}
.ac-avatar {
  display: inline-flex; align-items: center; justify-content: center;
  width: 52px; height: 52px; border-radius: 50%;
  background: linear-gradient(135deg, var(--cyber-cyan), var(--cyber-purple));
  color: #fff; font-size: 22px; font-weight: 800;
  margin-top: -26px; border: 3px solid var(--bg-elevated);
  box-shadow: 0 0 20px rgba(0,240,255,0.15);
  position: relative; z-index: 1;
  font-family: var(--font-mono);
}
.ac-name {
  margin-top: 10px; font-size: 16px; font-weight: 800; color: #fff;
}
.ac-role {
  display: inline-flex; align-items: center; gap: 5px;
  margin-top: 3px; font-size: 11px; color: var(--text-tertiary);
  font-family: var(--font-mono); letter-spacing: 0.5px;
}
.ac-role-dot {
  width: 5px; height: 5px; border-radius: 50%;
  background: var(--cyber-cyan);
  box-shadow: 0 0 6px var(--cyber-cyan-glow);
  animation: roleDotPulse 2s ease-in-out infinite;
}
@keyframes roleDotPulse {
  0%,100% { opacity: 1; } 50% { opacity: 0.3; }
}
.ac-bio {
  margin-top: 8px; font-size: 12px; color: var(--text-tertiary); line-height: 1.5;
}

/* Stats row */
.ac-stats-row {
  display: flex; justify-content: center; gap: 20px;
  margin-top: 14px; padding-top: 14px;
  border-top: 1px solid var(--border-subtle);
}
.ac-stat {
  display: flex; flex-direction: column; align-items: center; gap: 2px;
}
.acs-num {
  font-size: 16px; font-weight: 800; font-family: var(--font-mono);
  color: var(--cyber-cyan);
}
.acs-lbl {
  font-size: 10px; font-weight: 500; color: var(--text-tertiary);
  text-transform: uppercase; letter-spacing: 0.5px;
}

/* Social links */
.ac-socials {
  display: flex; justify-content: center; gap: 6px;
  margin-top: 14px; padding-top: 14px;
  border-top: 1px solid var(--border-subtle);
}
.acs-link {
  width: 34px; height: 34px; display: flex; align-items: center; justify-content: center;
  border-radius: 8px; color: var(--text-tertiary);
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border-subtle);
  transition: all 0.2s;
}
.acs-link:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.06); }

/* ═══ HOT LIST ═══ */
.hot-list { padding: 0 12px 14px; display: flex; flex-direction: column; gap: 2px; }
.hot-item {
  display: flex; align-items: flex-start; gap: 10px; cursor: pointer;
  padding: 8px 8px; border-radius: 8px;
  transition: all 0.2s;
}
.hot-item:hover { background: rgba(0,240,255,0.04); }
.hot-rank {
  width: 24px; height: 24px; border-radius: 6px; display: flex;
  align-items: center; justify-content: center;
  font-size: 11px; font-weight: 800; flex-shrink: 0;
  font-family: var(--font-mono); color: var(--text-tertiary);
  background: rgba(255,255,255,0.03);
}
.hot-rank.hot-top {
  background: linear-gradient(135deg, var(--cyber-amber), #D97706);
  color: #000;
}
.hot-body { flex: 1; min-width: 0; }
.hot-title {
  font-size: 12px; font-weight: 500; line-height: 1.4;
  overflow: hidden; max-height: 2.7em; line-height: 1.35;
  overflow: hidden; color: var(--text-secondary);
  transition: color 0.15s;
}
.hot-item:hover .hot-title { color: var(--text-primary); }
.hot-meta { margin-top: 3px; }
.hot-views {
  display: inline-flex; align-items: center; gap: 3px;
  font-size: 10px; color: var(--text-tertiary); font-family: var(--font-mono);
}

/* ═══ TAG CLOUD ═══ */
.tag-cloud { display: flex; flex-wrap: wrap; gap: 5px; padding: 0 14px 14px; }
.tc-pill {
  display: inline-flex; padding: 3px 11px; font-size: 11px; font-weight: 500;
  color: var(--text-secondary); background: rgba(255,255,255,0.03);
  border: 1px solid var(--border-subtle); border-radius: 6px;
  cursor: pointer; transition: all 0.2s;
}
.tc-pill:hover { color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2); background: rgba(0,240,255,0.05); }
.tc-size-l { font-size: 13px; font-weight: 600; padding: 4px 13px; color: var(--text-primary); }
.tc-size-m { font-size: 12px; padding: 3px 11px; }
.tc-size-s { font-size: 10px; padding: 2px 9px; }

/* ═══ LINK CARD ═══ */
.sb-link-card {
  display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 11px; font-size: 12px; font-weight: 500;
  color: var(--text-tertiary); text-decoration: none;
  border: 1px dashed var(--border-subtle); border-radius: 10px;
  transition: all 0.2s;
}
.sb-link-card:hover {
  color: var(--cyber-cyan); border-color: rgba(0,240,255,0.2);
  background: rgba(0,240,255,0.03);
}

/* ═══ CATEGORY INFO CARD ═══ */
.cat-info-card {
  padding: 22px 18px; text-align: center;
  background: linear-gradient(180deg, var(--bg-elevated) 0%, rgba(0,240,255,0.02) 100%);
}
.cic-icon { color: var(--cyber-cyan); opacity: 0.7; margin-bottom: 8px; }
.cic-name { font-size: 18px; font-weight: 800; color: #fff; }
.cic-count {
  margin-top: 4px; font-size: 12px; color: var(--text-tertiary);
  font-family: var(--font-mono);
}

/* ═══ TAG INFO CARD ═══ */
.tag-info-card {
  padding: 22px 18px; text-align: center;
  background: linear-gradient(180deg, var(--bg-elevated) 0%, rgba(124,58,237,0.03) 100%);
}
.tic-badge { color: var(--cyber-purple); opacity: 0.7; margin-bottom: 8px; }
.tic-name { font-size: 20px; font-weight: 800; color: var(--cyber-cyan); font-family: var(--font-mono); }
.tic-count {
  margin-top: 4px; font-size: 12px; color: var(--text-tertiary);
  font-family: var(--font-mono);
}

/* ═══ CATEGORY NAV LIST ═══ */
.cat-nav-list { padding: 0 10px 10px; display: flex; flex-direction: column; gap: 1px; }
.cn-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px 10px; font-size: 13px; color: var(--text-secondary);
  cursor: pointer; border-radius: 7px; transition: all 0.15s;
}
.cn-item:hover { color: var(--text-primary); background: rgba(255,255,255,0.03); }
.cn-active {
  color: var(--cyber-cyan) !important;
  background: rgba(0,240,255,0.06) !important;
  font-weight: 600;
}
.cn-count {
  font-size: 11px; color: var(--text-tertiary); font-family: var(--font-mono);
  background: rgba(255,255,255,0.03); padding: 1px 7px; border-radius: 10px;
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 900px) {
  .sb { width: 100%; }
}
</style>
