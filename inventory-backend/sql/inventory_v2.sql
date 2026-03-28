-- =============================================
-- 库存管理系统数据库脚本
-- 数据库名: inventory_v2
-- MySQL版本: 8.0.33
-- 字符集: utf8mb4
-- 创建时间: 2024-01-01
-- =============================================

-- 设置字符集
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
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 3. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `type` TINYINT DEFAULT 0 COMMENT '类型(0目录 1菜单 2按钮)',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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

-- 7. 操作日志表
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '模块标题',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '方法名称',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    `oper_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    `oper_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `oper_ip` VARCHAR(50) DEFAULT NULL COMMENT '主机地址',
    `oper_param` TEXT DEFAULT NULL COMMENT '请求参数',
    `json_result` TEXT DEFAULT NULL COMMENT '返回参数',
    `status` TINYINT DEFAULT 0 COMMENT '状态(0正常 1异常)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误消息',
    `oper_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
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
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `spec_info` JSON DEFAULT NULL COMMENT '规格信息(JSON格式)',
    `sale_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '销售价格',
    `cost_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价格',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    `safety_stock` INT DEFAULT 0 COMMENT '安全库存',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `order_type` TINYINT NOT NULL COMMENT '单据类型(1采购入库 2销售出库 3调拨入 4调拨出 5盘盈 6盘亏 7报损)',
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
    `status` TINYINT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已入库 3已取消)',
    `applicant_id` BIGINT DEFAULT NULL COMMENT '申请人ID',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `status` TINYINT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已出库 3已取消)',
    `pay_status` TINYINT DEFAULT 0 COMMENT '付款状态(0未付款 1部分付款 2已付款)',
    `deliver_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
    `check_status` TINYINT DEFAULT 0 COMMENT '盘点状态(0盘点中 1完成)',
    `check_time` DATETIME DEFAULT NULL COMMENT '盘点时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
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
-- 第四部分：初始化数据
-- =============================================

-- 初始化部门数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `order_num`, `create_by`) VALUES
(1, 0, 'Headquarters', 1, 1),
(2, 1, 'R&D Dept', 1, 1),
(3, 1, 'Marketing Dept', 2, 1),
(4, 1, 'Purchasing Dept', 3, 1),
(5, 1, 'Sales Dept', 4, 1),
(6, 1, 'Warehouse Dept', 5, 1),
(7, 1, 'Finance Dept', 6, 1);

-- 初始化角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `data_scope`, `status`, `create_by`) VALUES
(1, 'Administrator', 'admin', 1, 1, 1),
(2, 'Purchase Manager', 'purchase_manager', 3, 1, 1),
(3, 'Sales Manager', 'sale_manager', 3, 1, 1),
(4, 'Warehouse Manager', 'warehouse_manager', 3, 1, 1),
(5, 'Employee', 'employee', 5, 1, 1);

-- 初始化用户数据 (password: 123456)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `dept_id`, `status`, `create_by`) VALUES
(1, 'admin', '$2a$10$s/6K.oSU5QmSElA9YoRcneb.RhpBhmFMWOaFh1jtYg5e6NQkivm6K', 'Admin', '13800138000', 'admin@example.com', 1, 1, 1);

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 初始化菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `path`, `component`, `perms`, `type`, `order_num`, `status`, `create_by`) VALUES
(1, 0, 'System', '/system', NULL, NULL, 0, 1, 1, 1),
(2, 1, 'User Management', '/system/user', 'system/user/index', 'system:user:list', 1, 1, 1, 1),
(3, 1, 'Role Management', '/system/role', 'system/role/index', 'system:role:list', 1, 2, 1, 1),
(4, 1, 'Menu Management', '/system/menu', 'system/menu/index', 'system:menu:list', 1, 3, 1, 1),
(5, 1, 'Dept Management', '/system/dept', 'system/dept/index', 'system:dept:list', 1, 4, 1, 1),
(6, 1, 'Operation Log', '/system/log', 'system/log/index', 'system:log:list', 1, 5, 1, 1),
(10, 0, 'Base Data', '/base', NULL, NULL, 0, 2, 1, 1),
(11, 10, 'Product Management', '/base/product', 'base/product/index', 'base:product:list', 1, 1, 1, 1),
(12, 10, 'Warehouse Management', '/base/warehouse', 'base/warehouse/index', 'base:warehouse:list', 1, 2, 1, 1),
(13, 10, 'Supplier Management', '/base/supplier', 'base/supplier/index', 'base:supplier:list', 1, 3, 1, 1),
(14, 10, 'Customer Management', '/base/customer', 'base/customer/index', 'base:customer:list', 1, 4, 1, 1),
(20, 0, 'Purchase Management', '/purchase', NULL, NULL, 0, 3, 1, 1),
(21, 20, 'Purchase Order', '/purchase/order', 'purchase/order/index', 'purchase:order:list', 1, 1, 1, 1),
(22, 20, 'Purchase Inbound', '/purchase/inbound', 'purchase/inbound/index', 'purchase:inbound:list', 1, 2, 1, 1),
(30, 0, 'Sales Management', '/sale', NULL, NULL, 0, 4, 1, 1),
(31, 30, 'Sales Order', '/sale/order', 'sale/order/index', 'sale:order:list', 1, 1, 1, 1),
(32, 30, 'Sales Outbound', '/sale/outbound', 'sale/outbound/index', 'sale:outbound:list', 1, 2, 1, 1),
(40, 0, 'Inventory Management', '/stock', NULL, NULL, 0, 5, 1, 1),
(41, 40, 'Real-time Inventory', '/stock/main', 'stock/main/index', 'stock:main:list', 1, 1, 1, 1),
(42, 40, 'Inventory Record', '/stock/record', 'stock/record/index', 'stock:record:list', 1, 2, 1, 1),
(43, 40, 'Inventory Check', '/stock/check', 'stock/check/index', 'stock:check:list', 1, 3, 1, 1),
(44, 40, 'Inventory Transfer', '/stock/transfer', 'stock/transfer/index', 'stock:transfer:list', 1, 4, 1, 1),
(50, 0, 'System Monitor', '/monitor', NULL, NULL, 0, 6, 1, 1),
(51, 50, 'Online Users', '/monitor/online', 'monitor/online/index', 'monitor:online:list', 1, 1, 1, 1),
(52, 50, 'Server Monitor', '/monitor/server', 'monitor/server/index', 'monitor:server:list', 1, 2, 1, 1),
(53, 50, 'Cache Monitor', '/monitor/cache', 'monitor/cache/index', 'monitor:cache:list', 1, 3, 1, 1);

-- 初始化按钮权限
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `path`, `component`, `perms`, `type`, `order_num`, `status`, `create_by`) VALUES
(100, 2, 'User Query', NULL, NULL, 'system:user:query', 2, 1, 1, 1),
(101, 2, 'User Add', NULL, NULL, 'system:user:add', 2, 2, 1, 1),
(102, 2, 'User Edit', NULL, NULL, 'system:user:edit', 2, 3, 1, 1),
(103, 2, 'User Delete', NULL, NULL, 'system:user:remove', 2, 4, 1, 1),
(104, 2, 'Reset Password', NULL, NULL, 'system:user:resetPwd', 2, 5, 1, 1),
(105, 2, 'User Export', NULL, NULL, 'system:user:export', 2, 6, 1, 1),
(110, 3, 'Role Query', NULL, NULL, 'system:role:query', 2, 1, 1, 1),
(111, 3, 'Role Add', NULL, NULL, 'system:role:add', 2, 2, 1, 1),
(112, 3, 'Role Edit', NULL, NULL, 'system:role:edit', 2, 3, 1, 1),
(113, 3, 'Role Delete', NULL, NULL, 'system:role:remove', 2, 4, 1, 1),
(120, 4, 'Menu Query', NULL, NULL, 'system:menu:query', 2, 1, 1, 1),
(121, 4, 'Menu Add', NULL, NULL, 'system:menu:add', 2, 2, 1, 1),
(122, 4, 'Menu Edit', NULL, NULL, 'system:menu:edit', 2, 3, 1, 1),
(123, 4, 'Menu Delete', NULL, NULL, 'system:menu:remove', 2, 4, 1, 1),
(130, 5, 'Dept Query', NULL, NULL, 'system:dept:query', 2, 1, 1, 1),
(131, 5, 'Dept Add', NULL, NULL, 'system:dept:add', 2, 2, 1, 1),
(132, 5, 'Dept Edit', NULL, NULL, 'system:dept:edit', 2, 3, 1, 1),
(133, 5, 'Dept Delete', NULL, NULL, 'system:dept:remove', 2, 4, 1, 1),
(140, 11, 'Product Query', NULL, NULL, 'base:product:query', 2, 1, 1, 1),
(141, 11, 'Product Add', NULL, NULL, 'base:product:add', 2, 2, 1, 1),
(142, 11, 'Product Edit', NULL, NULL, 'base:product:edit', 2, 3, 1, 1),
(143, 11, 'Product Delete', NULL, NULL, 'base:product:remove', 2, 4, 1, 1),
(150, 12, 'Warehouse Query', NULL, NULL, 'base:warehouse:query', 2, 1, 1, 1),
(151, 12, 'Warehouse Add', NULL, NULL, 'base:warehouse:add', 2, 2, 1, 1),
(152, 12, 'Warehouse Edit', NULL, NULL, 'base:warehouse:edit', 2, 3, 1, 1),
(153, 12, 'Warehouse Delete', NULL, NULL, 'base:warehouse:remove', 2, 4, 1, 1),
(160, 13, 'Supplier Query', NULL, NULL, 'base:supplier:query', 2, 1, 1, 1),
(161, 13, 'Supplier Add', NULL, NULL, 'base:supplier:add', 2, 2, 1, 1),
(162, 13, 'Supplier Edit', NULL, NULL, 'base:supplier:edit', 2, 3, 1, 1),
(163, 13, 'Supplier Delete', NULL, NULL, 'base:supplier:remove', 2, 4, 1, 1),
(170, 14, 'Customer Query', NULL, NULL, 'base:customer:query', 2, 1, 1, 1),
(171, 14, 'Customer Add', NULL, NULL, 'base:customer:add', 2, 2, 1, 1),
(172, 14, 'Customer Edit', NULL, NULL, 'base:customer:edit', 2, 3, 1, 1),
(173, 14, 'Customer Delete', NULL, NULL, 'base:customer:remove', 2, 4, 1, 1),
(180, 21, 'Purchase Order Query', NULL, NULL, 'purchase:order:query', 2, 1, 1, 1),
(181, 21, 'Purchase Order Add', NULL, NULL, 'purchase:order:add', 2, 2, 1, 1),
(182, 21, 'Purchase Order Edit', NULL, NULL, 'purchase:order:edit', 2, 3, 1, 1),
(183, 21, 'Purchase Order Delete', NULL, NULL, 'purchase:order:remove', 2, 4, 1, 1),
(184, 21, 'Purchase Order Audit', NULL, NULL, 'purchase:order:audit', 2, 5, 1, 1),
(190, 31, 'Sales Order Query', NULL, NULL, 'sale:order:query', 2, 1, 1, 1),
(191, 31, 'Sales Order Add', NULL, NULL, 'sale:order:add', 2, 2, 1, 1),
(192, 31, 'Sales Order Edit', NULL, NULL, 'sale:order:edit', 2, 3, 1, 1),
(193, 31, 'Sales Order Delete', NULL, NULL, 'sale:order:remove', 2, 4, 1, 1),
(194, 31, 'Sales Order Audit', NULL, NULL, 'sale:order:audit', 2, 5, 1, 1),
(200, 43, 'Check Query', NULL, NULL, 'stock:check:query', 2, 1, 1, 1),
(201, 43, 'Check Add', NULL, NULL, 'stock:check:add', 2, 2, 1, 1),
(202, 43, 'Check Edit', NULL, NULL, 'stock:check:edit', 2, 3, 1, 1),
(203, 43, 'Check Delete', NULL, NULL, 'stock:check:remove', 2, 4, 1, 1),
(204, 43, 'Check Complete', NULL, NULL, 'stock:check:complete', 2, 5, 1, 1);

-- 初始化角色菜单关联 (管理员拥有所有权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- 初始化商品分类
INSERT INTO `base_category` (`id`, `parent_id`, `category_name`, `order_num`, `status`, `create_by`) VALUES
(1, 0, 'Electronics', 1, 1, 1),
(2, 0, 'Clothing', 2, 1, 1),
(3, 0, 'Food', 3, 1, 1),
(4, 1, 'Mobile Phones', 1, 1, 1),
(5, 1, 'Computers', 2, 1, 1),
(6, 2, 'Mens Wear', 1, 1, 1),
(7, 2, 'Womens Wear', 2, 1, 1);

-- 初始化仓库
INSERT INTO `base_warehouse` (`id`, `wh_code`, `wh_name`, `address`, `principal`, `status`, `create_by`) VALUES
(1, 'WH001', 'Main Warehouse', '123 Main Street', 'John Doe', 1, 1),
(2, 'WH002', 'Branch Warehouse', '456 Branch Avenue', 'Jane Smith', 1, 1);

-- 初始化供应商
INSERT INTO `base_supplier` (`id`, `sup_code`, `sup_name`, `contact_person`, `phone`, `address`, `create_by`) VALUES
(1, 'SUP001', 'Tech Supplier Co.', 'Mike Johnson', '13900001111', '789 Tech Park', 1),
(2, 'SUP002', 'Global Trade Ltd.', 'Sarah Wilson', '13900002222', '101 Trade Center', 1);

-- 初始化客户
INSERT INTO `base_customer` (`id`, `cust_code`, `cust_name`, `contact_person`, `phone`, `address`, `create_by`) VALUES
(1, 'CUST001', 'Big Mart Inc.', 'Tom Brown', '13800001111', '202 Retail Street', 1),
(2, 'CUST002', 'Online Store Ltd.', 'Lisa Davis', '13800002222', '303 Commerce Ave', 1);

-- =============================================
-- 脚本执行完成
-- =============================================
