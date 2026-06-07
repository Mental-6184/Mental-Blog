-- =====================================================
-- MentalBlog 数据库初始化脚本
-- 数据库：mentalblog
-- 编码：utf8mb4
-- =====================================================

CREATE DATABASE IF NOT EXISTS `mentalblog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mentalblog`;

-- =====================================================
-- 1. 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `username`      VARCHAR(50)   NOT NULL                  COMMENT '用户名',
    `password`      VARCHAR(255)  NOT NULL                  COMMENT '加密后密码',
    `nickname`      VARCHAR(50)   DEFAULT NULL              COMMENT '昵称',
    `email`         VARCHAR(100)  NOT NULL                  COMMENT '邮箱',
    `avatar`        VARCHAR(255)  DEFAULT NULL              COMMENT '头像 URL',
    `bio`           VARCHAR(500)  DEFAULT NULL              COMMENT '个人简介',
    `role`          VARCHAR(20)   NOT NULL DEFAULT 'USER'   COMMENT '角色: ADMIN / USER',
    `status`        TINYINT       NOT NULL DEFAULT 0        COMMENT '状态: 0-正常, 1-封禁',
    `image_permission` TINYINT    NOT NULL DEFAULT 0        COMMENT '图片权限: 0-未申请, 1-待审核, 2-已通过, 3-已拒绝',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 分类表
-- =====================================================
CREATE TABLE IF NOT EXISTS `category` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`          VARCHAR(50)   NOT NULL                  COMMENT '分类名',
    `slug`          VARCHAR(50)   NOT NULL                  COMMENT 'URL 友好标识',
    `description`   VARCHAR(500)  DEFAULT NULL              COMMENT '描述',
    `sort`          INT           NOT NULL DEFAULT 0        COMMENT '排序值（越小越靠前）',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =====================================================
-- 3. 标签表
-- =====================================================
CREATE TABLE IF NOT EXISTS `tag` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`          VARCHAR(50)   NOT NULL                  COMMENT '标签名',
    `slug`          VARCHAR(50)   NOT NULL                  COMMENT 'URL 友好标识',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =====================================================
-- 4. 文章表
-- =====================================================
CREATE TABLE IF NOT EXISTS `article` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `title`           VARCHAR(200)  NOT NULL                  COMMENT '标题',
    `summary`         VARCHAR(500)  DEFAULT NULL              COMMENT '摘要',
    `content`         LONGTEXT      NOT NULL                  COMMENT 'Markdown 正文',
    `cover_image`     VARCHAR(255)  DEFAULT NULL              COMMENT '封面图',
    `slug`            VARCHAR(100)  NOT NULL                  COMMENT 'URL 别名',
    `status`          TINYINT       NOT NULL DEFAULT 0        COMMENT '状态: 0-草稿, 1-已发布',
    `top`             TINYINT       NOT NULL DEFAULT 0        COMMENT '置顶: 0-否, 1-是',
    `view_count`      INT           NOT NULL DEFAULT 0        COMMENT '阅读量',
    `like_count`      INT           NOT NULL DEFAULT 0        COMMENT '点赞数',
    `favorite_count`  INT           NOT NULL DEFAULT 0        COMMENT '收藏数',
    `comment_count`   INT           NOT NULL DEFAULT 0        COMMENT '评论数',
    `category_id`     BIGINT        DEFAULT NULL              COMMENT '所属分类 ID',
    `author_id`       BIGINT        NOT NULL                  COMMENT '作者 ID',
    `published_at`    DATETIME      DEFAULT NULL              COMMENT '发布时间',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status_published` (`status`, `published_at`),
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- =====================================================
-- 5. 文章-标签关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS `article_tag` (
    `article_id`  BIGINT NOT NULL COMMENT '文章 ID',
    `tag_id`      BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`),
    CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- =====================================================
-- 6. 评论表
-- =====================================================
CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `content`     TEXT          NOT NULL                  COMMENT '评论内容',
    `user_id`     BIGINT        NOT NULL                  COMMENT '评论者 ID',
    `article_id`  BIGINT        NOT NULL                  COMMENT '所属文章 ID',
    `parent_id`   BIGINT        DEFAULT NULL              COMMENT '父评论 ID（支持嵌套）',
    `like_count`  INT           NOT NULL DEFAULT 0        COMMENT '点赞数',
    `status`      TINYINT       NOT NULL DEFAULT 1        COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 7. 收藏表
-- =====================================================
CREATE TABLE IF NOT EXISTS `favorite` (
    `id`          BIGINT    NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `user_id`     BIGINT    NOT NULL                  COMMENT '用户 ID',
    `article_id`  BIGINT    NOT NULL                  COMMENT '文章 ID',
    `created_at`  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    KEY `idx_article_id` (`article_id`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =====================================================
-- 8. 点赞记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS `like_record` (
    `id`          BIGINT    NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `user_id`     BIGINT    NOT NULL                  COMMENT '用户 ID',
    `article_id`  BIGINT    DEFAULT NULL              COMMENT '文章 ID（对文章点赞）',
    `comment_id`  BIGINT    DEFAULT NULL              COMMENT '评论 ID（对评论点赞）',
    `created_at`  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_comment_id` (`comment_id`),
    CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_like_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_like_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- =====================================================
-- 9. 友情链接表
-- =====================================================
CREATE TABLE IF NOT EXISTS `friend_link` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`        VARCHAR(100)  NOT NULL                  COMMENT '站点名称',
    `url`         VARCHAR(255)  NOT NULL                  COMMENT '链接',
    `logo`        VARCHAR(255)  DEFAULT NULL              COMMENT 'Logo',
    `description` VARCHAR(500)  DEFAULT NULL              COMMENT '描述',
    `sort`        INT           NOT NULL DEFAULT 0        COMMENT '排序',
    `status`      TINYINT       NOT NULL DEFAULT 1        COMMENT '状态: 0-隐藏, 1-显示',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- =====================================================
-- 10. 站点配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS `site_config` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `config_key`   VARCHAR(50)   NOT NULL                  COMMENT '配置键',
    `config_value` TEXT          NOT NULL                  COMMENT '配置值',
    `created_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

-- =====================================================
-- 11. 关于我页面数据表
-- =====================================================
CREATE TABLE IF NOT EXISTS `skill` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`        VARCHAR(50)   NOT NULL                  COMMENT '技能名称',
    `level`       INT           NOT NULL DEFAULT 0        COMMENT '熟练度 0-100',
    `sort`        INT           NOT NULL DEFAULT 0        COMMENT '排序',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能栈表';

CREATE TABLE IF NOT EXISTS `project` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`        VARCHAR(100)  NOT NULL                  COMMENT '项目名称',
    `description` VARCHAR(500)  DEFAULT NULL              COMMENT '项目描述',
    `url`         VARCHAR(255)  DEFAULT NULL              COMMENT '项目链接',
    `sort`        INT           NOT NULL DEFAULT 0        COMMENT '排序',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目经历表';

CREATE TABLE IF NOT EXISTS `milestone` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `year`        VARCHAR(10)   NOT NULL                  COMMENT '年份',
    `title`       VARCHAR(100)  NOT NULL                  COMMENT '标题',
    `description` VARCHAR(500)  DEFAULT NULL              COMMENT '描述',
    `sort`        INT           NOT NULL DEFAULT 0        COMMENT '排序',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成长里程碑表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入默认管理员账号 (用户名: admin, 密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`, `bio`)
VALUES ('admin', '$2b$10$qu7WiRDikvizFyvipeAM0uHMw3UscvksQYvKguqUczomZJmNL8fRm', '管理员', 'admin@mentalblog.com', 'ADMIN', 0, '博客管理员')
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 插入默认站点配置
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES
('site_name', 'MentalBlog'),
('site_description', '一个分享技术与生活的个人博客'),
('site_keywords', '技术,生活,博客,编程'),
('site_logo', ''),
('site_about', '## 关于我\n\n这个人很懒，还没有写介绍...'),
('site_footer', '© 2026 MentalBlog. All rights reserved.')
ON DUPLICATE KEY UPDATE `config_key` = `config_key`;

-- =========================================
-- 图片权限
-- =========================================
-- 图片权限申请表
CREATE TABLE IF NOT EXISTS `image_apply` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `user_id`     BIGINT        NOT NULL                  COMMENT '申请人 ID',
    `reason`      VARCHAR(500)  DEFAULT NULL              COMMENT '申请理由',
    `status`      TINYINT       NOT NULL DEFAULT 0        COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `reply`       VARCHAR(500)  DEFAULT NULL              COMMENT '管理员回复',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '申请时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片权限申请表';

-- =========================================
-- 图片模块（来自 image_module.sql）
-- =========================================
-- =====================================================
-- MentalBlog 图片模块
-- =====================================================

CREATE TABLE IF NOT EXISTS `image` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `filename`    VARCHAR(255)  NOT NULL                  COMMENT '原始文件名',
    `stored_path` VARCHAR(255)  NOT NULL                  COMMENT '存储路径',
    `url`         VARCHAR(500)  NOT NULL                  COMMENT '访问 URL',
    `size`        BIGINT        NOT NULL DEFAULT 0        COMMENT '文件大小(字节)',
    `mime_type`   VARCHAR(50)   NOT NULL DEFAULT 'image/jpeg' COMMENT '文件类型',
    `user_id`     BIGINT        NOT NULL                  COMMENT '上传者 ID',
    `description` VARCHAR(500)  DEFAULT NULL              COMMENT '图片描述',
    `like_count`  INT           NOT NULL DEFAULT 0        COMMENT '点赞数',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片表';

CREATE TABLE IF NOT EXISTS `image_tag` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`       VARCHAR(50)   NOT NULL                  COMMENT '标签名',
    `slug`       VARCHAR(50)   NOT NULL                  COMMENT 'URL 友好标识',
    `created_at` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片标签表';

CREATE TABLE IF NOT EXISTS `image_tag_rel` (
    `image_id` BIGINT NOT NULL COMMENT '图片 ID',
    `tag_id`   BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (`image_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片-标签关联表';

CREATE TABLE IF NOT EXISTS `image_comment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `content`     TEXT          NOT NULL                  COMMENT '评论内容',
    `user_id`     BIGINT        NOT NULL                  COMMENT '评论者 ID',
    `image_id`    BIGINT        NOT NULL                  COMMENT '所属图片 ID',
    `parent_id`   BIGINT        DEFAULT NULL              COMMENT '父评论 ID（支持嵌套）',
    `like_count`  INT           NOT NULL DEFAULT 0        COMMENT '点赞数',
    `status`      TINYINT       NOT NULL DEFAULT 1        COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_image_id` (`image_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片评论表';

-- 默认图片标签
INSERT IGNORE INTO `image_tag` (`name`, `slug`) VALUES
('风景', 'landscape'),
('人物', 'people'),
('美食', 'food'),
('科技', 'tech'),
('动物', 'animal'),
('建筑', 'architecture'),
('艺术', 'art'),
('插画', 'illustration'),
('摄影', 'photography'),
('其他', 'other');
