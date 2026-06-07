# MentalBlog 个人博客系统

一个功能完整的个人博客系统，采用前后端分离架构。支持文章管理、分类标签、评论互动、图片图库、用户权限管理等完整博客功能。

## 项目概览

```text
┌──────────────┐        REST API         ┌──────────────┐
│  MentalBlog-Q │  ───────────────────▶  │  MentalBlog-H │
│  Vue 3 SPA    │  ◀───────────────────  │ Spring Boot   │
└──────────────┘                         └──────┬───────┘
                                                │
                                        ┌───────┴────────┐
                                        │  MySQL / Redis  │
                                        └─────────────────┘
```

## 技术栈

**后端**
- Spring Boot 4.0 + MyBatis-Plus
- Spring Security + JWT 认证
- MySQL 8 + Redis
- Knife4j API 文档
- Rome RSS 订阅

**前端**
- Vue 3 + Composition API + TypeScript
- Vite 8 构建工具
- Vue Router + Pinia
- Element Plus UI 组件库
- Axios HTTP 请求
- Vditor Markdown 编辑器
- ECharts 数据可视化

## 接口文档

后端启动后可访问：

- Knife4j：`http://localhost:8080/doc.html`
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 功能特性

### 前台功能
- 文章列表展示（分类/标签筛选、分页）
- 文章详情（Markdown 渲染、目录导航 TOC、阅读进度条）
- 分类与标签浏览
- 时间轴归档
- 全文搜索
- 评论系统（嵌套回复）
- 点赞/收藏互动
- 文章阅读量统计
- RSS 订阅
- 个人中心
- 图库系统（上传/浏览/标签筛选/下载/评论）
- 深色模式

### 后台管理
- 仪表盘（数据统计、趋势图表、最近动态）
- 文章管理（CRUD、Markdown 编辑器）
- 分类/标签管理
- 评论审核管理
- 用户管理
- 图库管理
- 友链管理
- 系统设置
- 个人资料编辑
- 关于我管理（技能栈、项目、里程碑）
- 图库权限审批

### 设计特色
- Indigo/Cyan 蓝白科技配色
- Glassmorphism 毛玻璃效果
- 完整深色模式（跟随系统 + 手动切换）
- 响应式布局
- 页面过渡动画
- 阅读进度条 + 返回顶部

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8+
- Redis（可选）

### 1. 克隆项目

```bash
git clone https://github.com/czh666810/MentalBlog.git
cd MentalBlog
```

### 2. 数据库初始化

执行初始化脚本（会自动创建数据库 `mentalblog` 并建表）：

```bash
mysql -u root -p < init.sql
```

更多 SQL/脚本说明见 [database/README.md](database/README.md)。

### 3. 配置后端

后端主要配置在 `MentalBlog-H/src/main/resources/application.yml`，`application.properties` 目前主要用于上传目录等少量配置。

重点关注：

- MySQL：`spring.datasource.*`
- Redis（可选）：`spring.data.redis.*`
- JWT：`jwt.secret`、`jwt.expiration`
- 上传目录：`app.upload.dir`（或使用 `file.upload.path` 作为兜底）

本地开发建议不要把真实密码/密钥提交到仓库，可使用环境变量或本地配置覆盖。

示例（仅展示关键项）：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/mentalblog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis 配置（可选）
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT 配置
jwt.secret=your-secret-key-change-in-production
jwt.expiration=86400000

# 文件上传
app.upload.dir=E:/Front end separation project/MentalBlog/uploads/
```

### 4. 启动后端

```bash
cd MentalBlog-H
mvn clean package -DskipTests
java -jar target/MentalBlog-H-0.0.1-SNAPSHOT.jar
```

后端默认运行在 `http://localhost:8080`

### 5. 配置并启动前端

```bash
cd MentalBlog-Q
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`

### 6. 访问

- 前台首页：`http://localhost:5173`
- 后台管理：`http://localhost:5173/admin`
- 默认管理员账号：`admin / admin123`

### 7. 导入示例数据（可选）

```bash
mysql -u root -p mentalblog < seed_data.sql
```

## 数据库表概览

核心表（以 [init.sql](init.sql) 为准）：

- 用户与权限：`user`（含 `image_permission` 字段）、`image_apply`
- 内容：`article`、`category`、`tag`、`article_tag`
- 互动：`comment`、`favorite`、`like_record`
- 站点与友链：`site_config`、`friend_link`
- 图库：`image`、`image_tag`、`image_tag_rel`、`image_comment`
- 关于我：`skill`、`project`、`milestone`

## 项目结构

```
MentalBlog/
├── MentalBlog-H/          # 后端 - Spring Boot 项目
│   ├── src/main/java/
│   │   └── org/example/mentalblogh/
│   │       ├── config/          # 安全/Web/MVC 配置
│   │       ├── controller/      # REST API 控制器
│   │       │   └── admin/       # 管理端 API
│   │       ├── common/          # 公共工具类
│   │       ├── entity/          # 数据实体
│   │       ├── mapper/          # MyBatis-Plus 映射
│   │       ├── service/         # 业务逻辑
│   │       └── interceptor/     # JWT 认证过滤器
│   ├── src/main/resources/
│   └── pom.xml
├── MentalBlog-Q/          # 前端 - Vue 3 项目
│   ├── src/
│   │   ├── api/            # API 接口
│   │   ├── layouts/        # 布局组件
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia 状态管理
│   │   ├── composables/    # 组合式函数
│   │   └── styles/         # 全局样式
│   ├── package.json
│   └── vite.config.ts
├── database/              # SQL 使用说明
├── init.sql               # 推荐：完整建库建表脚本
└── uploads/               # 上传文件目录
```

## 权限说明

- **访客**：仅可访问首页
- **注册用户**：评论、点赞、收藏、图库（需申请权限）
- **管理员**：全部后台管理权限
- **图片权限**：用户需向管理员申请，审批通过后才能使用图库功能

## 常见问题

- 前端请求 404：确认前端请求前缀为 `/api`，并检查 [vite.config.ts](MentalBlog-Q/vite.config.ts) 是否正确代理到 `http://localhost:8080`
- 图片无法访问：确认后端上传目录配置 `app.upload.dir`（或 `file.upload.path`）指向真实存在的目录，且后端暴露了 `/uploads/**`
- 登录后仍 401：确认请求头携带 `Authorization: Bearer <token>`，以及后端 JWT 配置与时间同步正常

## 构建与部署

### 后端

```bash
cd MentalBlog-H
mvn clean package -DskipTests
java -jar target/MentalBlog-H-0.0.1-SNAPSHOT.jar
```

建议通过环境变量覆盖数据库账号密码、JWT 密钥、上传路径等配置（避免将真实配置写入仓库）。

### 前端

```bash
cd MentalBlog-Q
npm install
npm run build
```

构建产物在 `MentalBlog-Q/dist`，可交给 Nginx/Apache 或任意静态资源服务器托管。生产环境下通常需要把 `/api`、`/uploads` 反向代理到后端服务。

## 许可证

MIT License
