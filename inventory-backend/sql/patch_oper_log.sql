-- =============================================
-- 操作日志表补丁脚本
-- 添加缺失的字段
-- =============================================

USE `inventory_v2`;

-- 检查并添加 oper_location 字段
SET @dbname = DATABASE();
SET @tablename = 'sys_oper_log';
SET @columnname = 'oper_location';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = @tablename
    AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` VARCHAR(255) DEFAULT NULL COMMENT ''操作地点'' AFTER `oper_ip`')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查并添加 cost_time 字段
SET @columnname = 'cost_time';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = @tablename
    AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` BIGINT DEFAULT 0 COMMENT ''消耗时间(毫秒)'' AFTER `oper_time`')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SELECT '操作日志表补丁执行完成' AS Message;
