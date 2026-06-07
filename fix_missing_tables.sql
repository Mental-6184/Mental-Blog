-- Run this SQL to create missing image-related tables
-- These are required for Gallery functionality

ALTER TABLE `user`
ADD COLUMN `image_permission` TINYINT NOT NULL DEFAULT 0 COMMENT '图片权限: 0-未申请, 1-待审核, 2-已通过, 3-已拒绝'
AFTER `status`;

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
