const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, WidthType, BorderStyle, AlignmentType, HeadingLevel, PageBreak, LevelFormat, Tab, Header, Footer, PageNumber, NumberFormat, VerticalAlign, ShadingType } = require('docx');
const fs = require('fs');

// 创建文档
const doc = new Document({
  styles: {
    paragraphStyles: [
      {
        id: 'Normal',
        name: 'Normal',
        run: {
          font: 'SimSun',
          size: 24, // 12pt = 24 half-points
        },
        paragraph: {
          spacing: { line: 360, lineRule: 'auto' }, // 1.5倍行距
        },
      },
      {
        id: 'Heading1',
        name: 'Heading 1',
        basedOn: 'Normal',
        next: 'Normal',
        run: {
          font: 'SimHei',
          size: 36, // 18pt
          bold: true,
        },
        paragraph: {
          spacing: { before: 360, after: 240, line: 360, lineRule: 'auto' },
          outlineLevel: 0,
        },
      },
      {
        id: 'Heading2',
        name: 'Heading 2',
        basedOn: 'Normal',
        next: 'Normal',
        run: {
          font: 'SimHei',
          size: 32, // 16pt
          bold: true,
        },
        paragraph: {
          spacing: { before: 240, after: 180, line: 360, lineRule: 'auto' },
          outlineLevel: 1,
        },
      },
      {
        id: 'Heading3',
        name: 'Heading 3',
        basedOn: 'Normal',
        next: 'Normal',
        run: {
          font: 'SimHei',
          size: 28, // 14pt
          bold: true,
        },
        paragraph: {
          spacing: { before: 180, after: 120, line: 360, lineRule: 'auto' },
          outlineLevel: 2,
        },
      },
      {
        id: 'Code',
        name: 'Code',
        basedOn: 'Normal',
        run: {
          font: 'Courier New',
          size: 20, // 10pt
        },
        paragraph: {
          spacing: { line: 276, lineRule: 'auto' },
        },
      },
    ],
  },
  sections: [
    // 封面
    {
      properties: {},
      children: [
        new Paragraph({ spacing: { before: 2000 } }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '企业级智能库存管理系统 V2.0.0',
              font: 'SimHei',
              size: 56,
              bold: true,
            }),
          ],
        }),
        new Paragraph({ spacing: { before: 800 } }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: 'API接口文档（Swagger聚合）',
              font: 'SimHei',
              size: 44,
              bold: true,
            }),
          ],
        }),
        new Paragraph({ spacing: { before: 1600 } }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '版本：V2.0.0',
              font: 'SimSun',
              size: 28,
            }),
          ],
        }),
        new Paragraph({ spacing: { before: 200 } }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '日期：2026年3月',
              font: 'SimSun',
              size: 28,
            }),
          ],
        }),
        new Paragraph({ spacing: { before: 2000 } }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '版权所有 © 2026',
              font: 'SimSun',
              size: 24,
              color: '666666',
            }),
          ],
        }),
        new Paragraph({
          pageBreakBefore: true,
        }),
      ],
    },
    // 目录页
    {
      properties: {},
      children: [
        new Paragraph({
          text: '目  录',
          heading: HeadingLevel.HEADING_1,
          alignment: AlignmentType.CENTER,
        }),
        new Paragraph({ spacing: { before: 400 } }),
        createTocEntry('第一章 接口规范说明', 1),
        createTocEntry('1.1 接口基础信息', 2),
        createTocEntry('1.2 认证方式', 2),
        createTocEntry('1.3 统一响应格式', 2),
        createTocEntry('1.4 分页响应格式', 2),
        createTocEntry('1.5 错误码说明', 2),
        createTocEntry('第二章 认证授权接口', 1),
        createTocEntry('2.1 用户登录', 2),
        createTocEntry('2.2 获取用户信息', 2),
        createTocEntry('2.3 获取路由信息', 2),
        createTocEntry('2.4 用户登出', 2),
        createTocEntry('第三章 系统管理接口', 1),
        createTocEntry('3.1 用户管理', 2),
        createTocEntry('3.2 角色管理', 2),
        createTocEntry('3.3 菜单管理', 2),
        createTocEntry('3.4 部门管理', 2),
        createTocEntry('第四章 基础档案接口', 1),
        createTocEntry('4.1 商品SPU管理', 2),
        createTocEntry('4.2 商品SKU管理', 2),
        createTocEntry('4.3 仓库管理', 2),
        createTocEntry('4.4 供应商管理', 2),
        createTocEntry('4.5 客户管理', 2),
        createTocEntry('第五章 采购管理接口', 1),
        createTocEntry('5.1 采购订单列表', 2),
        createTocEntry('5.2 新增采购订单', 2),
        createTocEntry('5.3 审核采购订单', 2),
        createTocEntry('5.4 执行入库', 2),
        createTocEntry('第六章 销售管理接口', 1),
        createTocEntry('6.1 销售订单列表', 2),
        createTocEntry('6.2 新增销售订单', 2),
        createTocEntry('6.3 审核销售订单', 2),
        createTocEntry('6.4 执行出库', 2),
        createTocEntry('第七章 库存管理接口', 1),
        createTocEntry('7.1 实时库存查询', 2),
        createTocEntry('7.2 库存流水查询', 2),
        createTocEntry('7.3 库存预警', 2),
        createTocEntry('7.4 库存盘点', 2),
        createTocEntry('第八章 数据大屏接口', 1),
        createTocEntry('8.1 概览数据', 2),
        createTocEntry('8.2 出入库趋势', 2),
        createTocEntry('8.3 分类库存占比', 2),
        createTocEntry('8.4 库存预警列表', 2),
        createTocEntry('8.5 TOP商品', 2),
        createTocEntry('第九章 Swagger访问说明', 1),
        createTocEntry('9.1 访问地址', 2),
        createTocEntry('9.2 在线调试说明', 2),
        new Paragraph({
          pageBreakBefore: true,
        }),
      ],
    },
    // 正文内容
    {
      properties: {},
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({
                  text: 'API接口文档（Swagger聚合）',
                  font: 'SimSun',
                  size: 18,
                  color: '888888',
                }),
              ],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.CENTER,
              children: [
                new TextRun({
                  text: '第 ',
                  font: 'SimSun',
                  size: 20,
                }),
                new TextRun({
                  children: [PageNumber.CURRENT],
                  font: 'SimSun',
                  size: 20,
                }),
                new TextRun({
                  text: ' 页',
                  font: 'SimSun',
                  size: 20,
                }),
              ],
            }),
          ],
        }),
      },
      children: [
        // 第一章 接口规范说明
        new Paragraph({
          text: '第一章 接口规范说明',
          heading: HeadingLevel.HEADING_1,
        }),
        
        // 1.1 接口基础信息
        new Paragraph({
          text: '1.1 接口基础信息',
          heading: HeadingLevel.HEADING_2,
        }),
        createInfoTable([
          ['基础路径', 'http://localhost:8080'],
          ['协议', 'HTTP/HTTPS'],
          ['数据格式', 'JSON'],
          ['字符编码', 'UTF-8'],
          ['接口风格', 'RESTful'],
          ['认证方式', 'JWT Bearer Token'],
        ]),
        
        // 1.2 认证方式
        new Paragraph({
          text: '1.2 认证方式',
          heading: HeadingLevel.HEADING_2,
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '本系统采用JWT（JSON Web Token）进行身份认证。', font: 'SimSun', size: 24 })],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '认证类型：', font: 'SimHei', size: 24, bold: true }),
            new TextRun({ text: 'JWT Bearer Token', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: 'Token获取：', font: 'SimHei', size: 24, bold: true }),
            new TextRun({ text: '通过登录接口返回', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: 'Token使用：', font: 'SimHei', size: 24, bold: true }),
            new TextRun({ text: '请求头 Authorization: Bearer {token}', font: 'SimSun', size: 24 }),
          ],
        }),
        
        // 1.3 统一响应格式
        new Paragraph({
          text: '1.3 统一响应格式',
          heading: HeadingLevel.HEADING_2,
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '所有接口返回统一的JSON格式数据：', font: 'SimSun', size: 24 })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}`),
        
        // 1.4 分页响应格式
        new Paragraph({
          text: '1.4 分页响应格式',
          heading: HeadingLevel.HEADING_2,
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '列表查询接口返回分页数据格式：', font: 'SimSun', size: 24 })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "rows": [],
  "total": 100
}`),
        
        // 1.5 错误码说明
        new Paragraph({
          text: '1.5 错误码说明',
          heading: HeadingLevel.HEADING_2,
        }),
        createErrorTable([
          ['200', '操作成功'],
          ['400', '参数错误'],
          ['401', '未授权/Token失效'],
          ['403', '无权限'],
          ['500', '服务器内部错误'],
        ]),
        
        // 第二章 认证授权接口
        new Paragraph({
          text: '第二章 认证授权接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 2.1 用户登录
        new Paragraph({
          text: '2.1 用户登录',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('POST', '/auth/login', '用户登录认证'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "username": "admin",
  "password": "123456"
}`),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}`),
        
        // 2.2 获取用户信息
        new Paragraph({
          text: '2.2 获取用户信息',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/getInfo', '获取当前登录用户信息'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "user": {
      "userId": 1,
      "userName": "admin",
      "nickName": "管理员",
      "email": "admin@example.com",
      "phonenumber": "13800138000"
    },
    "roles": ["admin"],
    "permissions": ["*:*:*"]
  }
}`),
        
        // 2.3 获取路由信息
        new Paragraph({
          text: '2.3 获取路由信息',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/getRouters', '获取当前用户可访问的路由菜单'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "name": "Dashboard",
      "path": "/dashboard",
      "component": "dashboard/index",
      "meta": { "title": "首页", "icon": "dashboard" }
    }
  ]
}`),
        
        // 2.4 用户登出
        new Paragraph({
          text: '2.4 用户登出',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('POST', '/auth/logout', '用户退出登录'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "退出成功"
}`),
        
        // 第三章 系统管理接口
        new Paragraph({
          text: '第三章 系统管理接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 3.1 用户管理
        new Paragraph({
          text: '3.1 用户管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/system/user/list', '用户列表', '分页查询用户列表'],
          ['GET', '/system/user/{userId}', '用户详情', '根据ID查询用户详情'],
          ['POST', '/system/user', '新增用户', '创建新用户'],
          ['PUT', '/system/user', '修改用户', '更新用户信息'],
          ['DELETE', '/system/user/{userIds}', '删除用户', '批量删除用户'],
          ['PUT', '/system/user/resetPwd', '重置密码', '重置用户密码'],
        ]),
        
        // 3.2 角色管理
        new Paragraph({
          text: '3.2 角色管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/system/role/list', '角色列表', '查询角色列表'],
          ['POST', '/system/role', '新增角色', '创建新角色'],
          ['PUT', '/system/role', '修改角色', '更新角色信息'],
          ['DELETE', '/system/role/{roleIds}', '删除角色', '批量删除角色'],
          ['PUT', '/system/role/allotMenu', '分配菜单权限', '为角色分配菜单权限'],
        ]),
        
        // 3.3 菜单管理
        new Paragraph({
          text: '3.3 菜单管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/system/menu/list', '菜单列表', '查询菜单列表'],
          ['GET', '/system/menu/tree', '菜单树', '获取菜单树结构'],
          ['POST', '/system/menu', '新增菜单', '创建新菜单'],
          ['PUT', '/system/menu', '修改菜单', '更新菜单信息'],
          ['DELETE', '/system/menu/{menuId}', '删除菜单', '删除菜单'],
        ]),
        
        // 3.4 部门管理
        new Paragraph({
          text: '3.4 部门管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/system/dept/list', '部门列表', '查询部门列表'],
          ['GET', '/system/dept/tree', '部门树', '获取部门树结构'],
          ['POST', '/system/dept', '新增部门', '创建新部门'],
          ['PUT', '/system/dept', '修改部门', '更新部门信息'],
          ['DELETE', '/system/dept/{deptId}', '删除部门', '删除部门'],
        ]),
        
        // 第四章 基础档案接口
        new Paragraph({
          text: '第四章 基础档案接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 4.1 商品SPU管理
        new Paragraph({
          text: '4.1 商品SPU管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/base/product/list', '商品列表', '分页查询商品SPU列表'],
          ['GET', '/base/product/{id}', '商品详情', '查询商品SPU详情'],
          ['POST', '/base/product', '新增商品', '创建新商品SPU'],
          ['PUT', '/base/product', '修改商品', '更新商品SPU信息'],
          ['DELETE', '/base/product/{ids}', '删除商品', '批量删除商品SPU'],
        ]),
        
        // 4.2 商品SKU管理
        new Paragraph({
          text: '4.2 商品SKU管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/base/sku/list', 'SKU列表', '分页查询商品SKU列表'],
          ['GET', '/base/sku/{id}', 'SKU详情', '查询SKU详情'],
          ['POST', '/base/sku/batchGenerate', '批量生成SKU', '根据规格批量生成SKU'],
          ['PUT', '/base/sku', '修改SKU', '更新SKU信息'],
          ['DELETE', '/base/sku/{ids}', '删除SKU', '批量删除SKU'],
        ]),
        
        // 4.3 仓库管理
        new Paragraph({
          text: '4.3 仓库管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/base/warehouse/list', '仓库列表', '查询仓库列表'],
          ['GET', '/base/warehouse/tree', '仓库树', '获取仓库树结构'],
          ['POST', '/base/warehouse', '新增仓库', '创建新仓库'],
          ['PUT', '/base/warehouse', '修改仓库', '更新仓库信息'],
          ['DELETE', '/base/warehouse/{ids}', '删除仓库', '批量删除仓库'],
        ]),
        
        // 4.4 供应商管理
        new Paragraph({
          text: '4.4 供应商管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/base/supplier/list', '供应商列表', '分页查询供应商列表'],
          ['GET', '/base/supplier/{id}', '供应商详情', '查询供应商详情'],
          ['POST', '/base/supplier', '新增供应商', '创建新供应商'],
          ['PUT', '/base/supplier', '修改供应商', '更新供应商信息'],
          ['DELETE', '/base/supplier/{ids}', '删除供应商', '批量删除供应商'],
        ]),
        
        // 4.5 客户管理
        new Paragraph({
          text: '4.5 客户管理',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/base/customer/list', '客户列表', '分页查询客户列表'],
          ['GET', '/base/customer/{id}', '客户详情', '查询客户详情'],
          ['POST', '/base/customer', '新增客户', '创建新客户'],
          ['PUT', '/base/customer', '修改客户', '更新客户信息'],
          ['DELETE', '/base/customer/{ids}', '删除客户', '批量删除客户'],
        ]),
        
        // 第五章 采购管理接口
        new Paragraph({
          text: '第五章 采购管理接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 5.1 采购订单列表
        new Paragraph({
          text: '5.1 采购订单列表',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/business/purchase/list', '分页查询采购订单列表'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createParamTable([
          ['pageNum', 'Integer', '否', '页码，默认1'],
          ['pageSize', 'Integer', '否', '每页条数，默认10'],
          ['purchaseNo', 'String', '否', '采购订单号'],
          ['supplierId', 'Long', '否', '供应商ID'],
          ['status', 'Integer', '否', '订单状态：0待审核 1已审核 2已入库'],
        ]),
        
        // 5.2 新增采购订单
        new Paragraph({
          text: '5.2 新增采购订单',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('POST', '/business/purchase', '创建采购订单'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求体示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "supplierId": 1,
  "items": [
    {
      "skuId": 1,
      "quantity": 100,
      "unitPrice": 10.00
    },
    {
      "skuId": 2,
      "quantity": 200,
      "unitPrice": 20.00
    }
  ],
  "remark": "备货采购"
}`),
        
        // 5.3 审核采购订单
        new Paragraph({
          text: '5.3 审核采购订单',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('PUT', '/business/purchase/audit', '审核采购订单'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "id": 1,
  "status": 1,
  "auditRemark": "审核通过"
}`),
        
        // 5.4 执行入库
        new Paragraph({
          text: '5.4 执行入库',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('PUT', '/business/purchase/instock', '采购订单执行入库操作'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "id": 1,
  "warehouseId": 1
}`),
        
        // 第六章 销售管理接口
        new Paragraph({
          text: '第六章 销售管理接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 6.1 销售订单列表
        new Paragraph({
          text: '6.1 销售订单列表',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/business/sale/list', '分页查询销售订单列表'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createParamTable([
          ['pageNum', 'Integer', '否', '页码，默认1'],
          ['pageSize', 'Integer', '否', '每页条数，默认10'],
          ['saleNo', 'String', '否', '销售订单号'],
          ['customerId', 'Long', '否', '客户ID'],
          ['status', 'Integer', '否', '订单状态：0待审核 1已审核 2已出库'],
        ]),
        
        // 6.2 新增销售订单
        new Paragraph({
          text: '6.2 新增销售订单',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('POST', '/business/sale', '创建销售订单'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求体示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "customerId": 1,
  "items": [
    {
      "skuId": 1,
      "quantity": 50,
      "unitPrice": 15.00
    },
    {
      "skuId": 2,
      "quantity": 100,
      "unitPrice": 25.00
    }
  ],
  "remark": "常规销售"
}`),
        
        // 6.3 审核销售订单
        new Paragraph({
          text: '6.3 审核销售订单',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('PUT', '/business/sale/audit', '审核销售订单'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "id": 1,
  "status": 1,
  "auditRemark": "审核通过"
}`),
        
        // 6.4 执行出库
        new Paragraph({
          text: '6.4 执行出库',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('PUT', '/business/sale/outstock', '销售订单执行出库操作'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "id": 1,
  "warehouseId": 1
}`),
        
        // 第七章 库存管理接口
        new Paragraph({
          text: '第七章 库存管理接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 7.1 实时库存查询
        new Paragraph({
          text: '7.1 实时库存查询',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/stock/main/list', '分页查询实时库存'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createParamTable([
          ['pageNum', 'Integer', '否', '页码，默认1'],
          ['pageSize', 'Integer', '否', '每页条数，默认10'],
          ['skuId', 'Long', '否', 'SKU ID'],
          ['warehouseId', 'Long', '否', '仓库ID'],
          ['categoryId', 'Long', '否', '分类ID'],
        ]),
        
        // 7.2 库存流水查询
        new Paragraph({
          text: '7.2 库存流水查询',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/stock/record/list', '查询库存变动流水记录'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createParamTable([
          ['pageNum', 'Integer', '否', '页码，默认1'],
          ['pageSize', 'Integer', '否', '每页条数，默认10'],
          ['skuId', 'Long', '否', 'SKU ID'],
          ['warehouseId', 'Long', '否', '仓库ID'],
          ['bizType', 'String', '否', '业务类型：PURCHASE入库/SALE出库/CHECK盘点'],
          ['startTime', 'String', '否', '开始时间'],
          ['endTime', 'String', '否', '结束时间'],
        ]),
        
        // 7.3 库存预警
        new Paragraph({
          text: '7.3 库存预警',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/stock/warning', '查询库存预警列表'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "skuId": 1,
      "skuName": "商品A-红色-L",
      "quantity": 5,
      "minQuantity": 10,
      "warehouseName": "主仓库"
    }
  ]
}`),
        
        // 7.4 库存盘点
        new Paragraph({
          text: '7.4 库存盘点',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiListTable([
          ['GET', '/stock/check/list', '盘点单列表', '分页查询盘点单列表'],
          ['GET', '/stock/check/{id}', '盘点单详情', '查询盘点单详情'],
          ['POST', '/stock/check', '新增盘点', '创建盘点单'],
          ['PUT', '/stock/check/confirm', '确认盘点', '确认盘点结果并调整库存'],
        ]),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto', before: 200 },
          children: [new TextRun({ text: '新增盘点请求体：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "warehouseId": 1,
  "items": [
    {
      "skuId": 1,
      "systemQuantity": 100,
      "actualQuantity": 98,
      "remark": "正常损耗"
    }
  ]
}`),
        
        // 第八章 数据大屏接口
        new Paragraph({
          text: '第八章 数据大屏接口',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 8.1 概览数据
        new Paragraph({
          text: '8.1 概览数据',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/monitor/dashboard/overview', '获取数据大屏概览统计'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalSkuCount": 1000,
    "totalStockQuantity": 50000,
    "totalStockValue": 1500000.00,
    "todayInQuantity": 500,
    "todayOutQuantity": 300,
    "warningCount": 15
  }
}`),
        
        // 8.2 出入库趋势
        new Paragraph({
          text: '8.2 出入库趋势',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/monitor/dashboard/trend', '获取近7天出入库趋势数据'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "date": "2026-03-01",
      "inQuantity": 500,
      "outQuantity": 300
    },
    {
      "date": "2026-03-02",
      "inQuantity": 600,
      "outQuantity": 400
    }
  ]
}`),
        
        // 8.3 分类库存占比
        new Paragraph({
          text: '8.3 分类库存占比',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/monitor/dashboard/category', '获取各分类库存占比数据'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '响应示例：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock(`{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "categoryName": "电子产品",
      "quantity": 5000,
      "ratio": 25.5
    },
    {
      "categoryName": "服装",
      "quantity": 8000,
      "ratio": 40.0
    }
  ]
}`),
        
        // 8.4 库存预警列表
        new Paragraph({
          text: '8.4 库存预警列表',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/monitor/dashboard/warning', '获取库存预警商品列表'),
        
        // 8.5 TOP商品
        new Paragraph({
          text: '8.5 TOP商品',
          heading: HeadingLevel.HEADING_2,
        }),
        createApiInfo('GET', '/monitor/dashboard/topProducts', '获取销量TOP商品列表'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: '请求参数：', font: 'SimHei', size: 24, bold: true })],
        }),
        createParamTable([
          ['limit', 'Integer', '否', '返回数量，默认10'],
          ['type', 'String', '否', '类型：IN入库/OUT出库，默认OUT'],
        ]),
        
        // 第九章 Swagger访问说明
        new Paragraph({
          text: '第九章 Swagger访问说明',
          heading: HeadingLevel.HEADING_1,
          pageBreakBefore: true,
        }),
        
        // 9.1 访问地址
        new Paragraph({
          text: '9.1 访问地址',
          heading: HeadingLevel.HEADING_2,
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: 'Swagger UI 访问地址：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock('http://localhost:8080/swagger-ui.html'),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [new TextRun({ text: 'Swagger API 文档 JSON：', font: 'SimHei', size: 24, bold: true })],
        }),
        createCodeBlock('http://localhost:8080/v3/api-docs'),
        
        // 9.2 在线调试说明
        new Paragraph({
          text: '9.2 在线调试说明',
          heading: HeadingLevel.HEADING_2,
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '1. 打开 Swagger UI 页面，查看所有可用接口。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '2. 点击接口展开详情，查看请求参数和响应格式。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '3. 点击 "Authorize" 按钮，输入 Bearer Token 进行认证。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '4. 点击 "Try it out" 按钮，填写参数后执行请求。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '5. 查看响应结果，确认接口调用是否成功。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({ spacing: { before: 400 } }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '注意事项：', font: 'SimHei', size: 24, bold: true }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '• 需要先通过登录接口获取 Token，才能调试需要认证的接口。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '• Token 有效期默认为 2 小时，过期后需重新登录获取。', font: 'SimSun', size: 24 }),
          ],
        }),
        new Paragraph({
          spacing: { line: 360, lineRule: 'auto' },
          children: [
            new TextRun({ text: '• 部分 DELETE 接口支持批量操作，多个 ID 用逗号分隔。', font: 'SimSun', size: 24 }),
          ],
        }),
      ],
    },
  ],
});

// 辅助函数：创建目录条目
function createTocEntry(text, level) {
  return new Paragraph({
    spacing: { line: 360, lineRule: 'auto' },
    indent: { left: level === 1 ? 0 : 420 },
    children: [
      new TextRun({
        text: text,
        font: 'SimSun',
        size: 24,
      }),
    ],
  });
}

// 辅助函数：创建信息表格
function createInfoTable(data) {
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: data.map(([key, value]) => 
      new TableRow({
        children: [
          new TableCell({
            width: { size: 30, type: WidthType.PERCENTAGE },
            shading: { fill: 'F5F5F5' },
            children: [
              new Paragraph({
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: key, font: 'SimHei', size: 24, bold: true })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 70, type: WidthType.PERCENTAGE },
            children: [
              new Paragraph({
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: value, font: 'SimSun', size: 24 })],
              }),
            ],
          }),
        ],
      })
    ),
  });
}

// 辅助函数：创建错误码表格
function createErrorTable(data) {
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          new TableCell({
            width: { size: 20, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '错误码', font: 'SimHei', size: 24, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 80, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '说明', font: 'SimHei', size: 24, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
        ],
      }),
      ...data.map(([code, desc]) => 
        new TableRow({
          children: [
            new TableCell({
              width: { size: 20, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  alignment: AlignmentType.CENTER,
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: code, font: 'Courier New', size: 24 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 80, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: desc, font: 'SimSun', size: 24 })],
                }),
              ],
            }),
          ],
        })
      ),
    ],
  });
}

// 辅助函数：创建代码块
function createCodeBlock(code) {
  const lines = code.split('\n');
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        children: [
          new TableCell({
            shading: { fill: 'F5F5F5' },
            children: lines.map(line => 
              new Paragraph({
                spacing: { line: 276, lineRule: 'auto' },
                children: [new TextRun({ text: line || ' ', font: 'Courier New', size: 20 })],
              })
            ),
          }),
        ],
      }),
    ],
  });
}

// 辅助函数：创建API信息
function createApiInfo(method, path, desc) {
  const methodColors = {
    'GET': '2E7D32',
    'POST': '1565C0',
    'PUT': 'EF6C00',
    'DELETE': 'C62828',
  };
  
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        children: [
          new TableCell({
            width: { size: 15, type: WidthType.PERCENTAGE },
            shading: { fill: methodColors[method] || '666666' },
            verticalAlign: VerticalAlign.CENTER,
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: method, font: 'Courier New', size: 24, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 50, type: WidthType.PERCENTAGE },
            shading: { fill: 'F5F5F5' },
            verticalAlign: VerticalAlign.CENTER,
            children: [
              new Paragraph({
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: path, font: 'Courier New', size: 24 })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 35, type: WidthType.PERCENTAGE },
            verticalAlign: VerticalAlign.CENTER,
            children: [
              new Paragraph({
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: desc, font: 'SimSun', size: 24 })],
              }),
            ],
          }),
        ],
      }),
    ],
  });
}

// 辅助函数：创建API列表表格
function createApiListTable(data) {
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          new TableCell({
            width: { size: 12, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '方法', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 40, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '接口路径', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 23, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '接口名称', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 25, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '说明', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
        ],
      }),
      ...data.map(([method, path, name, desc]) => {
        const methodColors = {
          'GET': '2E7D32',
          'POST': '1565C0',
          'PUT': 'EF6C00',
          'DELETE': 'C62828',
        };
        return new TableRow({
          children: [
            new TableCell({
              width: { size: 12, type: WidthType.PERCENTAGE },
              shading: { fill: methodColors[method] || '666666' },
              verticalAlign: VerticalAlign.CENTER,
              children: [
                new Paragraph({
                  alignment: AlignmentType.CENTER,
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: method, font: 'Courier New', size: 20, bold: true, color: 'FFFFFF' })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 40, type: WidthType.PERCENTAGE },
              verticalAlign: VerticalAlign.CENTER,
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: path, font: 'Courier New', size: 20 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 23, type: WidthType.PERCENTAGE },
              verticalAlign: VerticalAlign.CENTER,
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: name, font: 'SimSun', size: 22 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 25, type: WidthType.PERCENTAGE },
              verticalAlign: VerticalAlign.CENTER,
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: desc, font: 'SimSun', size: 22 })],
                }),
              ],
            }),
          ],
        });
      }),
    ],
  });
}

// 辅助函数：创建参数表格
function createParamTable(data) {
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          new TableCell({
            width: { size: 20, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '参数名', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 15, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '类型', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 10, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '必填', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
          new TableCell({
            width: { size: 55, type: WidthType.PERCENTAGE },
            shading: { fill: '4472C4' },
            children: [
              new Paragraph({
                alignment: AlignmentType.CENTER,
                spacing: { line: 360, lineRule: 'auto' },
                children: [new TextRun({ text: '说明', font: 'SimHei', size: 22, bold: true, color: 'FFFFFF' })],
              }),
            ],
          }),
        ],
      }),
      ...data.map(([name, type, required, desc]) => 
        new TableRow({
          children: [
            new TableCell({
              width: { size: 20, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: name, font: 'Courier New', size: 20 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 15, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  alignment: AlignmentType.CENTER,
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: type, font: 'SimSun', size: 22 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 10, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  alignment: AlignmentType.CENTER,
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: required, font: 'SimSun', size: 22 })],
                }),
              ],
            }),
            new TableCell({
              width: { size: 55, type: WidthType.PERCENTAGE },
              children: [
                new Paragraph({
                  spacing: { line: 360, lineRule: 'auto' },
                  children: [new TextRun({ text: desc, font: 'SimSun', size: 22 })],
                }),
              ],
            }),
          ],
        })
      ),
    ],
  });
}

// 生成文档
Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync('/home/z/my-project/inventory-v2/docs/03-API接口文档.docx', buffer);
  console.log('API接口文档已生成：/home/z/my-project/inventory-v2/docs/03-API接口文档.docx');
});
