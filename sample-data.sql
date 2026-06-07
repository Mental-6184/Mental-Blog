-- =====================================================
-- MentalBlog 示例数据
-- 包含分类、标签、示例文章
-- 在运行 init.sql 之后执行
-- =====================================================

USE `mentalblog`;

-- ==================== 分类 ====================
INSERT IGNORE INTO `category` (`name`, `slug`, `description`, `sort`) VALUES
('技术', 'tech', '编程、框架、工具等技术相关文章', 1),
('生活', 'life', '日常记录、思考感悟', 2),
('项目心得', 'project', '项目开发经验与总结', 3),
('读书笔记', 'reading', '阅读记录与书评', 4);

-- ==================== 标签 ====================
INSERT IGNORE INTO `tag` (`name`, `slug`) VALUES
('Java', 'java'),
('Vue', 'vue'),
('Spring Boot', 'spring-boot'),
('MySQL', 'mysql'),
('前端', 'frontend'),
('后端', 'backend'),
('生活感悟', 'life-thoughts'),
('读书', 'reading'),
('架构设计', 'architecture'),
('工具推荐', 'tools');

-- ==================== 文章 ====================

-- 注意：以下文章假设 admin 用户的 ID 为 1
-- 如果 admin ID 不是 1，请先执行：SELECT id FROM user WHERE username = 'admin';

-- 文章 1: 技术 - Spring Boot
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'Spring Boot 4.0 新特性解读',
'Spring Boot 4.0 带来了哪些重大更新？本文将详细解读新版本的核心变化。',
'# Spring Boot 4.0 新特性解读

Spring Boot 4.0 于近期正式发布，这是自 3.0 以来最大的一次版本更新。本文将带你快速了解最重要的新特性。

## 1. 全新的 Jakarta EE 支持

Spring Boot 4.0 全面拥抱 Jakarta EE 11，移除了所有对旧版 Java EE 的兼容代码。

```java
// 新的 Jakarta 命名空间
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
```

## 2. 更快的启动速度

得益于新的启动时 AOT 编译支持，Spring Boot 4.0 的启动速度相比 3.x 提升了约 40%。

## 3. 简化的配置模型

```yaml
spring:
  application:
    name: my-app
  datasource:
    url: jdbc:mysql://localhost:3306/db
```

## 4. 虚拟线程支持

正式支持 JDK 21 虚拟线程，可以极大地简化高并发编程：

```java
@Bean
public TomcatProtocolHandlerCustomizer<?> protocolHandler() {
    return handler -> handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
}
```

## 5. 总结

Spring Boot 4.0 是一次值得升级的重大版本更新，尤其对于新项目来说，能直接享受到最新的 Jakarta EE 规范和性能优化。

> 参考：Spring Boot 4.0 Release Notes',
'spring-boot-4-features', 1, 1, 128, 12, 3, 1, 1, '2026-05-28 10:00:00', '2026-05-28 10:00:00');

-- 文章 2: 技术 - Vue 3 + TypeScript
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'Vue 3 + TypeScript 实战：从零搭建博客前台',
'本文记录了我用 Vue 3 + TypeScript + Vite 搭建博客前端的过程与心得。',
'# Vue 3 + TypeScript 实战

## 为什么选择 Vue 3

Vue 3 的 Composition API 让逻辑复用变得前所未有的简单，配合 TypeScript 的类型推导，开发体验极佳。

## 项目搭建

使用 Vite 创建项目：

```bash
npm create vite@latest my-project -- --template vue-ts
```

## 关键实践

### 1. Composition API

```vue
<script setup lang="ts">
import { ref, onMounted } from ''vue''

const count = ref(0)

onMounted(() => {
  console.log('Component mounted')
})
</script>
```

### 2. Pinia 状态管理

```typescript
import { defineStore } from ''pinia''

export const useUserStore = defineStore(''user'', () => {
  const token = ref('''')
  const login = (t: string) => { token.value = t }
  return { token, login }
})
```

### 3. 路由守卫

```typescript
router.beforeEach((to, _, next) => {
  if (to.meta.requiresAuth && !token) {
    next(''/login'')
    return
  }
  next()
})
```

## 总结

Vue 3 + TypeScript 的组合非常适合中大型前端项目，类型安全让代码更健壮。',
'vue3-typescript-practice', 1, 0, 95, 8, 2, 1, 1, '2026-05-25 14:30:00', '2026-05-25 14:30:00');

-- 文章 3: 技术 - MySQL 优化
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'MySQL 索引优化实战：从慢查询到毫秒响应',
'一次真实的数据库性能优化经历，通过索引优化将接口响应时间从 3 秒降到 30 毫秒。',
'# MySQL 索引优化实战

## 问题背景

最近博客的首页加载越来越慢，接口响应时间达到了 3 秒多。

## 定位问题

使用 MySQL 慢查询日志定位到了问题 SQL：

```sql
-- 耗时 2.8 秒
SELECT * FROM article
WHERE status = 1
ORDER BY created_at DESC
LIMIT 10;
```

## 分析执行计划

```sql
EXPLAIN SELECT * FROM article WHERE status = 1 ORDER BY created_at DESC LIMIT 10;
```

发现是 **全表扫描**，没有用到索引。

## 优化方案

创建复合索引：

```sql
ALTER TABLE article ADD INDEX idx_status_created (status, created_at DESC);
```

### 索引设计原则

1. **最左前缀原则**：查询条件从索引最左列开始匹配
2. **选择性高的列在前**：区分度越高的列越靠左
3. **覆盖索引**：尽量让索引包含查询所需的所有列

## 优化结果

```
优化前: 2.8s → 优化后: 28ms
性能提升: 100倍
```

## 总结

索引优化是数据库性能调优最直接有效的手段。一个合适的索引往往能带来数量级的性能提升。

| 类型 | 优化前 | 优化后 |
|------|--------|--------|
| 响应时间 | 2.8s | 28ms |
| 扫描行数 | 50000+ | 10 |
| Extra | Using filesort | Using index |',
'mysql-index-optimization', 1, 0, 156, 15, 4, 1, 1, '2026-05-22 09:15:00', '2026-05-22 09:15:00');

-- 文章 4: 生活
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'写博客这一年：我的收获与成长',
'坚持写博客一年了，聊聊这一路上的感悟与变化。',
'# 写博客这一年

转眼间，这个博客已经运行一年了。回头看，收获远超预期。

## 为什么要写博客

很多人问过我为什么写博客。其实最开始的原因很简单：

> 把学到的东西记下来，免得忘记。

但坚持下来之后，我发现收获远不止于此。

## 最大的三个收获

### 1. 知识体系化

写文章迫使你把零散的知识点串联起来。为了写清楚一个主题，你需要：

- 查阅大量资料
- 理解知识点之间的关联
- 用自己的语言重新表达

这个过程本身就是最好的学习。

### 2. 结识志同道合的朋友

通过博客认识了很多有趣的人，有读者、有同行、有前辈。他们的反馈和建议让我不断进步。

### 3. 记录成长的轨迹

翻看一年前的文章，能清晰地看到自己的成长。有些当时觉得很难的技术，现在看起来很简单——这种对比本身就是最好的鼓励。

## 关于坚持

说实话，有时候也会偷懒。但养成习惯的关键是：

1. **降低门槛** - 不要求每篇都是长篇大论
2. **固定时间** - 每周固定一个时间写作
3. **不求完美** - 先写出来，再慢慢完善

## 2026 年的计划

- 每月至少 4 篇文章
- 尝试写一些系列教程
- 优化博客的阅读体验

> 种一棵树最好的时间是十年前，其次是现在。',
'blogging-one-year', 1, 1, 210, 25, 7, 2, 1, '2026-05-20 18:00:00', '2026-05-20 18:00:00');

-- 文章 5: 项目心得
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'从零开发个人博客：前后端分离架构实践',
'记录从技术选型到部署上线的全流程，分享架构设计中的思考与取舍。',
'# 从零开发个人博客

## 项目背景

作为一个开发者，拥有一个属于自己的博客是一件很有意义的事情。市面上虽然有现成的博客平台，但自己动手造轮子才能学到最多。

## 技术选型

### 后端

| 技术 | 选型理由 |
|------|---------|
| Spring Boot 4.0 | 生态成熟，开发效率高 |
| MyBatis-Plus | 灵活且强大的 ORM 框架 |
| MySQL 8 | 稳定可靠，社区支持好 |
| Redis | 缓存加速，提升性能 |

### 前端

| 技术 | 选型理由 |
|------|---------|
| Vue 3 | Composition API，灵活组合 |
| TypeScript | 类型安全，更好的开发体验 |
| Vite | 极速热更新，开发体验极佳 |
| Element Plus | 组件丰富，适合管理后台 |

## 架构设计

```
用户 → Nginx → 前端静态资源
            ↘  API 代理 → Spring Boot → MySQL
                                     ↘ Redis
```

### 为什么选择前后端分离

1. **职责清晰** - 前后端各自独立开发部署
2. **性能优化** - 静态资源 CDN 加速
3. **扩展性好** - 容易接入小程序或移动端

## 数据库设计

博客的核心表设计：

- `user` - 用户与权限
- `article` - 文章内容
- `category` / `tag` - 分类与标签
- `comment` - 评论（支持嵌套）
- `favorite` / `like_record` - 互动数据

## 部署

使用 Docker 部署，通过 docker-compose 管理所有服务：

```yaml
version: ''3''
services:
  mysql:
    image: mysql:8
  redis:
    image: redis:7
  app:
    build: .
    ports: - "8080:8080"
  nginx:
    image: nginx:alpine
    ports: - "80:80" - "443:443"
```

## 总结

从零开发一个完整的项目，能让你对技术有更深的理解。过程中遇到的每个问题都是学习的机会。',
'build-personal-blog', 1, 0, 178, 20, 5, 3, 1, '2026-05-15 16:00:00', '2026-05-15 16:00:00');

-- 文章 6: 技术 - 前端
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'CSS 布局进阶：Flexbox 与 Grid 完全指南',
'熟练掌握 Flexbox 和 Grid 布局，轻松应对各种页面布局需求。',
'# CSS 布局进阶指南

## Flexbox 核心概念

Flexbox 是一维布局模型，适合处理**单行或单列**的布局。

### 容器属性

```css
.container {
  display: flex;
  justify-content: center;  /* 主轴对齐 */
  align-items: center;      /* 交叉轴对齐 */
  flex-wrap: wrap;          /* 换行 */
  gap: 16px;               /* 间距 */
}
```

### 项目属性

```css
.item {
  flex: 1;              /* 等分剩余空间 */
  align-self: center;   /* 单独对齐 */
  order: 1;            /* 排列顺序 */
}
```

## Grid 核心概念

Grid 是二维布局模型，适合同时处理**行和列**的布局。

### 基础用法

```css
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
```

### 响应式布局

```css
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
```

## 实战：博客卡片布局

```css
.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.blog-card {
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
}
```

## 推荐工具

可以多去 CSS Grid Garden 和 Flexbox Froggy 练习，非常有趣。',
'css-flexbox-grid-guide', 1, 0, 87, 6, 1, 1, 1, '2026-05-12 11:00:00', '2026-05-12 11:00:00');

-- 文章 7: 读书笔记
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'《代码整洁之道》读书笔记：写出可读性更好的代码',
'总结《Clean Code》中对我影响最大的几个原则和最佳实践。',
'# 《代码整洁之道》读书笔记

## 为什么要读这本书

写代码两年多后，我发现自己更多的时间花在**阅读代码**而不是**写代码**上。这时候才真正理解到：**代码是写给**人**看的**。

## 最有收获的原则

### 1. 有意义的命名

一个好的命名胜过一行注释：

```java
// 不好
int d; // 过去的天数

// 好
int daysSinceLastUpdate;
```

### 2. 函数应该短小

一个函数只做一件事：

```java
// 不好
void processUserAndSendEmail() { ... }

// 好
void processUser() { ... }
void sendEmail() { ... }
```

### 3. 不要重复自己

DRY 原则是重构的核心驱动力。

## 实际应用

读完这本书后，我对自己之前的代码做了重构：

- 函数平均长度从 **50行** 降到了 **15行**
- 注释减少了 **60%**
- Bug 修复时间缩短了 **40%**

## 推荐程度

⭐⭐⭐⭐⭐ 五星推荐，每个程序员都应该读。',
'clean-code-notes', 1, 0, 65, 7, 1, 4, 1, '2026-05-08 20:00:00', '2026-05-08 20:00:00');

-- 文章 8: 技术 - 工具推荐
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'我的开发工具清单：2026 年效率提升指南',
'分享我日常开发中离不开的工具，涵盖编辑器、终端、API 调试等。',
'# 2026 开发工具清单

## 编辑器

### VS Code — 主力编辑器

必装插件：

- GitHub Copilot · AI 编程助手
- Error Lens · 内联错误提示
- GitLens · Git 增强
- Prettier · 代码格式化

### IntelliJ IDEA — Java 开发必备

- 内置 Spring Boot 支持
- 强大的重构能力
- 数据库工具

## 终端

### Warp — 现代化终端

- AI 命令建议
- 智能补全
- 分块输出

## API 调试

### Bruno — 开源 API 客户端

- 本地存储
- Git 友好
- 轻量快速

## 效率工具

| 工具 | 用途 | 推荐理由 |
|------|------|---------|
| Raycast | 快捷启动 | 代替 Spotlight |
| Arc | 浏览器 | 侧边标签栏 |
| Obsidian | 笔记 | 本地 Markdown |

## 写在最后

工具的目的是提高效率，不要为了折腾工具而折腾工具。找到一个顺手的工作流比使用最新潮的工具更重要。',
'dev-tools-2026', 1, 0, 142, 18, 3, 1, 1, '2026-05-05 15:30:00', '2026-05-05 15:30:00');

-- ==================== 文章-标签关联 ====================
-- 文章 1: Spring Boot 4.0 → Java, Spring Boot, 后端
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, (SELECT id FROM tag WHERE slug = 'java')),
(1, (SELECT id FROM tag WHERE slug = 'spring-boot')),
(1, (SELECT id FROM tag WHERE slug = 'backend'));

-- 文章 2: Vue 3 + TS → Vue, 前端
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(2, (SELECT id FROM tag WHERE slug = 'vue')),
(2, (SELECT id FROM tag WHERE slug = 'frontend'));

-- 文章 3: MySQL 优化 → MySQL, 后端, 架构设计
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(3, (SELECT id FROM tag WHERE slug = 'mysql')),
(3, (SELECT id FROM tag WHERE slug = 'backend')),
(3, (SELECT id FROM tag WHERE slug = 'architecture'));

-- 文章 4: 写博客一年 → 生活感悟
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(4, (SELECT id FROM tag WHERE slug = 'life-thoughts'));

-- 文章 5: 博客架构 → Spring Boot, Vue, 架构设计, 后端, 前端
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(5, (SELECT id FROM tag WHERE slug = 'spring-boot')),
(5, (SELECT id FROM tag WHERE slug = 'vue')),
(5, (SELECT id FROM tag WHERE slug = 'architecture')),
(5, (SELECT id FROM tag WHERE slug = 'backend')),
(5, (SELECT id FROM tag WHERE slug = 'frontend'));

-- 文章 6: CSS 布局 → 前端
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(6, (SELECT id FROM tag WHERE slug = 'frontend'));

-- 文章 7: 代码整洁之道 → 读书, 架构设计
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(7, (SELECT id FROM tag WHERE slug = 'reading')),
(7, (SELECT id FROM tag WHERE slug = 'architecture'));

-- 文章 8: 开发工具 → 工具推荐, 前端, 后端
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
(8, (SELECT id FROM tag WHERE slug = 'tools')),
(8, (SELECT id FROM tag WHERE slug = 'frontend')),
(8, (SELECT id FROM tag WHERE slug = 'backend'));
