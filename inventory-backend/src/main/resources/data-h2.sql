-- =============================================
-- 初始化数据 (H2兼容版本)
-- =============================================

-- 初始化部门数据
INSERT INTO sys_dept (id, parent_id, dept_name, order_num, leader, phone, email, create_by) VALUES
(1, 0, 'Headquarters', 1, 'Admin', '13800138000', 'admin@example.com', '1'),
(2, 1, 'R&D Dept', 1, 'Tech Lead', '13800138001', 'rd@example.com', '1'),
(3, 1, 'Marketing Dept', 2, 'Marketing Mgr', '13800138002', 'market@example.com', '1'),
(4, 1, 'Purchasing Dept', 3, 'Purchase Mgr', '13800138003', 'purchase@example.com', '1'),
(5, 1, 'Sales Dept', 4, 'Sales Mgr', '13800138004', 'sales@example.com', '1'),
(6, 1, 'Warehouse Dept', 5, 'Warehouse Mgr', '13800138005', 'warehouse@example.com', '1'),
(7, 1, 'Finance Dept', 6, 'Finance Mgr', '13800138006', 'finance@example.com', '1');

-- 初始化角色数据
INSERT INTO sys_role (id, role_name, role_key, data_scope, status, create_by) VALUES
(1, 'Administrator', 'admin', '1', '0', '1'),
(2, 'Purchase Manager', 'purchase_manager', '3', '0', '1'),
(3, 'Sales Manager', 'sale_manager', '3', '0', '1'),
(4, 'Warehouse Manager', 'warehouse_manager', '3', '0', '1'),
(5, 'Employee', 'employee', '5', '0', '1');

-- 初始化用户数据 (password: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, dept_id, status, create_by) VALUES
(1, 'admin', '$2a$10$s/6K.oSU5QmSElA9YoRcneb.RhpBhmFMWOaFh1jtYg5e6NQkivm6K', 'Admin', '13800138000', 'admin@example.com', 1, '0', '1');

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- 初始化菜单数据
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, type, order_num, visible, status, icon, create_by) VALUES
(1, 0, 'System', '/system', NULL, NULL, 'M', 1, '0', '0', 'system', '1'),
(2, 1, 'User Management', '/system/user', 'system/user/index', 'system:user:list', 'C', 1, '0', '0', 'user', '1'),
(3, 1, 'Role Management', '/system/role', 'system/role/index', 'system:role:list', 'C', 2, '0', '0', 'peoples', '1'),
(4, 1, 'Menu Management', '/system/menu', 'system/menu/index', 'system:menu:list', 'C', 3, '0', '0', 'tree-table', '1'),
(5, 1, 'Dept Management', '/system/dept', 'system/dept/index', 'system:dept:list', 'C', 4, '0', '0', 'tree', '1'),
(6, 1, 'Operation Log', '/system/log', 'system/log/index', 'system:log:list', 'C', 5, '0', '0', 'log', '1'),
(10, 0, 'Base Data', '/base', NULL, NULL, 'M', 2, '0', '0', 'shopping', '1'),
(11, 10, 'Product Management', '/base/product', 'base/product/index', 'base:product:list', 'C', 1, '0', '0', 'shopping', '1'),
(12, 10, 'Warehouse Management', '/base/warehouse', 'base/warehouse/index', 'base:warehouse:list', 'C', 2, '0', '0', 'excel', '1'),
(13, 10, 'Supplier Management', '/base/supplier', 'base/supplier/index', 'base:supplier:list', 'C', 3, '0', '0', 'peoples', '1'),
(14, 10, 'Customer Management', '/base/customer', 'base/customer/index', 'base:customer:list', 'C', 4, '0', '0', 'peoples', '1'),
(20, 0, 'Purchase Management', '/purchase', NULL, NULL, 'M', 3, '0', '0', 'shopping-cart', '1'),
(21, 20, 'Purchase Order', '/purchase/order', 'purchase/order/index', 'purchase:order:list', 'C', 1, '0', '0', 'form', '1'),
(22, 20, 'Purchase Inbound', '/purchase/inbound', 'purchase/inbound/index', 'purchase:inbound:list', 'C', 2, '0', '0', 'checkbox', '1'),
(30, 0, 'Sales Management', '/sale', NULL, NULL, 'M', 4, '0', '0', 'money', '1'),
(31, 30, 'Sales Order', '/sale/order', 'sale/order/index', 'sale:order:list', 'C', 1, '0', '0', 'form', '1'),
(32, 30, 'Sales Outbound', '/sale/outbound', 'sale/outbound/index', 'sale:outbound:list', 'C', 2, '0', '0', 'checkbox', '1'),
(40, 0, 'Inventory Management', '/stock', NULL, NULL, 'M', 5, '0', '0', 'component', '1'),
(41, 40, 'Real-time Inventory', '/stock/main', 'stock/main/index', 'stock:main:list', 'C', 1, '0', '0', 'list', '1'),
(42, 40, 'Inventory Record', '/stock/record', 'stock/record/index', 'stock:record:list', 'C', 2, '0', '0', 'log', '1'),
(43, 40, 'Inventory Check', '/stock/check', 'stock/check/index', 'stock:check:list', 'C', 3, '0', '0', 'build', '1'),
(44, 40, 'Inventory Transfer', '/stock/transfer', 'stock/transfer/index', 'stock:transfer:list', 'C', 4, '0', '0', 'guide', '1'),
(50, 0, 'System Monitor', '/monitor', NULL, NULL, 'M', 6, '0', '0', 'monitor', '1'),
(51, 50, 'Online Users', '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'C', 1, '0', '0', 'online', '1'),
(52, 50, 'Server Monitor', '/monitor/server', 'monitor/server/index', 'monitor:server:list', 'C', 2, '0', '0', 'server', '1'),
(53, 50, 'Cache Monitor', '/monitor/cache', 'monitor/cache/index', 'monitor:cache:list', 'C', 3, '0', '0', 'redis', '1');

-- 初始化按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, perms, type, order_num, visible, status, icon, create_by) VALUES
(100, 2, 'User Query', NULL, NULL, 'system:user:query', 'F', 1, '0', '0', '#', '1'),
(101, 2, 'User Add', NULL, NULL, 'system:user:add', 'F', 2, '0', '0', '#', '1'),
(102, 2, 'User Edit', NULL, NULL, 'system:user:edit', 'F', 3, '0', '0', '#', '1'),
(103, 2, 'User Delete', NULL, NULL, 'system:user:remove', 'F', 4, '0', '0', '#', '1'),
(104, 2, 'Reset Password', NULL, NULL, 'system:user:resetPwd', 'F', 5, '0', '0', '#', '1'),
(105, 2, 'User Export', NULL, NULL, 'system:user:export', 'F', 6, '0', '0', '#', '1'),
(110, 3, 'Role Query', NULL, NULL, 'system:role:query', 'F', 1, '0', '0', '#', '1'),
(111, 3, 'Role Add', NULL, NULL, 'system:role:add', 'F', 2, '0', '0', '#', '1'),
(112, 3, 'Role Edit', NULL, NULL, 'system:role:edit', 'F', 3, '0', '0', '#', '1'),
(113, 3, 'Role Delete', NULL, NULL, 'system:role:remove', 'F', 4, '0', '0', '#', '1'),
(120, 4, 'Menu Query', NULL, NULL, 'system:menu:query', 'F', 1, '0', '0', '#', '1'),
(121, 4, 'Menu Add', NULL, NULL, 'system:menu:add', 'F', 2, '0', '0', '#', '1'),
(122, 4, 'Menu Edit', NULL, NULL, 'system:menu:edit', 'F', 3, '0', '0', '#', '1'),
(123, 4, 'Menu Delete', NULL, NULL, 'system:menu:remove', 'F', 4, '0', '0', '#', '1'),
(130, 5, 'Dept Query', NULL, NULL, 'system:dept:query', 'F', 1, '0', '0', '#', '1'),
(131, 5, 'Dept Add', NULL, NULL, 'system:dept:add', 'F', 2, '0', '0', '#', '1'),
(132, 5, 'Dept Edit', NULL, NULL, 'system:dept:edit', 'F', 3, '0', '0', '#', '1'),
(133, 5, 'Dept Delete', NULL, NULL, 'system:dept:remove', 'F', 4, '0', '0', '#', '1'),
(140, 11, 'Product Query', NULL, NULL, 'base:product:query', 'F', 1, '0', '0', '#', '1'),
(141, 11, 'Product Add', NULL, NULL, 'base:product:add', 'F', 2, '0', '0', '#', '1'),
(142, 11, 'Product Edit', NULL, NULL, 'base:product:edit', 'F', 3, '0', '0', '#', '1'),
(143, 11, 'Product Delete', NULL, NULL, 'base:product:remove', 'F', 4, '0', '0', '#', '1'),
(150, 12, 'Warehouse Query', NULL, NULL, 'base:warehouse:query', 'F', 1, '0', '0', '#', '1'),
(151, 12, 'Warehouse Add', NULL, NULL, 'base:warehouse:add', 'F', 2, '0', '0', '#', '1'),
(152, 12, 'Warehouse Edit', NULL, NULL, 'base:warehouse:edit', 'F', 3, '0', '0', '#', '1'),
(153, 12, 'Warehouse Delete', NULL, NULL, 'base:warehouse:remove', 'F', 4, '0', '0', '#', '1'),
(160, 13, 'Supplier Query', NULL, NULL, 'base:supplier:query', 'F', 1, '0', '0', '#', '1'),
(161, 13, 'Supplier Add', NULL, NULL, 'base:supplier:add', 'F', 2, '0', '0', '#', '1'),
(162, 13, 'Supplier Edit', NULL, NULL, 'base:supplier:edit', 'F', 3, '0', '0', '#', '1'),
(163, 13, 'Supplier Delete', NULL, NULL, 'base:supplier:remove', 'F', 4, '0', '0', '#', '1'),
(170, 14, 'Customer Query', NULL, NULL, 'base:customer:query', 'F', 1, '0', '0', '#', '1'),
(171, 14, 'Customer Add', NULL, NULL, 'base:customer:add', 'F', 2, '0', '0', '#', '1'),
(172, 14, 'Customer Edit', NULL, NULL, 'base:customer:edit', 'F', 3, '0', '0', '#', '1'),
(173, 14, 'Customer Delete', NULL, NULL, 'base:customer:remove', 'F', 4, '0', '0', '#', '1'),
(180, 21, 'Purchase Order Query', NULL, NULL, 'purchase:order:query', 'F', 1, '0', '0', '#', '1'),
(181, 21, 'Purchase Order Add', NULL, NULL, 'purchase:order:add', 'F', 2, '0', '0', '#', '1'),
(182, 21, 'Purchase Order Edit', NULL, NULL, 'purchase:order:edit', 'F', 3, '0', '0', '#', '1'),
(183, 21, 'Purchase Order Delete', NULL, NULL, 'purchase:order:remove', 'F', 4, '0', '0', '#', '1'),
(184, 21, 'Purchase Order Audit', NULL, NULL, 'purchase:order:audit', 'F', 5, '0', '0', '#', '1'),
(190, 31, 'Sales Order Query', NULL, NULL, 'sale:order:query', 'F', 1, '0', '0', '#', '1'),
(191, 31, 'Sales Order Add', NULL, NULL, 'sale:order:add', 'F', 2, '0', '0', '#', '1'),
(192, 31, 'Sales Order Edit', NULL, NULL, 'sale:order:edit', 'F', 3, '0', '0', '#', '1'),
(193, 31, 'Sales Order Delete', NULL, NULL, 'sale:order:remove', 'F', 4, '0', '0', '#', '1'),
(194, 31, 'Sales Order Audit', NULL, NULL, 'sale:order:audit', 'F', 5, '0', '0', '#', '1'),
(200, 43, 'Check Query', NULL, NULL, 'stock:check:query', 'F', 1, '0', '0', '#', '1'),
(201, 43, 'Check Add', NULL, NULL, 'stock:check:add', 'F', 2, '0', '0', '#', '1'),
(202, 43, 'Check Edit', NULL, NULL, 'stock:check:edit', 'F', 3, '0', '0', '#', '1'),
(203, 43, 'Check Delete', NULL, NULL, 'stock:check:remove', 'F', 4, '0', '0', '#', '1'),
(204, 43, 'Check Complete', NULL, NULL, 'stock:check:complete', 'F', 5, '0', '0', '#', '1');

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
