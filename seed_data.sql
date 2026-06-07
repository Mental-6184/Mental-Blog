-- =====================================================
-- MentalBlog 种子数据脚本
-- 包含：分类、标签、文章（带多对多关联）
-- =====================================================

USE `mentalblog`;

-- =====================================================
-- 1. 分类
-- =====================================================
INSERT IGNORE INTO `category` (`id`, `name`, `slug`, `description`, `sort`) VALUES
(1, '技术', 'tech', '编程、框架、工具等技术相关', 1),
(2, '生活', 'life', '日常生活随笔与感悟', 2),
(3, '项目', 'project', '项目开发经验分享', 3),
(4, '读书笔记', 'reading', '读书笔记与书评', 4),
(5, '随笔', 'essay', '随想、观点与杂谈', 5);

-- =====================================================
-- 2. 标签
-- =====================================================
INSERT IGNORE INTO `tag` (`id`, `name`, `slug`) VALUES
(1, 'Java', 'java'),
(2, 'Spring Boot', 'spring-boot'),
(3, 'Vue', 'vue'),
(4, '前端', 'frontend'),
(5, 'MySQL', 'mysql'),
(6, 'Redis', 'redis'),
(7, 'Docker', 'docker'),
(8, '运维', 'devops');

-- =====================================================
-- 3. 文章
-- =====================================================

-- 获取管理员 ID（假设 admin 用户 id = 1）
SET @admin_id = (SELECT `id` FROM `user` WHERE `username` = 'admin' LIMIT 1);

-- ----- 技术分类文章 -----
INSERT IGNORE INTO `article` (`id`, `title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`) VALUES
(1,
 'Spring Boot 3 实战：从零构建 RESTful API',
 '本文详细介绍如何使用 Spring Boot 3 快速搭建一个 RESTful API 项目，涵盖项目初始化、分层架构、异常处理、参数校验等核心环节。',
 '# Spring Boot 3 实战：从零构建 RESTful API\n\n## 前言\n\nSpring Boot 3 带来了许多新特性，包括对 Jakarta EE 的全面支持、AOT 编译以及更好的 GraalVM 集成。本文将以实战方式，带你从零搭建一个完整的 RESTful API 项目。\n\n## 1. 项目初始化\n\n使用 Spring Initializr 创建项目，选择以下依赖：\n\n- Spring Web\n- Spring Data JPA\n- MySQL Driver\n- Validation\n- Lombok\n\n## 2. 项目结构\n\n```\nsrc/main/java/com/example\n├── config/         -- 配置类\n├── controller/     -- 控制器\n├── service/        -- 业务逻辑\n├── repository/     -- 数据访问\n├── entity/         -- 实体\n├── dto/            -- 数据传输对象\n└── common/         -- 公共组件\n```\n\n## 3. 统一返回结构\n\n定义一个通用的 Result 类，规范前后端数据交互格式。\n\n```java\n@Data\npublic class Result<T> {\n    private int code;\n    private String message;\n    private T data;\n    \n    public static <T> Result<T> success(T data) {\n        Result<T> r = new Result<>();\n        r.code = 200;\n        r.message = \"success\";\n        r.data = data;\n        return r;\n    }\n}\n```\n\n## 4. 全局异常处理\n\n使用 `@RestControllerAdvice` 统一处理异常，避免在每个 Controller 中重复编写错误处理逻辑。\n\n## 5. 总结\n\nSpring Boot 3 的开发体验非常流畅，配合 Jakarta EE 的升级，让整个技术栈焕然一新。',
 'spring-boot-restful-api',
 1, 1, 186, 23, 12, 5, 1, @admin_id, '2026-05-15 10:00:00', NOW()),

(2,
 'Vue 3 Composition API 入门指南',
 'Vue 3 的 Composition API 是一种全新的逻辑复用方式。本文通过对比 Options API 和 Composition API，帮助你快速上手。',
 '# Vue 3 Composition API 入门指南\n\n## 为什么需要 Composition API？\n\n在 Vue 2 中，我们使用 Options API（`data`、`methods`、`computed`）组织代码。当组件变得复杂时，同一个功能块的代码分散在不同选项中，维护困难。\n\n## 核心概念\n\n### ref 和 reactive\n\n```vue\n<script setup>\nimport { ref, reactive } from \"vue\"\n\nconst count = ref(0)\nconst user = reactive({ name: \"张三\", age: 25 })\n</script>\n```\n\n### 计算属性\n\n```vue\n<script setup>\nimport { ref, computed } from \"vue\"\n\nconst price = ref(100)\nconst total = computed(() => price.value * 1.2)\n</script>\n```\n\n### 生命周期\n\n`onMounted`、`onUnmounted` 等生命周期钩子需要在 `setup` 中主动调用。\n\n## 逻辑复用\n\n通过自定义 Hook（以 `use` 开头的函数），可以轻松复用状态逻辑。\n\n```ts\n// useCounter.ts\nexport function useCounter() {\n  const count = ref(0)\n  const increment = () => count.value++\n  return { count, increment }\n}\n```\n\n## 总结\n\nComposition API 并不是要取代 Options API，而是提供更灵活的组织方式。建议新项目采用 Composition API，老项目保持 Options API 也不会有问题。',
 'vue3-composition-api',
 1, 0, 142, 18, 8, 3, 1, @admin_id, '2026-05-18 14:00:00', NOW()),

(3,
 'MySQL 索引优化实战',
 '慢查询是数据库最常见的性能问题。本文结合实际案例，分享 MySQL 索引设计和优化的实践经验。',
 '# MySQL 索引优化实战\n\n## 索引的基本原则\n\n### 最左前缀原则\n\n对于联合索引 `(a, b, c)`，以下查询可以使用索引：\n\n- `WHERE a = 1`\n- `WHERE a = 1 AND b = 2`\n- `WHERE a = 1 AND b = 2 AND c = 3`\n\n以下查询**不能**使用索引：\n\n- `WHERE b = 2` （跳过了 a）\n- `WHERE c = 3` （跳过了 a 和 b）\n\n### 覆盖索引\n\n当查询的字段全部在索引中时，MySQL 只需扫描索引而无需回表查询，性能大幅提升。\n\n```sql\n-- 如果 idx_a_b 是 (a, b) 的联合索引\nSELECT a, b FROM t WHERE a = 1;  -- 覆盖索引\nSELECT * FROM t WHERE a = 1;     -- 需要回表\n```\n\n## 实战案例\n\n### 慢查询日志分析\n\n开启慢查询日志：\n```sql\nSET GLOBAL slow_query_log = ON;\nSET GLOBAL long_query_time = 1;\n```\n\n### 使用 EXPLAIN\n\n```sql\nEXPLAIN SELECT * FROM orders WHERE user_id = 123 ORDER BY created_at DESC;\n```\n\n重点关注 `type`、`rows`、`Extra` 字段。\n\n## 总结\n\n- 索引不是越多越好，写操作会维护索引成本\n- 联合索引注意字段顺序\n- 尽量避免 `SELECT *`',
 'mysql-index-optimization',
 1, 0, 98, 12, 6, 2, 1, @admin_id, '2026-05-20 09:00:00', NOW());

-- ----- 项目分类文章 -----
INSERT IGNORE INTO `article` (`id`, `title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`) VALUES
(4,
 '从零搭建个人博客：全栈项目实战',
 '本文记录我个人博客 MentalBlog 的完整开发过程，包括技术选型、架构设计、前后端实现等全流程。',
 '# 从零搭建个人博客：全栈项目实战\n\n## 项目概述\n\nMentalBlog 是一个基于 Spring Boot + Vue 3 的前后端分离博客系统。本文记录整个项目的开发过程。\n\n## 技术选型\n\n### 后端\n- **框架**: Spring Boot 4.0\n- **ORM**: MyBatis-Plus\n- **数据库**: MySQL 8\n- **缓存**: Redis\n- **认证**: JWT\n\n### 前端\n- **框架**: Vue 3 + TypeScript\n- **构建**: Vite\n- **UI**: Element Plus\n- **状态管理**: Pinia\n\n## 架构设计\n\n```\n┌─────────────┐     ┌──────────────┐     ┌──────────┐\n│   Frontend   │────▶│   Backend    │────▶│  MySQL   │\n│  Vue 3 SPA   │     │ Spring Boot  │     │  Redis   │\n└─────────────┘     └──────────────┘     └──────────┘\n```\n\n前后端通过 RESTful API 通信，使用 JWT 进行身份认证。\n\n## 遇到的坑\n\n1. **CORS 跨域**：Spring Security 配置需要注意 CORS 的先后顺序\n2. **MyBatis-Plus 版本兼容**：Spring Boot 4.x 需要使用 `spring-boot4-starter`\n3. **Vite 版本兼容**：Node.js 版本与 Vite 版本需要匹配\n\n## 总结\n\n全栈开发最考验的是统筹能力，前后端的配合、数据流的设计、异常的排查，每一步都需要细心对待。',
 'build-personal-blog-fullstack',
 1, 1, 235, 31, 15, 8, 3, @admin_id, '2026-05-22 10:00:00', NOW()),

(5,
 '使用 Docker 部署 Spring Boot 应用',
 'Docker 让应用部署变得简单可靠。本文分享如何将 Spring Boot 应用打包成 Docker 镜像并部署到服务器。',
 '# 使用 Docker 部署 Spring Boot 应用\n\n## 为什么选择 Docker？\n\n- **环境一致**：本地开发环境与生产环境完全一致\n- **快速部署**：一条命令启动整个应用\n- **资源隔离**：每个容器独立运行\n\n## 编写 Dockerfile\n\n```dockerfile\nFROM eclipse-temurin:17-jre-alpine\nWORKDIR /app\nCOPY target/app.jar app.jar\nEXPOSE 8080\nENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]\n```\n\n## 多阶段构建\n\n```dockerfile\n# Build stage\nFROM maven:3.9-eclipse-temurin-17 AS build\nCOPY . .\nRUN mvn package -DskipTests\n\n# Run stage\nFROM eclipse-temurin:17-jre-alpine\nCOPY --from=build target/app.jar app.jar\nENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]\n```\n\n## Docker Compose\n\n```yaml\nversion: \"3.8\"\nservices:\n  app:\n    build: .\n    ports:\n      - \"8080:8080\"\n    depends_on:\n      - mysql\n      - redis\n  mysql:\n    image: mysql:8\n    environment:\n      MYSQL_ROOT_PASSWORD: root123\n  redis:\n    image: redis:7\n```\n\n## 总结\n\nDocker 已经成为现代应用部署的标准方式，掌握它对于开发者来说是非常必要的。',
 'docker-spring-boot-deploy',
 1, 0, 156, 19, 10, 4, 3, @admin_id, '2026-05-23 11:00:00', NOW());

-- ----- 生活分类文章 -----
INSERT IGNORE INTO `article` (`id`, `title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`) VALUES
(6,
 '程序员如何保持身体健康',
 '长期久坐的编程工作对身体的损耗不可忽视。分享我在工作和生活中积累的健康小习惯。',
 '# 程序员如何保持身体健康\n\n## 颈肩问题\n\n每天面对电脑 8 小时以上，颈肩酸痛几乎是每个程序员的通病。\n\n### 改善方法\n\n1. **调整显示器高度**：让视线平视屏幕上端\n2. **每 45 分钟站起来走走**：设置番茄钟提醒\n3. **靠墙站立**：每天靠墙站立 10 分钟，改善体态\n\n## 眼睛保护\n\n### 20-20-20 法则\n\n> 每工作 20 分钟，看 20 英尺（约 6 米）外的地方，持续 20 秒。\n\n### 其他建议\n\n- 使用护眼模式（f.lux / Windows 夜灯）\n- 多吃富含叶黄素的食物（菠菜、玉米）\n- 定期检查视力\n\n## 运动习惯\n\n不一定非要去健身房，日常的小习惯更重要：\n\n- **步行上下班** 或提前一站下车\n- **午休时间散步** 15 分钟\n- **居家微运动**：俯卧撑、深蹲、平板支撑\n\n## 作息规律\n\n熬夜写代码不如早起写代码。早睡早起不仅能提高代码质量，还能改善整体健康状况。\n\n> 身体是革命的本钱 —— 这句话在程序员这个行业特别真实。',
 'programmer-health-tips',
 1, 0, 210, 28, 14, 7, 2, @admin_id, '2026-05-19 08:00:00', NOW()),

(7,
 '我的 2026 上半年总结',
 '时间过得真快，转眼 2026 年已经过半。简单回顾一下这半年的经历与感悟。',
 '# 我的 2026 上半年总结\n\n## 工作方面\n\n今年最大的变化是全面拥抱了 Spring Boot 4 和新一代的 Jakarta EE 规范。从 Spring Boot 2.x 直接跳到 4.x，中间跳过了 3.x，变化确实不小。\n\n### 主要成果\n\n- 完成了 MentalBlog 博客项目\n- 参与了几个开源项目的 PR\n- 学习了 Vue 3 + TypeScript 的现代前端开发\n\n## 生活方面\n\n上半年最大的成就是养成了规律运动的好习惯。\n\n- 每周跑步 3 次，每次 5 公里\n- 体重减了 8 斤\n- 睡眠质量明显改善\n\n## 阅读清单\n\n- 《深入理解 Java 虚拟机》\n- 《架构整洁之道》\n- 《原子习惯》\n\n## 下半年计划\n\n1. 把博客项目部署上线\n2. 深入学习云原生技术\n3. 坚持运动\n\n> 种一棵树最好的时间是十年前，其次是现在。',
 '2026-mid-year-review',
 1, 0, 89, 8, 5, 2, 2, @admin_id, '2026-06-01 08:00:00', NOW());

-- ----- 读书笔记分类文章 -----
INSERT IGNORE INTO `article` (`id`, `title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`) VALUES
(8,
 '《原子习惯》读书笔记：微小的改变带来巨大的结果',
 'James Clear 的《原子习惯》颠覆了我对习惯养成的认知。本文分享书中的核心观点和实践方法。',
 '# 《原子习惯》读书笔记\n\n## 核心观点\n\n> 习惯是自我提高的复利。\n\n每天进步 1%，一年后你会变成原来的 37 倍。\n\n## 四个法则\n\n### 1. 让它显而易见\n\n- 明确意图：\"我将在 [时间] 于 [地点] 做 [行为]\"\n- 环境设计：把想养成的习惯放在显眼位置\n\n### 2. 让它有吸引力\n\n- 习惯捆绑：把想养成的习惯和已有的愉悦习惯绑定\n- 加入文化：融入有相同习惯的群体\n\n### 3. 让它简便易行\n\n- 两分钟法则：新习惯从两分钟版本开始\n- 减少阻力：为想做之事减少步骤，为不想做之事增加步骤\n\n### 4. 让它令人愉悦\n\n- 即时奖励：完成习惯后给自己一个小奖励\n- 习惯追踪：用日历打勾的方式记录进度\n\n## 我的实践\n\n自从读了这本书，我开始用日历打勾的方式追踪每天的编程学习和运动，效果非常显著。目前已经连续保持了 60 天。\n\n## 推荐理由\n\n这本书不是空谈理论，而是提供了大量可操作的方法。如果你想改变自己但不知从何开始，强烈推荐。',
 'atomic-habits-reading-notes',
 1, 0, 167, 20, 11, 6, 4, @admin_id, '2026-05-25 09:00:00', NOW()),

(9,
 '《重构：改善既有代码的设计》读书笔记',
 'Martin Fowler 的经典之作，这本书教会我如何让代码变得更干净、更易维护。',
 '# 《重构》读书笔记\n\n## 什么是重构\n\n> 重构（Refactoring）是指在不改变软件可观察行为的前提下，改善其内部结构的过程。\n\n## 何时重构\n\n- **三次法则**：同一段代码看到第三次时，就是重构的时机\n- **添加功能时**：先重构让新功能容易添加\n- **修复 Bug 时**：让代码更清晰，Bug 自然会暴露\n\n## 常见的重构手法\n\n### 提炼函数（Extract Function）\n\n```javascript\n// before\nfunction printOwing(invoice) {\n  printBanner()\n  let outstanding = calculateOutstanding(invoice)\n  console.log(`name: ${invoice.customer}`)\n  console.log(`amount: ${outstanding}`)\n}\n\n// after\nfunction printOwing(invoice) {\n  printBanner()\n  let outstanding = calculateOutstanding(invoice)\n  printDetails(invoice, outstanding)\n}\n\nfunction printDetails(invoice, outstanding) {\n  console.log(`name: ${invoice.customer}`)\n  console.log(`amount: ${outstanding}`)\n}\n```\n\n### 以多态取代条件表达式\n\n用多态消除长长的 switch-case 或 if-else。\n\n## 总结\n\n重构不是一次性的活动，而应该融入日常开发中。好的代码不是写出来的，而是改出来的。',
 'refactoring-reading-notes',
 1, 0, 78, 10, 4, 1, 4, @admin_id, '2026-05-28 10:00:00', NOW());

-- ----- 随笔分类文章 -----
INSERT IGNORE INTO `article` (`id`, `title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`) VALUES
(10,
 '关于技术选型的一些思考',
 '面对众多技术栈，如何做出合适的选择？本文分享我在技术选型时的一些思考框架。',
 '# 关于技术选型的一些思考\n\n## 选择的标准\n\n在做技术选型时，我通常会考虑以下几个维度：\n\n### 1. 社区活跃度\n\n- GitHub Star 数量和趋势\n- 贡献者数量和更新频率\n- 文档完善程度\n\n### 2. 学习成本\n\n- 概念的复杂度\n- 是否有充足的教程和资料\n- 团队其他成员的熟悉程度\n\n### 3. 生态完整性\n\n- 是否有一整套配套工具\n- 与其他技术栈的兼容性\n\n## 一些感悟\n\n> 没有最好的技术，只有最合适的技术。\n\n不要盲目追逐新技术。选择的技术应该能够解决当前的实际问题，而不是为了用新而用新。\n\n## 我的选择\n\n对于个人项目我通常选择：\n\n- **后端**：Spring Boot（Java 生态首选）\n- **前端**：Vue（上手快，文档好）\n- **数据库**：MySQL（稳定可靠）\n- **缓存**：Redis（功能强大）\n\n技术选型没有标准答案，重要的是理解每个选项的 trade-off。',
 'tech-stack-thoughts',
 1, 0, 134, 15, 7, 4, 5, @admin_id, '2026-05-30 10:00:00', NOW()),

(11,
 '为什么每个开发者都应该有自己的博客',
 '拥有一个个人博客对技术人来说是一种长期投资。分享我的写作心得和收获。',
 '# 为什么每个开发者都应该有自己的博客\n\n## 写作的好处\n\n### 1. 加深理解\n\n> 如果你不能简单地说清楚，那说明你还没真正理解。—— 爱因斯坦\n\n写文章的过程就是逼迫自己把知识梳理清楚的过程。很多时候我以为自己懂了，真正写的时候才发现还有盲点。\n\n### 2. 建立个人品牌\n\n博客是你的个人名片。面试官看到你有技术博客，第一印象就会加分。\n\n### 3. 帮助他人\n\n你踩过的坑很可能是别人正在踩的坑。一篇博客可能帮到成千上万的开发者。\n\n### 4. 记录成长\n\n回头看自己一年前写的文章，能清晰地看到自己的进步。\n\n## 怎么开始\n\n- **不要追求完美**：第一篇文章不要求长篇大论，几百字也可以\n- **从笔记开始**：把日常解决问题的笔记整理成文章\n- **坚持比质量重要**：每周一篇比写了一篇完美的就停更更有价值\n\n## 写在最后\n\n这个博客就是我的实践。从搭建到写作，整个过程都是学习的一部分。希望看到这里的你，也勇敢迈出第一步。',
 'why-developers-need-blog',
 1, 0, 178, 22, 13, 6, 5, @admin_id, '2026-05-31 15:00:00', NOW());

-- =====================================================
-- 4. 文章-标签关联
-- =====================================================
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES
-- 文章1：Spring Boot
(1, 1), (1, 2),
-- 文章2：Vue
(2, 3), (2, 4),
-- 文章3：MySQL
(3, 5),
-- 文章4：全栈项目
(4, 1), (4, 2), (4, 3),
-- 文章5：Docker
(5, 7), (5, 8),
-- 文章6：生活
-- 无标签
-- 文章7：生活
-- 无标签
-- 文章8：读书
(8, 6),
-- 文章9：读书
(9, 1),
-- 文章10：随笔
(10, 1), (10, 2), (10, 4),
-- 文章11：随笔
(11, 3), (11, 4);

-- =====================================================
-- 5. 插入几条示例评论
-- =====================================================
INSERT IGNORE INTO `comment` (`id`, `content`, `user_id`, `article_id`, `parent_id`, `like_count`, `status`, `created_at`) VALUES
(1, '写得非常详细，对新手很友好！', @admin_id, 1, NULL, 3, 1, '2026-05-16 10:00:00'),
(2, '请问有没有配套的源码可以学习？', @admin_id, 1, NULL, 1, 1, '2026-05-17 14:00:00'),
(3, '同问，希望博主能分享一下源码', @admin_id, 1, 2, 2, 1, '2026-05-17 16:00:00'),
(4, 'Composition API 确实比 Options API 好用很多', @admin_id, 2, NULL, 5, 1, '2026-05-19 09:00:00'),
(5, '第二点说的太对了，写文章最能发现自己知识的盲区', @admin_id, 11, NULL, 4, 1, '2026-06-01 09:00:00');

-- 重置自增主键（避免冲突）
ALTER TABLE `article` AUTO_INCREMENT = 12;
ALTER TABLE `comment` AUTO_INCREMENT = 6;
ALTER TABLE `category` AUTO_INCREMENT = 6;
ALTER TABLE `tag` AUTO_INCREMENT = 9;
