USE `mentalblog`;

ALTER TABLE `comment` MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝';

-- favorite 与 like_record 的唯一索引已由 init.sql 创建；执行前请清理旧库中的重复历史数据。
