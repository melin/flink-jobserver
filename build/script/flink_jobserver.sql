/*
 Navicat Premium Data Transfer

 Source Server         : 10.0.8.2
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : 10.0.8.2:3306
 Source Schema         : flink_jobserver

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 18/11/2022 23:54:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fjs_cluster
-- ----------------------------
DROP TABLE IF EXISTS `fjs_cluster`;
CREATE TABLE `fjs_cluster` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(64) NOT NULL COMMENT 'code',
  `name` varchar(128) NOT NULL COMMENT 'name',
  `kerberos_enabled` smallint DEFAULT '0' COMMENT '是否启用kerberos 0：关闭，1：开启',
  `kerberos_keytab` longblob COMMENT 'kerberos keytab',
  `kerberos_file_name` varchar(255) DEFAULT NULL,
  `kerberos_config` longtext COMMENT 'kerberos conf',
  `kerberos_user` varchar(128) DEFAULT NULL COMMENT 'kerberos用户',
  `scheduler_type` varchar(45) DEFAULT 'YARN' COMMENT '调度框架:YARN、K8S',
  `jobserver_config` longtext COMMENT 'jobserver config',
  `flink_config` longtext COMMENT 'flink config',
  `core_config` longtext COMMENT 'core-site配置',
  `hdfs_config` longtext COMMENT 'hdfs-site配置',
  `yarn_config` longtext COMMENT 'yarn-site配置',
  `hive_config` longtext COMMENT 'hive-site配置',
  `storage_type` varchar(45) DEFAULT 'HDFS' COMMENT '存储类型:HDFS、OBS、OSS、S3等文件系统',
  `storage_config` longtext COMMENT '对象存储配置',
  `yarn_queue_name` varchar(255) DEFAULT NULL COMMENT '集群Yarn 默认队列',
  `status` smallint DEFAULT '1' COMMENT '0：无效，1：有效',
  `creater` varchar(45) NOT NULL COMMENT 'creater',
  `modifier` varchar(45) DEFAULT NULL COMMENT 'modifier',
  `gmt_created` datetime NOT NULL COMMENT 'gmt_create',
  `gmt_modified` datetime DEFAULT NULL COMMENT 'gmt_modify',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计算集群';

-- ----------------------------
-- Table structure for fjs_flink_driver
-- ----------------------------
DROP TABLE IF EXISTS `fjs_flink_driver`;
CREATE TABLE `fjs_flink_driver` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cluster_code` varchar(45) DEFAULT NULL COMMENT '集群Code',
  `version` int DEFAULT '0' COMMENT '乐观锁，避免重复提交',
  `server_ip` varchar(100) DEFAULT NULL,
  `server_port` int NOT NULL,
  `compute_type` varchar(45) DEFAULT NULL COMMENT 'thriftServer, driverServer',
  `status` varchar(45) NOT NULL COMMENT '状态',
  `application_id` varchar(64) NOT NULL,
  `log_server` varchar(64) DEFAULT NULL COMMENT 'spark 日志拉取server ip',
  `instance_count` int DEFAULT '0' COMMENT '运行实例数量',
  `server_cores` int NOT NULL COMMENT 'application占用core数',
  `server_memory` int NOT NULL COMMENT 'Application占用内存大小',
  `share_driver` tinyint(1) DEFAULT '0',
  `runtime_mode` varchar(32) DEFAULT NULL COMMENT '运行模式: batch & stream',
  `yarn_queue` varchar(255) DEFAULT NULL,
  `creater` varchar(45) DEFAULT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  `gmt_created` datetime NOT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_application_id` (`application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Job driver注册信息';

-- ----------------------------
-- Table structure for fjs_job_instance
-- ----------------------------
DROP TABLE IF EXISTS `fjs_job_instance`;
CREATE TABLE `fjs_job_instance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace` varchar(45) DEFAULT NULL COMMENT '项目code',
  `code` varchar(45) NOT NULL,
  `name` varchar(512) DEFAULT NULL,
  `cluster_code` varchar(64) DEFAULT 'default',
  `yarn_queue` varchar(128) DEFAULT NULL,
  `dependent_code` varchar(1024) DEFAULT 'START' COMMENT '依赖上一个实例code',
  `job_type` varchar(32) NOT NULL,
  `runtime_mode` varchar(32) DEFAULT NULL COMMENT '运行模式：batch, stream',
  `instance_type` varchar(32) NOT NULL COMMENT 'dev、schedule',
  `version` int NOT NULL DEFAULT '0',
  `status` varchar(45) NOT NULL DEFAULT '0',
  `schedule_time` datetime DEFAULT NULL COMMENT '调度开始时间',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `owner` varchar(64) NOT NULL,
  `runtimes` int DEFAULT '0' COMMENT '运行时间，单位秒',
  `max_retry_count` int DEFAULT NULL COMMENT '最大重试次数',
  `retry_count` int DEFAULT '0',
  `failure_count` int DEFAULT NULL COMMENT '失败次数',
  `application_id` varchar(128) DEFAULT 'spark app id',
  `client_name` varchar(128) DEFAULT '实例创建客户端名',
  `gmt_created` datetime NOT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `creater` varchar(45) NOT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_UNIQUE` (`code`) USING BTREE,
  KEY `idx_workspace_index` (`workspace`) USING BTREE,
  KEY `idx_application_id_index` (`application_id`) USING BTREE,
  KEY `idx_name` (`name`(128)) USING BTREE,
  KEY `idx_schedule_time` (`schedule_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业实例表';

-- ----------------------------
-- Table structure for fjs_job_instance_content
-- ----------------------------
DROP TABLE IF EXISTS `fjs_job_instance_content`;
CREATE TABLE `fjs_job_instance_content` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(45) NOT NULL COMMENT '实例code',
  `job_text` longtext COMMENT '作业内容',
  `job_config` varchar(4000) DEFAULT NULL COMMENT '作业运行参数',
  `error_msg` longtext COMMENT '错误信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实例内容表';

-- ----------------------------
-- Table structure for fjs_job_instance_dependent
-- ----------------------------
DROP TABLE IF EXISTS `fjs_job_instance_dependent`;
CREATE TABLE `fjs_job_instance_dependent` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(45) NOT NULL COMMENT 'code',
  `parent_code` varchar(45) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`,`parent_code`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_dependent_code` (`parent_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实例依赖表';

SET FOREIGN_KEY_CHECKS = 1;
