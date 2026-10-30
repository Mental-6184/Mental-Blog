USE `mentalblog`;

CREATE TABLE IF NOT EXISTS `article_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `summary` VARCHAR(500) DEFAULT NULL,
  `content` LONGTEXT NOT NULL,
  `cover_image` VARCHAR(255) DEFAULT NULL,
  `slug` VARCHAR(100) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0,
  `top` TINYINT NOT NULL DEFAULT 0,
  `category_id` BIGINT DEFAULT NULL,
  `tag_ids` VARCHAR(1000) DEFAULT NULL,
  `author_id` BIGINT NOT NULL,
  `note` VARCHAR(200) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), KEY `idx_article_version_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(40) NOT NULL,
  `article_id` BIGINT DEFAULT NULL,
  `comment_id` BIGINT DEFAULT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` VARCHAR(500) DEFAULT NULL,
  `read_status` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), KEY `idx_notification_user` (`user_id`,`read_status`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
