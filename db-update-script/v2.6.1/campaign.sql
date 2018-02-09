CREATE TABLE `admin_user_privilege` (
	`user_privilege_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT(20) NOT NULL DEFAULT '0',
	`privilege_id` BIGINT(20) NOT NULL DEFAULT '0',
	PRIMARY KEY (`user_privilege_id`),
	INDEX `user_id` (`user_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `admin_user` (
	`user_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(64) NULL DEFAULT NULL,
	`email` VARCHAR(128) NOT NULL DEFAULT '0',
	`phone` VARCHAR(50) NOT NULL DEFAULT '0',
	`password` VARCHAR(128) NOT NULL DEFAULT '0',
	`last_login` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`enabled` TINYINT(1) NOT NULL DEFAULT '0',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`role_id` BIGINT(20) NOT NULL DEFAULT '0',
	`user_image` VARCHAR(1024) NOT NULL DEFAULT '0',
	`description` VARCHAR(256) NOT NULL DEFAULT '0',
	PRIMARY KEY (`user_id`),
	INDEX `username` (`username`),
	INDEX `email` (`email`),
	INDEX `phone` (`phone`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `admin_role_default_privilege` (
	`role_privilege_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`role_id` BIGINT(20) NOT NULL DEFAULT '0',
	`privilege_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`role_privilege_id`),
	INDEX `role_id` (`role_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `admin_role` (
	`role_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(128) NOT NULL COMMENT '角色名称',
	`description` VARCHAR(128) NULL DEFAULT NULL COMMENT '描述',
	PRIMARY KEY (`role_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `admin_privilege` (
	`privilege_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(128) NOT NULL DEFAULT '0',
	`chinese_name` VARCHAR(128) NOT NULL DEFAULT '0',
	`type` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0: page, 1: sigle',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`enabled` TINYINT(1) NOT NULL DEFAULT '1',
	`description` VARCHAR(256) NOT NULL DEFAULT '0',
	PRIMARY KEY (`privilege_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
