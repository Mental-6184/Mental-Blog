-- =====================================================
-- MentalBlog 示例文章数据
-- 5 个分类，每个分类 3 篇，共 15 篇
-- =====================================================

-- 文章 1: 技术 - Spring Boot 3.0 入门指南：从零搭建第一个应用
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'Spring Boot 3.0 入门指南：从零搭建第一个应用',
'本文手把手带你从零开始搭建一个 Spring Boot 3.0 项目，涵盖项目初始化、依赖配置、REST API 编写和热部署配置。',
'## 前言

Spring Boot 作为 Java 生态中最流行的微服务框架，以其"约定优于配置"的理念大大简化了 Spring 应用的开发。本文将带你从零开始，搭建你的第一个 Spring Boot 3.0 应用。

## 环境准备

在开始之前，你需要准备以下环境：

- JDK 17 或更高版本
- Maven 3.6+ 或 Gradle 7+
- IDE（推荐 IntelliJ IDEA）

## 项目初始化

打开 Spring Initializr，选择以下配置：

- Project: Maven
- Language: Java
- Spring Boot: 3.0.x
- Dependencies: Spring Web, Spring Boot DevTools

点击 Generate 下载项目压缩包，解压后用 IDE 打开。

## 编写第一个 Controller

在启动类同包下创建 HelloController.java：

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot 3.0!";
    }
}
```

## 运行与测试

在 IDE 中运行启动类，控制台输出 Spring 图标即启动成功。打开浏览器访问 http://localhost:8080/hello，看到返回信息说明一切正常。

## 总结

本文介绍了 Spring Boot 3.0 项目从初始化到运行的全流程。Spring Boot 的自动配置和起步依赖让开发变得异常高效，后续文章我们将深入探讨数据库集成、安全认证等高级主题。',
'spring-boot-guide', 1, 1, 0, 0, 0, 1, 1, NOW(), NOW()
);

-- 文章 2: 技术 - Vue 3 Composition API 实战：告别 Options API
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'Vue 3 Composition API 实战：告别 Options API',
'深入解析 Vue 3 Composition API 的核心概念，通过实际案例对比 Options API 与 Composition API 的差异。',
'## 为什么需要 Composition API？

在 Vue 2 中，我们使用 Options API（data、methods、computed）组织组件逻辑。当组件变得复杂时，同一功能的代码被分散在不同的选项中，导致代码难以维护。Composition API 让我们可以将相关逻辑聚合在一起。

## setup 函数

setup 是 Composition API 的入口，在组件创建之前执行。它是组合组件逻辑的地方。script setup 是语法糖，让 Composition API 的写法更加简洁。

## ref 与 reactive

ref 用于定义基本类型的响应式数据，reactive 用于对象类型。两者都会让数据变成响应式的，区别在于访问方式。

## 自定义 Hook

Composition API 最大的优势是逻辑复用。我们可以将公共逻辑抽取为自定义 Hook，在多个组件中共享状态逻辑。

## 总结

Composition API 不是要取代 Options API，而是提供更灵活的逻辑组织方式。对于简单组件，Options API 仍然直观；对于复杂业务逻辑，Composition API 的优势非常明显。',
'vue3-composition-api', 1, 0, 0, 0, 0, 1, 1, NOW(), NOW()
);

-- 文章 3: 技术 - MySQL 索引优化实战：让查询速度提升 100 倍
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'MySQL 索引优化实战：让查询速度提升 100 倍',
'索引是 MySQL 性能优化的核心手段。本文通过真实案例，讲解索引的原理、类型选择和最佳实践。',
'## 索引是什么？

想象一本没有目录的技术书，你要找某一节内容，只能从头翻到尾——这就是全表扫描。索引就是书的目录，它告诉 MySQL 数据在哪一页，极大减少扫描的数据量。

## 索引类型

MySQL 中最常用的索引类型是 B+ Tree 索引。常见分类：

- 主键索引：唯一的聚簇索引，存储整行数据
- 普通索引：加速查询，不要求唯一
- 唯一索引：加速查询 + 保证数据唯一
- 联合索引：多个字段组合成一个索引

## 最左前缀原则

联合索引 (a, b, c) 遵循最左前缀原则。查询条件必须从最左列开始匹配才能使用索引。

## 实战优化案例

某文章列表页查询耗时 3.2 秒，添加联合索引后，查询耗时降至 0.03 秒，性能提升超过 100 倍。

## 常见误区

1. 索引不是越多越好：每个索引占用磁盘空间，降低写入性能
2. 避免在索引列上使用函数，否则会让索引失效
3. 使用 EXPLAIN 分析 SQL，关注 type、rows 字段

## 总结

索引优化是后端开发的基本功。用 EXPLAIN 分析慢查询，针对性建立联合索引，遵循最左前缀原则。',
'mysql-index-optimization', 1, 0, 0, 0, 0, 1, 1, NOW(), NOW()
);

-- 文章 4: 生活 - 晨跑 100 天：坚持带来的改变
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'晨跑 100 天：坚持带来的改变',
'从最初跑 1 公里就气喘吁吁，到能轻松完成 10 公里。100 天的晨跑习惯改变了我的生活。',
'## 为什么开始跑步？

2025 年底的一次体检给了我当头一棒——脂肪肝、颈椎曲度变直、体重超标。作为一个天天坐在电脑前的程序员，我的身体在抗议。于是我做了一个决定：每天早起跑步。

## 第一周：最难熬的日子

1 月的早晨 6 点，天黑得让人不想起床。穿上跑鞋的那一刻，内心有无数个声音在说再睡一会儿。第一天跑出去不到 500 米就已经喘不上气。

但我知道，任何习惯的养成都需要度过最初的不适期。我告诉自己：只要穿上跑鞋走出门，就已经成功了一半。

## 第三十天：渐入佳境

坚持一个月后，身体开始适应。配速从 8 分半提升到了 6 分半，能连续跑 5 公里而不觉得特别累。生物钟调整过来了，每天 5 点 50 自然醒。

## 第一百天：跑步成为生活的一部分

现在跑步不再需要毅力来维持。体重下降了 8 公斤，脂肪肝消失了，颈椎也不再酸痛。更重要的是，我学会了如何将这种坚持迁移到工作和学习中。

## 给想开始跑步的人

不要一开始就追求速度和距离。穿上跑鞋，走出门，慢跑 10 分钟也好。重要的是迈出第一步，然后保持下去。',
'morning-run-100-days', 1, 1, 0, 0, 0, 2, 1, NOW(), NOW()
);

-- 文章 5: 生活 - 周末厨房：手冲咖啡入门指南
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'周末厨房：手冲咖啡入门指南',
'从选豆到冲煮，手把手教你做出一杯好喝的手冲咖啡。分享我两年的手冲经验。',
'## 从速溶到手冲

两年前我对咖啡的认知还停留在速溶和星巴克。直到朋友送了一套手冲壶和 V60 滤杯，第一次冲煮时被那股花果香气惊艳到，从此掉进了手冲咖啡的坑。

## 需要什么器具？

新手入门不需要太复杂的设备，以下四样就够了：

1. 手冲壶：推荐细嘴壶，控水更精准
2. 滤杯 + 滤纸：V60 最经典
3. 咖啡豆：新鲜烘焙是王道
4. 磨豆机：手磨或电磨都可以

## 冲煮参数

经过反复调试，我最喜欢的参数组合：水温 92 度，粉水比 1:15，研磨度白砂糖颗粒大小，时间 2 分 30 秒。

## 结语

手冲咖啡的魅力在于每一次冲煮都独一无二。同样的豆子、同样的参数，不同的人冲出来味道也不一样。这种不确定性和探索的过程，正是手冲的乐趣所在。',
'hand-brew-coffee', 1, 0, 0, 0, 0, 2, 1, NOW(), NOW()
);

-- 文章 6: 生活 - 极简主义生活实践：我扔掉了 80% 的物品
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'极简主义生活实践：我扔掉了 80% 的物品',
'受到断舍离的启发，我开始实践极简主义生活。半年内处理掉了家中 80% 的物品。',
'## 为什么要极简？

作为一个程序员，我的桌面总是堆满了各种数据线、技术书籍和杂七杂八的周边。每次找东西都要翻半天。

2025 年底搬家的时候，我惊讶地发现自己竟然有 8 箱书和数不清的杂物。搬完家后我坐在一堆纸箱中间问自己：这些真的都是我需要的吗？

## 断舍离的过程

我用整理法：把每件物品拿在手里，问自己这让我心动吗？犹豫超过 3 秒的，统一放进待处理箱。

第一轮清理掉了过期药品、坏掉的电子产品、不再穿的衣物。第二轮把读过的技术书送给学弟学妹。第三轮是最难的——那些可能有纪念意义的东西。我告诉自己，回忆不需要用实物来承载。

## 带来的改变

1. 省钱了：不再乱买东西
2. 省时间了：找东西从翻箱倒柜到一眼定位
3. 心理负担小了：物品越少，心里越安静
4. 专注力提升了：整洁的环境带来清晰的思维',
'minimalist-life', 1, 0, 0, 0, 0, 2, 1, NOW(), NOW()
);

-- 文章 7: 项目 - 从零到一：MentalBlog 博客系统架构全解析
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'从零到一：MentalBlog 博客系统架构全解析',
'详细解析 MentalBlog 博客系统的技术选型、架构设计、数据库设计和部署方案。',
'## 项目背景

作为一个技术写作者，我一直想要一个完全属于自己的博客平台。WordPress 太臃肿，Hexo 不够灵活。思来想去，决定自己动手写一个。这就是 MentalBlog 的由来。

## 技术选型

后端：Spring Boot 4.0 + MyBatis-Plus，4.0 版本支持 JDK 17。前端：Vue 3 + TypeScript + Vite，Vite 的 HMR 让开发体验丝滑。数据库：MySQL 8 + Redis。

## 系统架构

项目采用前后端分离架构：Nginx 反向代理静态资源，后端 Spring Boot API 运行在 8080 端口，MySQL 持久化，Redis 缓存。

## 数据库设计

核心表共 10 张：用户、分类、标签、文章、文章标签关联、评论、收藏、点赞、友情链接、站点配置。文章表采用冗余设计，直接存储评论数、点赞数和阅读量。

## 总结

从立项到上线历时 6 周，经历了完整项目流程。最大的收获不只是技术成长，更是对项目全生命周期的深刻理解。',
'mentalblog-architecture', 1, 1, 0, 0, 0, 3, 1, NOW(), NOW()
);

-- 文章 8: 项目 - 基于 WebSocket 的实时消息系统设计与实现
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'基于 WebSocket 的实时消息系统设计与实现',
'从轮询到 WebSocket，讲解如何设计一个高并发实时消息系统。',
'## 为什么需要 WebSocket？

传统的 HTTP 协议是请求-响应模式，服务器无法主动推送消息。为了实现实时通信，早期方案是轮询——客户端每隔几秒发一次请求，浪费带宽且不够实时。

WebSocket 建立在 TCP 之上，连接建立后双方可以随时发送数据，真正实现全双工通信。

## WebSocket 握手

WebSocket 连接通过 HTTP 升级请求建立，服务端验证后返回 101 状态码，连接建立。

## 心跳机制

连接建立后需要定时发送心跳包维持连接。服务端连续三次未收到客户端心跳则判定断开。

## 集群部署

单机 WebSocket 连接数有限，集群部署需解决 Session 共享。使用 Redis 发布订阅广播消息。

## 总结

WebSocket 是实现实时通信的最佳方案之一。设计时需要注意心跳保活、断线重连和集群 Session 共享等关键问题。',
'websocket-real-time-system', 1, 0, 0, 0, 0, 3, 1, NOW(), NOW()
);

-- 文章 9: 项目 - 使用 Docker Compose 搭建 CI/CD 自动化部署流水线
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'使用 Docker Compose 搭建 CI/CD 自动化部署流水线',
'详述如何使用 GitHub Actions + Docker Compose 搭建一套完整的 CI/CD 流水线。',
'## 为什么要做 CI/CD？

在团队协作中，最痛苦的事情之一是代码合并后手动部署不仅效率低下，还容易出问题。CI/CD 就是为了解决这个问题。

## 技术栈

- GitHub Actions：持续集成
- Docker Compose：容器编排
- Docker Hub：镜像仓库

## CI 流水线

每次推送代码到 main 分支，GitHub Actions 自动触发：检出代码、构建后端、构建前端、构建 Docker 镜像、推送到镜像仓库。

## Docker Compose 部署

在服务器上使用 Docker Compose 一键启动所有服务，包括应用、MySQL、Redis 和 Nginx。

## 效果

配置完成后，一次代码推送从提交到线上更新只需要 3 分钟。建议所有中大型项目都应接入自动化 CI/CD。',
'cicd-docker-compose', 1, 0, 0, 0, 0, 3, 1, NOW(), NOW()
);

-- 文章 10: 读书笔记 - 《深入理解 Java 虚拟机》核心笔记：内存模型与垃圾回收
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'《深入理解 Java 虚拟机》核心笔记：内存模型与垃圾回收',
'读周志明《深入理解 Java 虚拟机》精华总结，涵盖 JVM 内存区域、垃圾回收算法和调优经验。',
'## 为什么读这本书？

周志明老师的这本书是 Java 开发者必读的经典。读完后你会真正理解 Java 程序的运行机制，遇到内存溢出和性能问题时不再束手无策。

## JVM 内存区域

线程私有区域：程序计数器、虚拟机栈、本地方法栈。线程共享区域：堆和方法区。堆是 GC 管理的主要区域。

## 对象创建过程

类加载检查、分配内存、初始化零值、设置对象头、执行 init 方法。

## 垃圾回收算法

标记-清除：最基础，会产生内存碎片。复制算法：效率高但浪费空间，适合新生代。标记-整理：适合老年代。

## 垃圾收集器

CMS：以最短停顿时间为目标。G1：面向服务端，可预测停顿时间。ZGC：JDK 11 引入，停顿不超过 10ms。',
'jvm-deep-understanding', 1, 1, 0, 0, 0, 4, 1, NOW(), NOW()
);

-- 文章 11: 读书笔记 - 《代码整洁之道》读后感：写代码是一种修行
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'《代码整洁之道》读后感：写代码是一种修行',
'分享《代码整洁之道》阅读后的核心收获：命名规范、函数设计、注释原则。',
'## 整洁的代码意味着什么？

写代码的时间只占 10%，剩下的 90% 都在读代码。如果你的代码难以阅读，那不只是浪费别人的时间，更是浪费自己的时间。

## 命名规范

变量名要表达意图，避免误导，做有意义的区分，使用读得出来的名字。好的命名让代码自文档化。

## 函数设计

函数应该短小，不超过 20 行。只做一件事，参数越少越好。如果一个函数做了三件事——验证数据、执行业务逻辑、发送通知——请拆成三个函数。

## 注释不是万能的

注释是一种失败，因为我们无法用代码表达意图。与其写注释解释一段混乱的代码，不如把代码改清楚。好的代码本身就是最好的注释。

## 实践建议

童子军规则：离开时让代码比来时更干净。重构优先于重写。代码审查是成长最快的方式。',
'clean-code-notes', 1, 0, 0, 0, 0, 4, 1, NOW(), NOW()
);

-- 文章 12: 读书笔记 - 《软技能：代码之外的生存指南》精华提炼
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'《软技能：代码之外的生存指南》精华提炼',
'程序员不能只会写代码。这本书从个人品牌、学习方法到职业规划全面剖析自我提升之道。',
'## 打破误区

很多程序员认为只要技术够硬就一定能成功。这种想法是错的。技术能力只是基础，沟通能力、个人品牌、商业思维同样重要。

## 个人品牌建设

写技术博客、开源贡献、社交媒体分享、演讲——这些都能帮你建立个人品牌。当别人提到某个技术方向时能想到你的名字，你就拥有了职业发展的主动权。

## 学习方法

了解全局、确定范围、定义目标、寻找资源、创建计划、动手实践、教给别人、融会贯通、温故知新。教给别人是最有效的学习方式。

## 写在最后

编程只是程序员价值的一部分。学会沟通、建立品牌、管理时间、规划职业——这些软技能才是决定职业天花板的关键。',
'soft-skills-reading', 1, 0, 0, 0, 0, 4, 1, NOW(), NOW()
);

-- 文章 13: 随笔 - 编程三年，我的技术成长之路
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'编程三年，我的技术成长之路',
'从大学第一行 Hello World 到独立开发完整项目，回顾三年编程路上的成长。',
'## 起点

三年前我坐在大学机房里，对着屏幕写下了人生第一行代码。红色的错误提示密密麻麻，我不知道什么是类，什么是对象，甚至连 main 函数写错了都看不出来。那时候我觉得编程是世界上最难的事。

## 迷茫期

大二开始接触 Java，SSM 框架让我晕头转向。看着 GitHub 上那些开源项目，再看看自己的代码，差距大到让人绝望。那段时间我一度怀疑自己是不是不适合编程。

## 转折点

转折发生在一次课程项目中。我花了两周时间从需求分析到编码实现。项目虽然简陋，但跑通的那一刻，那种成就感无法用语言形容。

我突然意识到：编程不是看会的，是写会的。

## 给新手的建议

不要怕犯错，错误是最好的老师。动手比看教程重要 10 倍。学会用搜索引擎。坚持写博客，输出倒逼输入。保持好奇心。',
'three-years-coding', 1, 1, 0, 0, 0, 5, 1, NOW(), NOW()
);

-- 文章 14: 随笔 - 关于技术选型的一些思考：不要为了用而用
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'关于技术选型的一些思考：不要为了用而用',
'面对层出不穷的新技术，如何做出理性的技术选型决策？分享五大原则和三个陷阱。',
'## 一个教训

去年接手一个小项目，是一个内部管理系统，日均 PV 不到 1000。前一个开发者用了 Spring Cloud 全家桶。我问他为什么要用微服务，他说：因为大家都在用啊。这就是典型的技术选型错误。

## 五大原则

1. 业务优先：技术是为业务服务的
2. 团队熟悉度：考虑团队技术栈
3. 社区活跃度：活跃社区意味着能找到解决方案
4. 长期维护性：选择有良好演进历史的技术
5. 够用就好：不是越复杂越好，是越合适越好

## 三个陷阱

简历驱动开发：什么火用什么。羊群效应：大厂用什么我们就用什么。技术崇拜：盲目相信新技术。

## 我的选择框架

问自己三个问题：这个技术解决了我什么痛点？引入成本是多少？有没有更简单的替代方案？',
'tech-selection-thoughts', 1, 0, 0, 0, 0, 5, 1, NOW(), NOW()
);

-- 文章 15: 随笔 - 写给一年前的自己的一封信
INSERT IGNORE INTO `article` (`title`, `summary`, `content`, `slug`, `status`, `top`, `view_count`, `like_count`, `comment_count`, `category_id`, `author_id`, `published_at`, `created_at`)
VALUES (
'写给一年前的自己的一封信',
'如果能够穿越时空，我会对一年前那个焦虑、迷茫的自己说些什么？',
'亲爱的自己：

你好。我知道你现在正处于低谷期，项目 deadline 压得喘不过气，新技术学不完的焦虑感如影随形，身边的朋友好像都在飞速进步，只有你原地踏步。

## 关于焦虑

你现在的焦虑来源于和别人的比较。每个人的节奏不同，有的人 25 岁就功成名就，有的人 35 岁才找到方向。你的路要自己走，不是复制别人的。

## 关于学习

你总想把所有技术都学完才安心。但这是一个永远无法完成的目标。与其泛泛地了解十门技术，不如精通一门。

## 关于写代码

先把功能做出来，再考虑优化。代码是迭代出来的，不是一次性写出来的。过度追求完美会拖慢进度。

## 关于生活

去跑跑步、看看电影、约朋友吃顿饭。身体是革命的本钱，注意颈椎和眼睛。

一年后的你',
'letter-to-past-self', 1, 0, 0, 0, 0, 5, 1, NOW(), NOW()
);

-- =====================================================
-- 文章-标签关联
-- =====================================================

INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'spring-boot-guide'), (SELECT `id` FROM `tag` WHERE `name` = 'Spring Boot'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'spring-boot-guide'), (SELECT `id` FROM `tag` WHERE `name` = 'Java'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'vue3-composition-api'), (SELECT `id` FROM `tag` WHERE `name` = 'Vue'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'vue3-composition-api'), (SELECT `id` FROM `tag` WHERE `name` = '前端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'mysql-index-optimization'), (SELECT `id` FROM `tag` WHERE `name` = 'MySQL'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'mysql-index-optimization'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'mentalblog-architecture'), (SELECT `id` FROM `tag` WHERE `name` = 'Spring Boot'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'mentalblog-architecture'), (SELECT `id` FROM `tag` WHERE `name` = 'Vue'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'mentalblog-architecture'), (SELECT `id` FROM `tag` WHERE `name` = '架构'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'websocket-real-time-system'), (SELECT `id` FROM `tag` WHERE `name` = 'Java'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'websocket-real-time-system'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'cicd-docker-compose'), (SELECT `id` FROM `tag` WHERE `name` = 'Docker'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'cicd-docker-compose'), (SELECT `id` FROM `tag` WHERE `name` = 'Linux'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'jvm-deep-understanding'), (SELECT `id` FROM `tag` WHERE `name` = 'Java'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'jvm-deep-understanding'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'clean-code-notes'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'soft-skills-reading'), (SELECT `id` FROM `tag` WHERE `name` = '架构'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'three-years-coding'), (SELECT `id` FROM `tag` WHERE `name` = 'Java'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'three-years-coding'), (SELECT `id` FROM `tag` WHERE `name` = '前端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'three-years-coding'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'tech-selection-thoughts'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'tech-selection-thoughts'), (SELECT `id` FROM `tag` WHERE `name` = '架构'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'letter-to-past-self'), (SELECT `id` FROM `tag` WHERE `name` = '前端'));
INSERT IGNORE INTO `article_tag` (`article_id`, `tag_id`) VALUES ((SELECT `id` FROM `article` WHERE `slug` = 'letter-to-past-self'), (SELECT `id` FROM `tag` WHERE `name` = '后端'));