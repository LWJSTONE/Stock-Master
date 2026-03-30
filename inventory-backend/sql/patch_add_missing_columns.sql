-- 数据库迁移脚本：添加缺失的字段
-- 执行日期：2024-03-30
-- 说明：修复仓库、供应商、客户实体缺少的字段

-- ==================== 仓库表(base_warehouse)添加缺失字段 ====================
-- 添加联系电话字段
ALTER TABLE base_warehouse ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '联系电话';
-- 添加仓库面积字段
ALTER TABLE base_warehouse ADD COLUMN IF NOT EXISTS area VARCHAR(50) COMMENT '仓库面积';
-- 添加备注字段
ALTER TABLE base_warehouse ADD COLUMN IF NOT EXISTS remark VARCHAR(500) COMMENT '备注';

-- ==================== 供应商表(base_supplier)添加缺失字段 ====================
-- 添加邮箱字段
ALTER TABLE base_supplier ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '邮箱';
-- 添加开户银行字段
ALTER TABLE base_supplier ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100) COMMENT '开户银行';
-- 添加状态字段
ALTER TABLE base_supplier ADD COLUMN IF NOT EXISTS status INT DEFAULT 1 COMMENT '状态(0禁用 1启用)';
-- 添加备注字段
ALTER TABLE base_supplier ADD COLUMN IF NOT EXISTS remark VARCHAR(500) COMMENT '备注';

-- ==================== 客户表(base_customer)添加缺失字段 ====================
-- 添加邮箱字段
ALTER TABLE base_customer ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '邮箱';
-- 添加信用额度字段
ALTER TABLE base_customer ADD COLUMN IF NOT EXISTS credit_limit DECIMAL(12,2) DEFAULT 0 COMMENT '信用额度';
-- 添加客户等级字段
ALTER TABLE base_customer ADD COLUMN IF NOT EXISTS level VARCHAR(20) DEFAULT 'normal' COMMENT '客户等级';
-- 添加状态字段
ALTER TABLE base_customer ADD COLUMN IF NOT EXISTS status INT DEFAULT 1 COMMENT '状态(0禁用 1启用)';
-- 添加备注字段
ALTER TABLE base_customer ADD COLUMN IF NOT EXISTS remark VARCHAR(500) COMMENT '备注';

-- ==================== 更新现有数据状态默认值 ====================
UPDATE base_supplier SET status = 1 WHERE status IS NULL;
UPDATE base_customer SET status = 1 WHERE status IS NULL;
