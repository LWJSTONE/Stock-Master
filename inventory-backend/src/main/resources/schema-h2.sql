-- =============================================
-- 库存管理系统数据库脚本 (H2兼容版本)
-- 数据库名: inventory_v2
-- =============================================

-- 第一部分：系统权限模块 (RBAC模型)

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    status VARCHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    del_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_dept_id ON sys_user(dept_id);
CREATE INDEX IF NOT EXISTS idx_status ON sys_user(status);

-- 2. 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    leader VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    status VARCHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    del_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_parent_id ON sys_dept(parent_id);

-- 3. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色权限字符',
    data_scope VARCHAR(1) DEFAULT '1' COMMENT '数据范围',
    status VARCHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    del_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_role_key ON sys_role(role_key);

-- 4. 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    component VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    perms VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    type VARCHAR(1) DEFAULT 'M' COMMENT '类型(M目录 C菜单 F按钮)',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    visible VARCHAR(1) DEFAULT '0' COMMENT '是否显示(0显示 1隐藏)',
    status VARCHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    icon VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    del_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_parent_id_menu ON sys_menu(parent_id);

-- 5. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_role_id ON sys_user_role(role_id);

-- 6. 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
);

CREATE INDEX IF NOT EXISTS idx_menu_id ON sys_role_menu(menu_id);

-- 7. 操作日志表
CREATE TABLE IF NOT EXISTS sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    title VARCHAR(50) DEFAULT NULL COMMENT '模块标题',
    method VARCHAR(200) DEFAULT NULL COMMENT '方法名称',
    request_method VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
    oper_name VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    oper_url VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    oper_ip VARCHAR(50) DEFAULT NULL COMMENT '主机地址',
    oper_param CLOB DEFAULT NULL COMMENT '请求参数',
    json_result CLOB DEFAULT NULL COMMENT '返回参数',
    status TINYINT DEFAULT 0 COMMENT '状态(0正常 1异常)',
    error_msg CLOB DEFAULT NULL COMMENT '错误消息',
    oper_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_oper_time ON sys_oper_log(oper_time);

-- 第二部分：基础档案模块

-- 8. 商品分类表
CREATE TABLE IF NOT EXISTS base_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_code VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_parent_id_cat ON base_category(parent_id);

-- 9. 商品标准单元表(SPU)
CREATE TABLE IF NOT EXISTS base_product_spu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
    spu_code VARCHAR(50) NOT NULL COMMENT 'SPU编码',
    spu_name VARCHAR(100) NOT NULL COMMENT 'SPU名称',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    brand VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_spu_code ON base_product_spu(spu_code);
CREATE INDEX IF NOT EXISTS idx_category_id ON base_product_spu(category_id);

-- 10. 商品库存单元表(SKU)
CREATE TABLE IF NOT EXISTS base_product_sku (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    spu_id BIGINT NOT NULL COMMENT 'SPU ID',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    sku_name VARCHAR(100) NOT NULL COMMENT 'SKU名称',
    spec_info VARCHAR(500) DEFAULT NULL COMMENT '规格信息(JSON格式)',
    sale_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '销售价格',
    cost_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价格',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    safety_stock INT DEFAULT 0 COMMENT '安全库存',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sku_code ON base_product_sku(sku_code);
CREATE INDEX IF NOT EXISTS idx_spu_id ON base_product_sku(spu_id);

-- 11. 仓库表
CREATE TABLE IF NOT EXISTS base_warehouse (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
    wh_code VARCHAR(50) NOT NULL COMMENT '仓库编码',
    wh_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    address VARCHAR(200) DEFAULT NULL COMMENT '仓库地址',
    principal VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_wh_code ON base_warehouse(wh_code);

-- 12. 供应商表
CREATE TABLE IF NOT EXISTS base_supplier (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
    sup_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    sup_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    bank_account VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sup_code ON base_supplier(sup_code);

-- 13. 客户表
CREATE TABLE IF NOT EXISTS base_customer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    cust_code VARCHAR(50) NOT NULL COMMENT '客户编码',
    cust_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_cust_code ON base_customer(cust_code);

-- 第三部分：核心业务模块

-- 14. 实时库存表
CREATE TABLE IF NOT EXISTS stock_main (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    quantity DECIMAL(10,2) DEFAULT 0.00 COMMENT '库存数量',
    frozen_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '冻结数量',
    batch_no VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    position VARCHAR(50) DEFAULT NULL COMMENT '库位',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_wh_sku_batch ON stock_main(warehouse_id, sku_id, batch_no);
CREATE INDEX IF NOT EXISTS idx_sku_id ON stock_main(sku_id);
CREATE INDEX IF NOT EXISTS idx_batch_no ON stock_main(batch_no);

-- 15. 库存流水表
CREATE TABLE IF NOT EXISTS stock_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    order_no VARCHAR(50) NOT NULL COMMENT '单据编号',
    order_type TINYINT NOT NULL COMMENT '单据类型',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    change_qty DECIMAL(10,2) NOT NULL COMMENT '变动数量',
    before_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '变动前数量',
    after_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '变动后数量',
    batch_no VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
    customer_id BIGINT DEFAULT NULL COMMENT '客户ID',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_order_no ON stock_record(order_no);
CREATE INDEX IF NOT EXISTS idx_order_type ON stock_record(order_type);
CREATE INDEX IF NOT EXISTS idx_warehouse_sku ON stock_record(warehouse_id, sku_id);
CREATE INDEX IF NOT EXISTS idx_create_time ON stock_record(create_time);

-- 16. 采购订单主表
CREATE TABLE IF NOT EXISTS bus_purchase_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '采购订单ID',
    purchase_no VARCHAR(50) NOT NULL COMMENT '采购单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    total_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已入库 3已取消)',
    applicant_id BIGINT DEFAULT NULL COMMENT '申请人ID',
    audit_time TIMESTAMP DEFAULT NULL COMMENT '审核时间',
    audit_by BIGINT DEFAULT NULL COMMENT '审核人',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_purchase_no ON bus_purchase_order(purchase_no);
CREATE INDEX IF NOT EXISTS idx_supplier_id ON bus_purchase_order(supplier_id);
CREATE INDEX IF NOT EXISTS idx_status_po ON bus_purchase_order(status);

-- 17. 采购订单明细表
CREATE TABLE IF NOT EXISTS bus_purchase_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    purchase_id BIGINT NOT NULL COMMENT '采购订单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_code VARCHAR(50) DEFAULT NULL COMMENT 'SKU编码',
    sku_name VARCHAR(100) DEFAULT NULL COMMENT 'SKU名称',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    quantity DECIMAL(10,2) DEFAULT 0.00 COMMENT '数量',
    total_price DECIMAL(12,2) DEFAULT 0.00 COMMENT '总价',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_purchase_id ON bus_purchase_item(purchase_id);
CREATE INDEX IF NOT EXISTS idx_sku_id_pi ON bus_purchase_item(sku_id);

-- 18. 销售订单主表
CREATE TABLE IF NOT EXISTS bus_sale_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '销售订单ID',
    sale_no VARCHAR(50) NOT NULL COMMENT '销售单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    total_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态(0待审核 1已审核 2已出库 3已取消)',
    pay_status TINYINT DEFAULT 0 COMMENT '付款状态(0未付款 1部分付款 2已付款)',
    deliver_time TIMESTAMP DEFAULT NULL COMMENT '发货时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sale_no ON bus_sale_order(sale_no);
CREATE INDEX IF NOT EXISTS idx_customer_id ON bus_sale_order(customer_id);
CREATE INDEX IF NOT EXISTS idx_status_so ON bus_sale_order(status);

-- 19. 销售订单明细表
CREATE TABLE IF NOT EXISTS bus_sale_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    sale_id BIGINT NOT NULL COMMENT '销售订单ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    sku_code VARCHAR(50) DEFAULT NULL COMMENT 'SKU编码',
    sku_name VARCHAR(100) DEFAULT NULL COMMENT 'SKU名称',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    quantity DECIMAL(10,2) DEFAULT 0.00 COMMENT '数量',
    total_price DECIMAL(12,2) DEFAULT 0.00 COMMENT '总价',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_sale_id ON bus_sale_item(sale_id);
CREATE INDEX IF NOT EXISTS idx_sku_id_si ON bus_sale_item(sku_id);

-- 20. 库存盘点主表
CREATE TABLE IF NOT EXISTS bus_stock_check (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
    check_no VARCHAR(50) NOT NULL COMMENT '盘点单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    check_status TINYINT DEFAULT 0 COMMENT '盘点状态(0盘点中 1完成)',
    check_time TIMESTAMP DEFAULT NULL COMMENT '盘点时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT DEFAULT NULL COMMENT '创建人',
    update_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '删除标志(0未删除 1已删除)',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_check_no ON bus_stock_check(check_no);
CREATE INDEX IF NOT EXISTS idx_warehouse_id_bsc ON bus_stock_check(warehouse_id);

-- 21. 盘点明细表
CREATE TABLE IF NOT EXISTS bus_stock_check_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点明细ID',
    check_id BIGINT NOT NULL COMMENT '盘点ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    system_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '系统数量',
    actual_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际数量',
    diff_qty DECIMAL(10,2) DEFAULT 0.00 COMMENT '差异数量',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_check_id ON bus_stock_check_item(check_id);
CREATE INDEX IF NOT EXISTS idx_sku_id_sci ON bus_stock_check_item(sku_id);
