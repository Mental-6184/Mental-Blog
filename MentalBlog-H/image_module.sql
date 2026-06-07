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
