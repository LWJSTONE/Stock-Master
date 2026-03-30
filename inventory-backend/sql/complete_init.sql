-- =============================================
-- 库存管理系统完整初始化脚本
-- 包含：数据库结构 + 基础数据 + 补丁修复
-- 执行方式: mysql -u root -p < complete_init.sql
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 删除已存在的数据库（如果存在）
DROP DATABASE IF EXISTS `inventory_v2`;

-- 创建数据库
CREATE DATABASE `inventory_v2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `inventory_v2`;

-- =============================================
-- 第一部分：系统权限模块 (RBAC模型)
-- =============================================

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 部门表
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 3. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符',
    `data_scope` CHAR(1) DEFAULT '1' COMMENT '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 4. 菜单权限表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `type` CHAR(1) DEFAULT 'M' COMMENT '类型(M目录 C菜单 F按钮)',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `visible` CHAR(1) DEFAULT '0' COMMENT '是否显示(0显示 1隐藏)',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 5. 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 6. 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 7. 操作日志表（含补丁字段）
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '模块标题',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '方法名称',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    `oper_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    `oper_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `oper_ip` VARCHAR(50) DEFAULT NULL COMMENT '主机地址',
    `oper_location` VARCHAR(255) DEFAULT NULL COMMENT '操作地点',
    `oper_param` TEXT DEFAULT NULL COMMENT '请求参数',
    `json_result` TEXT DEFAULT NULL COMMENT '返回参数',
    `status` TINYINT DEFAULT 0 COMMENT '状态(0正常 1异常)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误消息',
    `oper_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time` BIGINT DEFAULT 0 COMMENT '消耗时间(毫秒)',
    PRIMARY KEY (`id`),
    KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 第二部分：基础档案模块
-- =============================================

-- 8. 商品分类表
DROP TABLE IF EXISTS `base_category`;
CREATE TABLE `base_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `category_code` VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `status` INT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 9. 商品标准单元表(SPU)
DROP TABLE IF EXISTS `base_product_spu`;
CREATE TABLE `base_product_spu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
    `spu_code` VARCHAR(50) NOT NULL COMMENT 'SPU编码',
    `spu_name` VARCHAR(100) NOT NULL COMMENT 'SPU名称',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `status` INT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spu_code` (`spu_code`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品标准单元表';

-- 10. 商品库存单元表(SKU)
DROP TABLE IF EXISTS `base_product_sku`;
CREATE TABLE `base_product_sku` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    `spu_id` BIGINT NOT NULL COMMENT 'SPU ID',
    `sku_code` VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    `sku_name` VARCHAR(100) NOT NULL COMMENT 'SKU名称',
    `spec_info` TEXT DEFAULT NULL COMMENT '规格信息(JSON格式)',
    `sale_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '销售价格',
    `cost_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价格',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    `safety_stock` INT DEFAULT 0 COMMENT '安全库存',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_spu_id` (`spu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品库存单元表';

-- 11. 仓库表
DROP TABLE IF EXISTS `base_warehouse`;
CREATE TABLE `base_warehouse` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
    `wh_code` VARCHAR(50) NOT NULL COMMENT '仓库编码',
    `wh_name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '仓库地址',
    `principal` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `area` VARCHAR(50) DEFAULT NULL COMMENT '仓库面积',
    `status` INT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wh_code` (`wh_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库表';

-- 12. 供应商表
DROP TABLE IF EXISTS `base_supplier`;
CREATE TABLE `base_supplier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
    `sup_code` VARCHAR(50) NOT NULL COMMENT '供应商编码',
    `sup_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '开户银行',
    `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    `status` INT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sup_code` (`sup_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- 13. 客户表
DROP TABLE IF EXISTS `base_customer`;
CREATE TABLE `base_customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    `cust_code` VARCHAR(50) NOT NULL COMMENT '客户编码',
    `cust_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `credit_limit` DECIMAL(12,2) DEFAULT 0.00 COMMENT '信用额度',
    `level` VARCHAR(20) DEFAULT 'normal' COMMENT '客户等级(normal普通/vip/strategic战略)',
    `status` INT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cust_code` (`cust_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- =============================================
-- 第三部分：核心业务模块
-- =============================================

-- 14. 实时库存表
DROP TABLE IF EXISTS `stock_main`;
CREATE TABLE `stock_main` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `quantity` DECIMAL(10,2) DEFAULT 0.00 COMMENT '库存数量',
    `frozen_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结数量',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '库位',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wh_sku_batch` (`warehouse_id`, `sku_id`, `batch_no`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实时库存表';

-- 15. 库存流水表
DROP TABLE IF EXISTS `stock_record`;
CREATE TABLE `stock_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '单据编号',
    `order_type` INT NOT NULL COMMENT '单据类型(1采购入库 2销售出库 3调拨入 4调拨出 5盘盈 6盘亏 7报损)',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `change_qty` DECIMAL(10,2) NOT NULL COMMENT '变动数量',
    `before_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '变动前数量',
    `after_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '变动后数量',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
    `customer_id` BIGINT DEFAULT NULL COMMENT '客户ID',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_order_type` (`order_type`),
    KEY `idx_warehouse_sku` (`warehouse_id`, `sku_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存流水表';

-- 16. 采购订单主表
DROP TABLE IF EXISTS `bus_purchase_order`;
CREATE TABLE `bus_purchase_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '采购订单ID',
    `purchase_no` VARCHAR(50) NOT NULL COMMENT '采购单号',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
    `status` INT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已入库 3已取消)',
    `applicant_id` BIGINT DEFAULT NULL COMMENT '申请人ID',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_purchase_no` (`purchase_no`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单主表';

-- 17. 采购订单明细表
DROP TABLE IF EXISTS `bus_purchase_item`;
CREATE TABLE `bus_purchase_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `purchase_id` BIGINT NOT NULL COMMENT '采购订单ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `sku_code` VARCHAR(50) DEFAULT NULL COMMENT 'SKU编码',
    `sku_name` VARCHAR(100) DEFAULT NULL COMMENT 'SKU名称',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    `quantity` DECIMAL(10,2) DEFAULT 0.00 COMMENT '数量',
    `total_price` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总价',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_purchase_id` (`purchase_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单明细表';

-- 18. 销售订单主表
DROP TABLE IF EXISTS `bus_sale_order`;
CREATE TABLE `bus_sale_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '销售订单ID',
    `sale_no` VARCHAR(50) NOT NULL COMMENT '销售单号',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
    `status` INT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已出库 3已取消)',
    `pay_status` INT DEFAULT 0 COMMENT '付款状态(0未付款 1部分付款 2已付款)',
    `deliver_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sale_no` (`sale_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售订单主表';

-- 19. 销售订单明细表
DROP TABLE IF EXISTS `bus_sale_item`;
CREATE TABLE `bus_sale_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `sale_id` BIGINT NOT NULL COMMENT '销售订单ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `sku_code` VARCHAR(50) DEFAULT NULL COMMENT 'SKU编码',
    `sku_name` VARCHAR(100) DEFAULT NULL COMMENT 'SKU名称',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    `quantity` DECIMAL(10,2) DEFAULT 0.00 COMMENT '数量',
    `total_price` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总价',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sale_id` (`sale_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售订单明细表';

-- 20. 库存盘点主表
DROP TABLE IF EXISTS `bus_stock_check`;
CREATE TABLE `bus_stock_check` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
    `check_no` VARCHAR(50) NOT NULL COMMENT '盘点单号',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `check_status` INT DEFAULT 0 COMMENT '盘点状态(0盘点中 1完成)',
    `check_time` DATETIME DEFAULT NULL COMMENT '盘点时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` INT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_check_no` (`check_no`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点主表';

-- 21. 盘点明细表
DROP TABLE IF EXISTS `bus_stock_check_item`;
CREATE TABLE `bus_stock_check_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点明细ID',
    `check_id` BIGINT NOT NULL COMMENT '盘点ID',
    `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
    `system_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '系统数量',
    `actual_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际数量',
    `diff_qty` DECIMAL(10,2) DEFAULT 0.00 COMMENT '差异数量',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_check_id` (`check_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盘点明细表';

-- =============================================
-- 第四部分：初始化系统数据
-- =============================================

-- 初始化部门数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `create_by`) VALUES
(1, 0, '总公司', 1, '管理员', '13800138000', 'admin@example.com', 'admin'),
(2, 1, '研发部', 1, '技术主管', '13800138001', 'rd@example.com', 'admin'),
(3, 1, '市场部', 2, '市场经理', '13800138002', 'market@example.com', 'admin'),
(4, 1, '采购部', 3, '采购经理', '13800138003', 'purchase@example.com', 'admin'),
(5, 1, '销售部', 4, '销售经理', '13800138004', 'sales@example.com', 'admin'),
(6, 1, '仓储部', 5, '仓储经理', '13800138005', 'warehouse@example.com', 'admin'),
(7, 1, '财务部', 6, '财务经理', '13800138006', 'finance@example.com', 'admin');

-- 初始化角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `data_scope`, `status`, `create_by`) VALUES
(1, '超级管理员', 'admin', '1', '0', 'admin'),
(2, '采购经理', 'purchase_manager', '3', '0', 'admin'),
(3, '销售经理', 'sale_manager', '3', '0', 'admin'),
(4, '仓储经理', 'warehouse_manager', '3', '0', 'admin'),
(5, '普通员工', 'employee', '5', '0', 'admin');

-- 初始化用户数据 (password: 123456)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `dept_id`, `status`, `create_by`) VALUES
(1, 'admin', '$2a$10$s/6K.oSU5QmSElA9YoRcneb.RhpBhmFMWOaFh1jtYg5e6NQkivm6K', '管理员', '13800138000', 'admin@example.com', 1, '0', 'admin');

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `path`, `component`, `perms`, `type`, `order_num`, `visible`, `status`, `icon`, `create_by`) VALUES
(1, 0, '系统管理', '/system', NULL, NULL, 'M', 1, '0', '0', 'system', 'admin'),
(2, 1, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 'C', 1, '0', '0', 'user', 'admin'),
(3, 1, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 'C', 2, '0', '0', 'peoples', 'admin'),
(4, 1, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 'C', 3, '0', '0', 'tree-table', 'admin'),
(5, 1, '部门管理', '/system/dept', 'system/dept/index', 'system:dept:list', 'C', 4, '0', '0', 'tree', 'admin'),
(10, 0, '基础资料', '/base', NULL, NULL, 'M', 2, '0', '0', 'shopping', 'admin'),
(11, 10, '产品管理', '/base/product', 'base/product/index', 'base:product:list', 'C', 1, '0', '0', 'shopping', 'admin'),
(12, 10, '仓库管理', '/base/warehouse', 'base/warehouse/index', 'base:warehouse:list', 'C', 2, '0', '0', 'excel', 'admin'),
(13, 10, '供应商管理', '/base/supplier', 'base/supplier/index', 'base:supplier:list', 'C', 3, '0', '0', 'peoples', 'admin'),
(14, 10, '客户管理', '/base/customer', 'base/customer/index', 'base:customer:list', 'C', 4, '0', '0', 'peoples', 'admin'),
(20, 0, '采购管理', '/purchase', NULL, NULL, 'M', 3, '0', '0', 'shopping-cart', 'admin'),
(21, 20, '采购订单', '/purchase/order', 'purchase/order/index', 'purchase:order:list', 'C', 1, '0', '0', 'form', 'admin'),
(30, 0, '销售管理', '/sale', NULL, NULL, 'M', 4, '0', '0', 'money', 'admin'),
(31, 30, '销售订单', '/sale/order', 'sale/order/index', 'sale:order:list', 'C', 1, '0', '0', 'form', 'admin'),
(40, 0, '库存管理', '/stock', NULL, NULL, 'M', 5, '0', '0', 'component', 'admin'),
(41, 40, '库存查询', '/stock/main', 'stock/main/index', 'stock:main:list', 'C', 1, '0', '0', 'list', 'admin'),
(42, 40, '库存流水', '/stock/record', 'stock/record/index', 'stock:record:list', 'C', 2, '0', '0', 'log', 'admin'),
(43, 40, '库存盘点', '/stock/check', 'stock/check/index', 'stock:check:list', 'C', 3, '0', '0', 'build', 'admin'),
(50, 0, '系统监控', '/monitor', NULL, NULL, 'M', 6, '0', '0', 'monitor', 'admin'),
(51, 50, '在线用户', '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'C', 1, '0', '0', 'online', 'admin'),
(52, 50, '操作日志', '/monitor/log', 'monitor/log/index', 'monitor:operlog:list', 'C', 2, '0', '0', 'log', 'admin');

-- 初始化角色菜单关联 (管理员拥有所有权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) SELECT 1, id FROM `sys_menu`;

-- =============================================
-- 第五部分：初始化业务基础数据
-- =============================================

-- 初始化商品分类
INSERT INTO `base_category` (`id`, `parent_id`, `category_code`, `category_name`, `order_num`, `status`, `create_by`) VALUES
(1, 0, 'ELEC', '电子产品', 1, 1, 'admin'),
(2, 0, 'DAILY', '日用品', 2, 1, 'admin'),
(3, 0, 'FOOD', '食品饮料', 3, 1, 'admin'),
(4, 0, 'CLOTH', '服装鞋帽', 4, 1, 'admin'),
(5, 0, 'HOME', '家居用品', 5, 1, 'admin'),
(6, 1, 'PHONE', '手机通讯', 1, 1, 'admin'),
(7, 1, 'COMPUTER', '电脑办公', 2, 1, 'admin'),
(8, 1, 'DIGITAL', '数码配件', 3, 1, 'admin'),
(9, 2, 'CLEAN', '清洁用品', 1, 1, 'admin'),
(10, 2, 'PERSONAL', '个人护理', 2, 1, 'admin');

-- 初始化仓库
INSERT INTO `base_warehouse` (`id`, `wh_code`, `wh_name`, `address`, `principal`, `status`, `create_by`) VALUES
(1, 'WH001', '主仓库', '北京市朝阳区建国路88号', '张三', 1, 'admin'),
(2, 'WH002', '分仓库', '北京市海淀区中关村大街66号', '李四', 1, 'admin'),
(3, 'WH003', '电商仓', '北京市通州区物流园路1号', '王五', 1, 'admin');

-- 初始化供应商
INSERT INTO `base_supplier` (`id`, `sup_code`, `sup_name`, `contact_person`, `phone`, `address`, `bank_account`, `create_by`) VALUES
(1, 'SUP001', '华为技术有限公司', '张经理', '13800138001', '深圳市龙岗区坂田华为基地', '6222021234567890001', 'admin'),
(2, 'SUP002', '小米科技有限公司', '李经理', '13800138002', '北京市海淀区清河中街68号', '6222021234567890002', 'admin'),
(3, 'SUP003', '联想集团有限公司', '王经理', '13800138003', '北京市海淀区上地创业路6号', '6222021234567890003', 'admin'),
(4, 'SUP004', '苹果贸易(上海)有限公司', '赵经理', '13800138004', '上海市浦东新区世纪大道88号', '6222021234567890004', 'admin'),
(5, 'SUP005', '蓝月亮集团有限公司', '孙经理', '13800138005', '广州市黄埔区科学城开源大道', '6222021234567890005', 'admin'),
(6, 'SUP006', '宝洁(中国)有限公司', '周经理', '13800138006', '广州市天河区林和中路', '6222021234567890006', 'admin');

-- 初始化客户
INSERT INTO `base_customer` (`id`, `cust_code`, `cust_name`, `contact_person`, `phone`, `address`, `create_by`) VALUES
(1, 'CUST001', '京东商城', '采购部张经理', '13900139001', '北京市亦庄经济技术开发区科创十一街', 'admin'),
(2, 'CUST002', '天猫超市', '采购部李经理', '13900139002', '杭州市余杭区文一西路969号', 'admin'),
(3, 'CUST003', '苏宁易购', '采购部王经理', '13900139003', '南京市玄武区苏宁大道1号', 'admin'),
(4, 'CUST004', '国美电器', '采购部赵经理', '13900139004', '北京市通州区怡乐中路', 'admin'),
(5, 'CUST005', '沃尔玛(中国)', '采购部孙经理', '13900139005', '深圳市福田区福华路', 'admin');

-- 初始化商品SPU
INSERT INTO `base_product_spu` (`id`, `spu_code`, `spu_name`, `category_id`, `brand`, `unit`, `status`, `description`, `create_by`) VALUES
(1, 'SPU001', '华为Mate 60 Pro智能手机', 6, '华为', '台', 1, '麒麟9000S芯片，北斗卫星消息，昆仑玻璃', 'admin'),
(2, 'SPU002', '小米14 Ultra智能手机', 6, '小米', '台', 1, '骁龙8 Gen3处理器，徕卡影像系统', 'admin'),
(3, 'SPU003', '苹果iPhone 15 Pro Max', 6, '苹果', '台', 1, 'A17 Pro芯片，钛金属设计', 'admin'),
(4, 'SPU004', 'ThinkPad X1 Carbon笔记本', 7, '联想', '台', 1, '14英寸商务轻薄本，Intel第13代处理器', 'admin'),
(5, 'SPU005', 'MacBook Pro 14英寸', 7, '苹果', '台', 1, 'M3 Pro芯片，Liquid Retina XDR显示屏', 'admin'),
(6, 'SPU006', 'AirPods Pro 2代', 8, '苹果', '副', 1, '主动降噪，自适应通透模式', 'admin'),
(7, 'SPU007', '华为FreeBuds Pro 3', 8, '华为', '副', 1, '智能降噪，高清音频', 'admin'),
(8, 'SPU008', '蓝月亮深层洁净洗衣液', 9, '蓝月亮', '瓶', 1, '深层洁净，温和不伤手', 'admin'),
(9, 'SPU009', '舒肤佳纯白清香沐浴露', 10, '宝洁', '瓶', 1, '持久清香，滋润肌肤', 'admin'),
(10, 'SPU010', '高露洁全效牙膏', 10, '高露洁', '支', 1, '全效保护，清新口气', 'admin');

-- 初始化商品SKU
INSERT INTO `base_product_sku` (`id`, `spu_id`, `sku_code`, `sku_name`, `spec_info`, `sale_price`, `cost_price`, `safety_stock`, `create_by`) VALUES
(1, 1, 'SKU001-BLK-256', '华为Mate 60 Pro 黑色 256GB', '{"颜色":"黑色","存储":"256GB"}', 6999.00, 5500.00, 50, 'admin'),
(2, 1, 'SKU001-BLK-512', '华为Mate 60 Pro 黑色 512GB', '{"颜色":"黑色","存储":"512GB"}', 7999.00, 6200.00, 30, 'admin'),
(3, 1, 'SKU001-WHT-256', '华为Mate 60 Pro 白色 256GB', '{"颜色":"白色","存储":"256GB"}', 6999.00, 5500.00, 50, 'admin'),
(4, 1, 'SKU001-WHT-512', '华为Mate 60 Pro 白色 512GB', '{"颜色":"白色","存储":"512GB"}', 7999.00, 6200.00, 30, 'admin'),
(5, 2, 'SKU002-BLK-256', '小米14 Ultra 黑色 256GB', '{"颜色":"黑色","存储":"256GB"}', 5999.00, 4800.00, 40, 'admin'),
(6, 2, 'SKU002-BLK-512', '小米14 Ultra 黑色 512GB', '{"颜色":"黑色","存储":"512GB"}', 6499.00, 5200.00, 30, 'admin'),
(7, 2, 'SKU002-WHT-256', '小米14 Ultra 白色 256GB', '{"颜色":"白色","存储":"256GB"}', 5999.00, 4800.00, 40, 'admin'),
(8, 3, 'SKU003-NAT-256', 'iPhone 15 Pro Max 原色钛金属 256GB', '{"颜色":"原色钛金属","存储":"256GB"}', 9999.00, 8000.00, 30, 'admin'),
(9, 3, 'SKU003-NAT-512', 'iPhone 15 Pro Max 原色钛金属 512GB', '{"颜色":"原色钛金属","存储":"512GB"}', 11999.00, 9600.00, 20, 'admin'),
(10, 3, 'SKU003-BLU-256', 'iPhone 15 Pro Max 蓝色钛金属 256GB', '{"颜色":"蓝色钛金属","存储":"256GB"}', 9999.00, 8000.00, 30, 'admin'),
(11, 4, 'SKU004-I7-16-512', 'ThinkPad X1 Carbon i7/16G/512G', '{"处理器":"i7-1365U","内存":"16GB","硬盘":"512GB"}', 13999.00, 11000.00, 20, 'admin'),
(12, 4, 'SKU004-I7-32-1T', 'ThinkPad X1 Carbon i7/32G/1T', '{"处理器":"i7-1365U","内存":"32GB","硬盘":"1TB"}', 16999.00, 13500.00, 10, 'admin'),
(13, 5, 'SKU005-M3P-18-512', 'MacBook Pro 14 M3 Pro/18G/512G', '{"芯片":"M3 Pro","内存":"18GB","硬盘":"512GB"}', 14999.00, 12500.00, 15, 'admin'),
(14, 5, 'SKU005-M3M-36-1T', 'MacBook Pro 14 M3 Max/36G/1T', '{"芯片":"M3 Max","内存":"36GB","硬盘":"1TB"}', 24999.00, 20000.00, 8, 'admin'),
(15, 6, 'SKU006-AP2', 'AirPods Pro 2代 标准版', '{"版本":"标准版"}', 1899.00, 1450.00, 100, 'admin'),
(16, 6, 'SKU006-AP2-MC', 'AirPods Pro 2代 MagSafe充电盒版', '{"版本":"MagSafe充电盒"}', 1999.00, 1520.00, 80, 'admin'),
(17, 7, 'SKU007-FB3-BLK', '华为FreeBuds Pro 3 黑色', '{"颜色":"黑色"}', 1499.00, 1100.00, 60, 'admin'),
(18, 7, 'SKU007-FB3-WHT', '华为FreeBuds Pro 3 白色', '{"颜色":"白色"}', 1499.00, 1100.00, 60, 'admin'),
(19, 8, 'SKU008-2L', '蓝月亮洗衣液 2L装', '{"规格":"2L"}', 39.90, 28.00, 200, 'admin'),
(20, 8, 'SKU008-3KG', '蓝月亮洗衣液 3KG装', '{"规格":"3KG"}', 49.90, 35.00, 150, 'admin'),
(21, 9, 'SKU009-400ML', '舒肤佳沐浴露 400ml', '{"规格":"400ml"}', 29.90, 20.00, 300, 'admin'),
(22, 9, 'SKU009-750ML', '舒肤佳沐浴露 750ml', '{"规格":"750ml"}', 45.90, 32.00, 200, 'admin'),
(23, 10, 'SKU010-120G', '高露洁全效牙膏 120g', '{"规格":"120g"}', 15.90, 10.00, 500, 'admin'),
(24, 10, 'SKU010-200G', '高露洁全效牙膏 200g', '{"规格":"200g"}', 22.90, 15.00, 400, 'admin');

-- 初始化库存数据（主仓库）
INSERT INTO `stock_main` (`warehouse_id`, `sku_id`, `quantity`, `frozen_qty`, `batch_no`, `position`) VALUES
(1, 1, 120.00, 10.00, 'B20240101001', 'A-01-01'),
(1, 2, 80.00, 5.00, 'B20240101001', 'A-01-02'),
(1, 3, 100.00, 8.00, 'B20240101001', 'A-01-03'),
(1, 4, 60.00, 3.00, 'B20240101001', 'A-01-04'),
(1, 5, 90.00, 6.00, 'B20240101002', 'A-02-01'),
(1, 6, 70.00, 4.00, 'B20240101002', 'A-02-02'),
(1, 7, 85.00, 5.00, 'B20240101002', 'A-02-03'),
(1, 8, 45.00, 2.00, 'B20240101003', 'A-03-01'),
(1, 9, 35.00, 1.00, 'B20240101003', 'A-03-02'),
(1, 10, 50.00, 3.00, 'B20240101003', 'A-03-03'),
(1, 11, 30.00, 2.00, 'B20240101004', 'B-01-01'),
(1, 12, 18.00, 1.00, 'B20240101004', 'B-01-02'),
(1, 13, 25.00, 2.00, 'B20240101005', 'B-02-01'),
(1, 14, 12.00, 0.00, 'B20240101005', 'B-02-02'),
(1, 15, 180.00, 15.00, 'B20240101006', 'C-01-01'),
(1, 16, 150.00, 12.00, 'B20240101006', 'C-01-02'),
(1, 17, 100.00, 8.00, 'B20240101007', 'C-02-01'),
(1, 18, 95.00, 7.00, 'B20240101007', 'C-02-02'),
(1, 19, 350.00, 25.00, 'B20240101008', 'D-01-01'),
(1, 20, 280.00, 20.00, 'B20240101008', 'D-01-02'),
(1, 21, 450.00, 30.00, 'B20240101009', 'D-02-01'),
(1, 22, 320.00, 22.00, 'B20240101009', 'D-02-02'),
(1, 23, 800.00, 50.00, 'B20240101010', 'D-03-01'),
(1, 24, 600.00, 40.00, 'B20240101010', 'D-03-02');

-- 初始化库存数据（分仓库）
INSERT INTO `stock_main` (`warehouse_id`, `sku_id`, `quantity`, `frozen_qty`, `batch_no`, `position`) VALUES
(2, 1, 60.00, 5.00, 'B20240102001', 'A-01-01'),
(2, 2, 40.00, 3.00, 'B20240102001', 'A-01-02'),
(2, 5, 45.00, 4.00, 'B20240102002', 'A-02-01'),
(2, 8, 25.00, 2.00, 'B20240102003', 'A-03-01'),
(2, 15, 100.00, 8.00, 'B20240102004', 'B-01-01'),
(2, 19, 200.00, 15.00, 'B20240102005', 'C-01-01'),
(2, 21, 250.00, 20.00, 'B20240102006', 'C-02-01'),
(2, 23, 400.00, 30.00, 'B20240102007', 'D-01-01');

-- 初始化采购订单
INSERT INTO `bus_purchase_order` (`id`, `purchase_no`, `supplier_id`, `total_amount`, `status`, `applicant_id`, `remark`, `create_by`) VALUES
(1, 'PO202401010001', 1, 359940.00, 2, 1, '首批进货-华为手机', 'admin'),
(2, 'PO202401020001', 2, 239960.00, 2, 1, '首批进货-小米手机', 'admin'),
(3, 'PO202401030001', 5, 15980.00, 1, 1, '日用品补货', 'admin'),
(4, 'PO202401040001', 4, 59994.00, 0, 1, '苹果产品采购', 'admin');

-- 初始化采购订单明细
INSERT INTO `bus_purchase_item` (`purchase_id`, `sku_id`, `sku_code`, `sku_name`, `price`, `quantity`, `total_price`) VALUES
(1, 1, 'SKU001-BLK-256', '华为Mate 60 Pro 黑色 256GB', 5500.00, 30, 165000.00),
(1, 2, 'SKU001-BLK-512', '华为Mate 60 Pro 黑色 512GB', 6200.00, 20, 124000.00),
(1, 3, 'SKU001-WHT-256', '华为Mate 60 Pro 白色 256GB', 5500.00, 10, 55000.00),
(1, 17, 'SKU007-FB3-BLK', '华为FreeBuds Pro 3 黑色', 1100.00, 12, 13200.00),
(1, 18, 'SKU007-FB3-WHT', '华为FreeBuds Pro 3 白色', 1100.00, 8, 8800.00),
(2, 5, 'SKU002-BLK-256', '小米14 Ultra 黑色 256GB', 4800.00, 25, 120000.00),
(2, 6, 'SKU002-BLK-512', '小米14 Ultra 黑色 512GB', 5200.00, 15, 78000.00),
(2, 7, 'SKU002-WHT-256', '小米14 Ultra 白色 256GB', 4800.00, 8, 38400.00),
(2, 17, 'SKU007-FB3-BLK', '华为FreeBuds Pro 3 黑色', 1100.00, 3, 3300.00),
(2, 18, 'SKU007-FB3-WHT', '华为FreeBuds Pro 3 白色', 1100.00, 2, 2200.00),
(3, 19, 'SKU008-2L', '蓝月亮洗衣液 2L装', 28.00, 300, 8400.00),
(3, 21, 'SKU009-400ML', '舒肤佳沐浴露 400ml', 20.00, 350, 7000.00),
(3, 23, 'SKU010-120G', '高露洁全效牙膏 120g', 10.00, 58, 580.00),
(4, 8, 'SKU003-NAT-256', 'iPhone 15 Pro Max 原色钛金属 256GB', 8000.00, 5, 40000.00),
(4, 15, 'SKU006-AP2', 'AirPods Pro 2代 标准版', 1450.00, 10, 14500.00),
(4, 16, 'SKU006-AP2-MC', 'AirPods Pro 2代 MagSafe充电盒版', 1520.00, 3, 4560.00);

-- 初始化销售订单
INSERT INTO `bus_sale_order` (`id`, `sale_no`, `customer_id`, `total_amount`, `status`, `pay_status`, `remark`, `create_by`) VALUES
(1, 'SO202401050001', 1, 209970.00, 2, 2, '京东首批订单', 'admin'),
(2, 'SO202401060001', 2, 59990.00, 1, 1, '天猫订单', 'admin'),
(3, 'SO202401070001', 3, 8990.00, 0, 0, '苏宁小批量订单', 'admin');

-- 初始化销售订单明细
INSERT INTO `bus_sale_item` (`sale_id`, `sku_id`, `sku_code`, `sku_name`, `price`, `quantity`, `total_price`) VALUES
(1, 1, 'SKU001-BLK-256', '华为Mate 60 Pro 黑色 256GB', 6999.00, 20, 139980.00),
(1, 17, 'SKU007-FB3-BLK', '华为FreeBuds Pro 3 黑色', 1499.00, 30, 44970.00),
(1, 18, 'SKU007-FB3-WHT', '华为FreeBuds Pro 3 白色', 1499.00, 17, 25483.00),
(2, 5, 'SKU002-BLK-256', '小米14 Ultra 黑色 256GB', 5999.00, 10, 59990.00),
(3, 15, 'SKU006-AP2', 'AirPods Pro 2代 标准版', 1899.00, 3, 5697.00),
(3, 19, 'SKU008-2L', '蓝月亮洗衣液 2L装', 39.90, 50, 1995.00),
(3, 23, 'SKU010-120G', '高露洁全效牙膏 120g', 15.90, 82, 1303.80);

-- 初始化库存流水记录
INSERT INTO `stock_record` (`order_no`, `order_type`, `warehouse_id`, `sku_id`, `change_qty`, `before_qty`, `after_qty`, `batch_no`, `supplier_id`, `operator_id`, `remark`) VALUES
('PO202401010001', 1, 1, 1, 30.00, 0.00, 30.00, 'B20240101001', 1, 1, '采购入库'),
('PO202401010001', 1, 1, 2, 20.00, 0.00, 20.00, 'B20240101001', 1, 1, '采购入库'),
('PO202401010001', 1, 1, 3, 10.00, 0.00, 10.00, 'B20240101001', 1, 1, '采购入库'),
('PO202401020001', 1, 1, 5, 25.00, 0.00, 25.00, 'B20240101002', 2, 1, '采购入库'),
('PO202401020001', 1, 1, 6, 15.00, 0.00, 15.00, 'B20240101002', 2, 1, '采购入库'),
('PO202401030001', 1, 1, 19, 300.00, 0.00, 300.00, 'B20240101008', 5, 1, '采购入库'),
('PO202401030001', 1, 1, 21, 350.00, 0.00, 350.00, 'B20240101009', 5, 1, '采购入库'),
('SO202401050001', 2, 1, 1, -10.00, 30.00, 20.00, 'B20240101001', NULL, 1, '销售出库'),
('SO202401050001', 2, 1, 17, -15.00, 20.00, 5.00, 'B20240101007', NULL, 1, '销售出库'),
('SO202401050001', 2, 1, 19, -50.00, 300.00, 250.00, 'B20240101008', NULL, 1, '销售出库');

-- =============================================
-- 初始化完成提示
-- =============================================
SELECT '========================================' AS '';
SELECT '数据库初始化完成！' AS '状态';
SELECT '========================================' AS '';
SELECT CONCAT('用户: ', username, ' 密码: 123456') AS '管理员账号' FROM sys_user WHERE id = 1;
SELECT CONCAT('商品SPU数量: ', COUNT(*)) AS 'SPU统计' FROM base_product_spu;
SELECT CONCAT('商品SKU数量: ', COUNT(*)) AS 'SKU统计' FROM base_product_sku;
SELECT CONCAT('库存记录数量: ', COUNT(*)) AS '库存统计' FROM stock_main;
SELECT CONCAT('供应商数量: ', COUNT(*)) AS '供应商统计' FROM base_supplier;
SELECT CONCAT('客户数量: ', COUNT(*)) AS '客户统计' FROM base_customer;
SELECT CONCAT('采购订单数量: ', COUNT(*)) AS '采购订单统计' FROM bus_purchase_order;
SELECT CONCAT('销售订单数量: ', COUNT(*)) AS '销售订单统计' FROM bus_sale_order;
