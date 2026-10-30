-- =====================================================
-- MentalBlog 关于我页面数据表
-- 技能栈、项目经历、成长里程碑
-- =====================================================

-- 技能栈表
CREATE TABLE IF NOT EXISTS `skill` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `name`        VARCHAR(50)   NOT NULL                  COMMENT '技能名称',
    `level`       INT           NOT NULL DEFAULT 0        COMMENT '熟练度 0-100',
    `sort`        INT           NOT NULL DEFAULT 0        COMMENT '排序',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能栈表';

-- 项目经历表
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

-- 成长里程碑表
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
