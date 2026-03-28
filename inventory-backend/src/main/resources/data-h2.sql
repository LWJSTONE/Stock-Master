-- =============================================
-- 初始化数据 (H2兼容版本)
-- =============================================

-- 初始化部门数据
INSERT INTO sys_dept (id, parent_id, dept_name, order_num, create_by) VALUES
(1, 0, 'Headquarters', 1, 1),
(2, 1, 'R&D Dept', 1, 1),
(3, 1, 'Marketing Dept', 2, 1),
(4, 1, 'Purchasing Dept', 3, 1),
(5, 1, 'Sales Dept', 4, 1),
(6, 1, 'Warehouse Dept', 5, 1),
(7, 1, 'Finance Dept', 6, 1);

-- 初始化角色数据
INSERT INTO sys_role (id, role_name, role_key, data_scope, status, create_by) VALUES
(1, 'Administrator', 'admin', 1, 1, 1),
(2, 'Purchase Manager', 'purchase_manager', 3, 1, 1),
(3, 'Sales Manager', 'sale_manager', 3, 1, 1),
(4, 'Warehouse Manager', 'warehouse_manager', 3, 1, 1),
(5, 'Employee', 'employee', 5, 1, 1);

-- 初始化用户数据 (password: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, dept_id, status, create_by) VALUES
(1, 'admin', '$2a$10$s/6K.oSU5QmSElA9YoRcneb.RhpBhmFMWOaFh1jtYg5e6NQkivm6K', 'Admin', '13800138000', 'admin@example.com', 1, 1, 1);

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- 初始化菜单数据
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, type, order_num, status, create_by) VALUES
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
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, type, order_num, status, create_by) VALUES
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
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 初始化商品分类
INSERT INTO base_category (id, parent_id, category_name, order_num, status, create_by) VALUES
(1, 0, 'Electronics', 1, 1, 1),
(2, 0, 'Clothing', 2, 1, 1),
(3, 0, 'Food', 3, 1, 1),
(4, 1, 'Mobile Phones', 1, 1, 1),
(5, 1, 'Computers', 2, 1, 1),
(6, 2, 'Mens Wear', 1, 1, 1),
(7, 2, 'Womens Wear', 2, 1, 1);

-- 初始化仓库
INSERT INTO base_warehouse (id, wh_code, wh_name, address, principal, status, create_by) VALUES
(1, 'WH001', 'Main Warehouse', '123 Main Street', 'John Doe', 1, 1),
(2, 'WH002', 'Branch Warehouse', '456 Branch Avenue', 'Jane Smith', 1, 1);

-- 初始化供应商
INSERT INTO base_supplier (id, sup_code, sup_name, contact_person, phone, address, create_by) VALUES
(1, 'SUP001', 'Tech Supplier Co.', 'Mike Johnson', '13900001111', '789 Tech Park', 1),
(2, 'SUP002', 'Global Trade Ltd.', 'Sarah Wilson', '13900002222', '101 Trade Center', 1);

-- 初始化客户
INSERT INTO base_customer (id, cust_code, cust_name, contact_person, phone, address, create_by) VALUES
(1, 'CUST001', 'Big Mart Inc.', 'Tom Brown', '13800001111', '202 Retail Street', 1),
(2, 'CUST002', 'Online Store Ltd.', 'Lisa Davis', '13800002222', '303 Commerce Ave', 1);
