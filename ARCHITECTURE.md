# MentalBlog 完整架构与功能规划

> 综合型个人博客 | Spring Boot + Vue 3 | MySQL | 前后端分离 | 服务器部署

---

## 一、角色与权限体系

### 1.1 用户角色

| 角色 | 身份 | 说明 |
|------|------|------|
| **管理员 (ADMIN)** | 博客主人 | 拥有后台全部权限，管理文章/分类/评论/用户/系统设置 |
| **注册用户 (USER)** | 访客（登录后） | 发表评论、点赞、收藏文章、个人中心 |
| **游客 (GUEST)** | 未登录访问者 | 仅可浏览公开内容 |

### 1.2 权限矩阵

| 功能模块 | 管理员 | 注册用户 | 游客 |
|---------|-------|---------|------|
| 浏览文章 | ✅ | ✅ | ✅ |
| 查看文章详情 | ✅ | ✅ | ✅ |
| 搜索文章 | ✅ | ✅ | ✅ |
| 发表评论 | ✅ | ✅ | ❌ |
| 删除自己的评论 | ✅ | ✅ | ❌ |
| 点赞文章/评论 | ✅ | ✅ | ❌ |
| 收藏文章 | ✅ | ✅ | ❌ |
| 发布文章 | ✅ | ❌ | ❌ |
| 编辑/删除文章 | ✅ | ❌ | ❌ |
| 管理分类/标签 | ✅ | ❌ | ❌ |
| 管理所有评论 | ✅ | ❌ | ❌ |
| 管理注册用户 | ✅ | ❌ | ❌ |
| 后台仪表盘 | ✅ | ❌ | ❌ |
| 个人中心 | ✅ | ✅ | ❌ |
| 注册/登录 | ✅ | ✅ | ✅（注册后登录） |

---

## 二、功能模块全景

### 2.1 前台（面向所有访客）

| 模块 | 功能点 |
|------|--------|
| **首页** | 展示最新/热门文章列表，轮播推荐，分类快捷入口 |
| **文章列表** | 分页展示，可按分类/标签/日期筛选 |
| **文章详情** | Markdown 渲染，目录导航，上/下篇文章 |
| **分类浏览** | 按分类查看文章 |
| **标签云** | 可视化标签导航，点击跳转对应文章 |
| **搜索** | 全文搜索（标题+摘要+内容），支持关键词高亮 |
| **归档** | 时间轴式归档展示 |
| **关于我** | 博主个人介绍页面（可后台编辑） |
| **友情链接** | 展示友链，可后台管理 |
| **评论系统** | 支持嵌套评论（二级回复），Markdown |
| **点赞/收藏** | 登录后操作 |
| **文章阅读量** | 自动统计 |
| **RSS 订阅** | 提供 RSS/Atom 订阅源 |

### 2.2 后台（仅管理员）

| 模块 | 功能点 |
|------|--------|
| **仪表盘** | 文章数、评论数、用户数、访问量概览，近期动态 |
| **文章管理** | 文章 CRUD，Markdown 编辑器（支持预览），草稿/发布切换，置顶 |
| **分类管理** | 分类 CRUD，层级结构 |
| **标签管理** | 标签 CRUD |
| **评论管理** | 查看/删除评论，审核开关 |
| **用户管理** | 查看注册用户列表，封禁/解封 |
| **个人资料编辑** | 修改管理员信息、头像、简介 |
| **系统设置** | 博客名称、Logo、SEO 设置、关于我页面编辑、友链管理 |
| **媒体管理** | 图片上传与管理（后续可扩展） |

### 2.3 个人中心（注册用户）

| 模块 | 功能点 |
|------|--------|
| **我的信息** | 查看/编辑昵称、头像、个人简介 |
| **我的评论** | 查看自己发表过的评论 |
| **我的收藏** | 查看收藏的文章列表 |
| **修改密码** | 修改登录密码 |

---

## 三、数据库设计

### 3.1 E-R 关系概览

```
User ──1:N──> Article
User ──1:N──> Comment
User ──M:N──> Article (通过 favorite 表)
User ──M:N──> Article (通过 like 表)
Category ──1:N──> Article
Tag ──M:N──> Article (通过 article_tag 关联表)
Article ──1:N──> Comment
```

### 3.2 表结构设计

#### 1. `user` — 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(255) | 加密后密码 |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱（唯一） |
| avatar | VARCHAR(255) | 头像 URL |
| bio | VARCHAR(500) | 个人简介 |
| role | ENUM('ADMIN','USER') | 角色 |
| status | TINYINT | 0-正常, 1-封禁 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

#### 2. `category` — 分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| name | VARCHAR(50) | 分类名（唯一） |
| slug | VARCHAR(50) | URL 友好标识 |
| description | VARCHAR(200) | 描述 |
| sort | INT | 排序值 |
| created_at | DATETIME | |

#### 3. `tag` — 标签表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| name | VARCHAR(50) | 标签名（唯一） |
| slug | VARCHAR(50) | URL 友好标识 |
| created_at | DATETIME | |

#### 4. `article` — 文章表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| title | VARCHAR(200) | 标题 |
| summary | VARCHAR(500) | 摘要 |
| content | LONGTEXT | Markdown 正文 |
| cover_image | VARCHAR(255) | 封面图 |
| slug | VARCHAR(100) | URL 别名（唯一） |
| status | TINYINT | 0-草稿, 1-已发布 |
| top | TINYINT | 0-不置顶, 1-置顶 |
| view_count | INT | 阅读量 |
| like_count | INT | 点赞数 |
| favorite_count | INT | 收藏数 |
| comment_count | INT | 评论数 |
| category_id | BIGINT (FK) | 所属分类 |
| author_id | BIGINT (FK) | 作者 |
| published_at | DATETIME | 发布时间 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

#### 5. `article_tag` — 文章-标签关联表

| 字段 | 类型 |
|------|------|
| article_id | BIGINT (FK) |
| tag_id | BIGINT (FK) |

#### 6. `comment` — 评论表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| content | TEXT | 评论内容 |
| user_id | BIGINT (FK) | 评论者 |
| article_id | BIGINT (FK) | 所属文章 |
| parent_id | BIGINT (FK) | 父评论（支持嵌套） |
| like_count | INT | 点赞数 |
| status | TINYINT | 0-待审核, 1-已通过, 2-已拒绝 |
| created_at | DATETIME | |

#### 7. `favorite` — 收藏表

| 字段 | 类型 |
|------|------|
| id | BIGINT (PK) |
| user_id | BIGINT (FK) |
| article_id | BIGINT (FK) |
| created_at | DATETIME |

#### 8. `like_record` — 点赞记录表

| 字段 | 类型 |
|------|------|
| id | BIGINT (PK) |
| user_id | BIGINT (FK) |
| article_id | BIGINT (FK) | （可为 null，表示对文章点赞） |
| comment_id | BIGINT (FK) | （可为 null，表示对评论点赞） |
| created_at | DATETIME |

#### 9. `friend_link` — 友情链接表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| name | VARCHAR(100) | 站点名称 |
| url | VARCHAR(255) | 链接 |
| logo | VARCHAR(255) | Logo |
| description | VARCHAR(200) | 描述 |
| sort | INT | 排序 |
| status | TINYINT | 0-隐藏, 1-显示 |
| created_at | DATETIME | |

#### 10. `site_config` — 站点配置表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | |
| config_key | VARCHAR(50) | 配置键（唯一） |
| config_value | TEXT | 配置值 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

---

## 四、后端架构设计 (MentalBlog-H)

### 4.1 技术栈

| 技术 | 用途 |
|------|------|
| **Spring Boot 4.0** | 核心框架 |
| **Spring MVC** | RESTful API |
| **Spring Security + JWT** | 认证与授权 |
| **MyBatis-Plus** | ORM |
| **MySQL 8** | 数据库 |
| **Redis** | 缓存（文章缓存、Token 黑名单等） |
| **MinIO / 本地存储** | 图片/文件存储 |
| **Knife4j** | API 文档 |
| **Lombok** | 减少样板代码 |
| **Hutool** | 工具类库 |

### 4.2 项目包结构

```
com.example.mentalblog
├── MentalBlogHApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── WebMvcConfig.java
│   ├── MyBatisPlusConfig.java
│   ├── RedisConfig.java
│   └── SwaggerConfig.java
├── common/
│   ├── result/
│   │   ├── Result.java
│   │   └── PageResult.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   └── ErrorCode.java
│   ├── constant/
│   │   ├── ArticleStatus.java
│   │   └── UserRole.java
│   └── utils/
│       ├── JwtUtil.java
│       └── PasswordUtil.java
├── entity/
├── dto/
│   ├── request/
│   └── response/
├── mapper/
├── service/
├── service/impl/
├── controller/
│   └── admin/
└── interceptor/
```

### 4.3 API 接口概览

#### 公开接口（无需登录）

| 方法 | URL | 说明 |
|------|-----|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/articles` | 文章列表（分页） |
| GET | `/api/articles/{id}` | 文章详情 |
| GET | `/api/articles/slug/{slug}` | 通过 slug 获取文章 |
| GET | `/api/categories` | 分类列表 |
| GET | `/api/tags` | 标签列表 |
| GET | `/api/tags/cloud` | 标签云数据 |
| GET | `/api/comments/article/{articleId}` | 文章评论列表 |
| GET | `/api/archives` | 归档数据 |
| GET | `/api/search?q={keyword}` | 搜索文章 |
| GET | `/api/site/config` | 站点公共配置 |

#### 需登录接口

| 方法 | URL | 说明 |
|------|-----|------|
| PUT | `/api/user/profile` | 修改个人信息 |
| PUT | `/api/user/password` | 修改密码 |
| GET | `/api/user/info` | 获取个人信息 |
| POST | `/api/comments` | 发表评论 |
| DELETE | `/api/comments/{id}` | 删除自己的评论 |
| POST | `/api/favorites` | 收藏/取消收藏文章 |
| GET | `/api/user/favorites` | 我的收藏列表 |
| POST | `/api/likes/article` | 点赞/取消点赞文章 |
| POST | `/api/likes/comment` | 点赞/取消点赞评论 |
| GET | `/api/user/comments` | 我的评论列表 |

#### 管理员接口（需 ADMIN 角色）

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/api/admin/dashboard` | 仪表盘数据 |
| POST | `/api/admin/articles` | 创建文章 |
| PUT | `/api/admin/articles/{id}` | 编辑文章 |
| DELETE | `/api/admin/articles/{id}` | 删除文章 |
| GET | `/api/admin/articles` | 管理端文章列表（含草稿） |
| GET | `/api/admin/articles/{id}` | 编辑时获取文章内容 |
| POST | `/api/admin/categories` | 创建分类 |
| PUT | `/api/admin/categories/{id}` | 编辑分类 |
| DELETE | `/api/admin/categories/{id}` | 删除分类 |
| POST | `/api/admin/tags` | 创建标签 |
| PUT | `/api/admin/tags/{id}` | 编辑标签 |
| DELETE | `/api/admin/tags/{id}` | 删除标签 |
| GET | `/api/admin/comments` | 评论列表（管理端） |
| DELETE | `/api/admin/comments/{id}` | 删除评论 |
| PUT | `/api/admin/comments/{id}/status` | 审核评论 |
| GET | `/api/admin/users` | 用户列表 |
| PUT | `/api/admin/users/{id}/status` | 封禁/解封用户 |
| GET | `/api/admin/friend-links` | 友链列表 |
| POST | `/api/admin/friend-links` | 创建友链 |
| PUT | `/api/admin/friend-links/{id}` | 编辑友链 |
| DELETE | `/api/admin/friend-links/{id}` | 删除友链 |
| GET | `/api/admin/site-configs` | 获取所有配置 |
| PUT | `/api/admin/site-configs` | 更新配置 |
| POST | `/api/admin/upload` | 上传图片/文件 |

---

## 五、前端架构设计 (MentalBlog-Q)

### 5.1 技术栈

| 技术 | 用途 |
|------|------|
| **Vue 3 + Composition API** | 核心框架 |
| **TypeScript** | 类型安全 |
| **Vite 8** | 构建工具 |
| **Vue Router** | 路由管理 |
| **Pinia** | 状态管理 |
| **Axios** | HTTP 请求 |
| **Vditor** | Markdown 编辑器 |
| **Element Plus** | UI 组件库（后台） |
| **Tailwind CSS** | 前台样式 |
| **ECharts** | 数据可视化（仪表盘） |
| **Day.js** | 日期处理 |

### 5.2 路由结构

#### 前台路由

```
/                       → 首页（文章列表）
/article/:id            → 文章详情
/category/:slug         → 分类页
/tag/:slug              → 标签页
/archives               → 归档
/search?q=xxx           → 搜索结果
/about                  → 关于我
/links                  → 友情链接
/login                  → 登录
/register               → 注册
/user                   → 个人中心（需登录）
  /user/profile           → 个人信息
  /user/comments          → 我的评论
  /user/favorites         → 我的收藏
  /user/password          → 修改密码
```

#### 后台路由（需管理员登录）

```
/admin                  → 仪表盘
/admin/articles         → 文章列表
/admin/articles/create  → 写文章
/admin/articles/:id/edit → 编辑文章
/admin/categories       → 分类管理
/admin/tags             → 标签管理
/admin/comments         → 评论管理
/admin/users            → 用户管理
/admin/profile          → 个人资料
/admin/settings         → 系统设置
/admin/links            → 友链管理
```

---

## 六、开发阶段规划

### 第一阶段：基础搭建（骨架期）

| 任务 | 预估工时 |
|------|---------|
| 后端项目搭建，MyBatis-Plus 配置 | 1天 |
| 数据库建表 SQL 编写与初始化 | 1天 |
| 统一返回、异常处理、工具类 | 1天 |
| Spring Security + JWT 认证 | 1.5天 |
| 前端项目搭建，路由配置，Axios 封装 | 1天 |
| Element Plus 引入，布局组件开发 | 1天 |

### 第二阶段：核心功能（前台）

| 任务 | 预估工时 |
|------|---------|
| 文章列表页（分页、分类筛选） | 1天 |
| 文章详情页（Markdown 渲染、目录） | 1.5天 |
| 分类/标签浏览 | 0.5天 |
| 搜索功能 | 1天 |
| 评论系统（发表、嵌套展示） | 1.5天 |
| 归档页 | 0.5天 |
| 登录/注册/个人中心 | 1.5天 |

### 第三阶段：后台管理

| 任务 | 预估工时 |
|------|---------|
| 后台布局（侧边栏、顶栏） | 0.5天 |
| 文章管理（列表、新建编辑、删除） | 2天 |
| 分类/标签管理 | 0.5天 |
| 评论审核管理 | 0.5天 |
| 用户管理 | 0.5天 |
| 系统设置 / 友链管理 | 0.5天 |
| 仪表盘数据可视化 | 1天 |
| Markdown 编辑器集成 | 1天 |

### 第四阶段：增强与打磨

| 任务 | 预估工时 |
|------|---------|
| 图片上传功能 | 1天 |
| 点赞/收藏功能 | 1天 |
| 文章阅读量统计 | 0.5天 |
| RSS 订阅 | 0.5天 |
| 标签云/侧边栏组件 | 0.5天 |
| SEO 优化（meta 标签、sitemap） | 1天 |
| 页面响应式适配 | 1天 |
| 性能优化（Redis 缓存） | 1天 |

### 第五阶段：部署上线

| 任务 | 预估工时 |
|------|---------|
| 服务器环境配置（JDK、MySQL、Redis、Nginx） | 1天 |
| 后端打包部署 | 0.5天 |
| 前端构建部署 | 0.5天 |
| Nginx 反向代理 + HTTPS 配置 | 0.5天 |
| 域名绑定 | 0.5天 |

**预估总工时：约 4-6 周（全职）**

---

## 七、部署架构

```
                           ┌──────────────┐
                           │   域名访问     │
                           │  www.xxx.com  │
                           └──────┬───────┘
                                  │
                                  ▼
                           ┌──────────────┐
                           │    Nginx      │
                           │  (反向代理)    │
                           │  HTTPS + CDN  │
                           └──┬───────┬───┘
                              │       │
                     ┌────────▼──┐ ┌──▼──────────┐
                     │ 前端静态资源 │ │  /api/* 转发  │
                     │ .html/.js  │ │ 后端 API 接口 │
                     │ .css/image │ └──┬──────────┘
                     └────────────┘    │
                                       ▼
                              ┌────────────────┐
                              │  Spring Boot   │
                              │  8080 端口      │
                              └───┬────────┬───┘
                                  │        │
                                  ▼        ▼
                           ┌────────┐ ┌────────┐
                           │ MySQL  │ │ Redis  │
                           └────────┘ └────────┘
```

**推荐服务器**：2核4G Linux 云服务器（Ubuntu 22+），约 ¥50-100/月。

---
