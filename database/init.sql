-- AI面试系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ai_interview` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `ai_interview`;

-- 创建用户表（示例）
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建面试记录表（示例）
CREATE TABLE IF NOT EXISTS `interview_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '面试记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `position` varchar(100) NOT NULL COMMENT '面试岗位',
  `questions` text COMMENT '面试问题',
  `answers` text COMMENT '用户回答',
  `ai_feedback` text COMMENT 'AI反馈',
  `score` int DEFAULT 0 COMMENT '面试分数',
  `status` varchar(20) DEFAULT 'pending' COMMENT '面试状态',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录表';