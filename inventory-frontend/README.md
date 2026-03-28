# 库存管理系统前端

## 项目信息

- **项目名称**: 库存管理系统前端 (Inventory Frontend)
- **版本**: V1.0.0
- **作者**: Graduation Team

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 2.6.14 | 前端框架 |
| Vue Router | 3.6.5 | 路由管理 |
| Vuex | 3.6.2 | 状态管理 |
| Element UI | 2.15.14 | UI组件库 |
| Axios | 1.6.2 | HTTP请求库 |
| ECharts | 5.4.3 | 图表库 |
| Sass | 1.69.5 | CSS预处理器 |
| Vue CLI | 5.0.8 | 构建工具 |

## 项目结构

```
inventory-frontend/
├── public/                              # 静态资源
│   └── index.html                       # HTML模板
├── src/
│   ├── api/                             # API接口
│   │   ├── base.js                      # 基础档案API
│   │   ├── business.js                  # 业务模块API
│   │   ├── customer.js                  # 客户API
│   │   ├── inbound.js                   # 入库API
│   │   ├── index.js                     # API入口
│   │   ├── inventory.js                 # 库存API
│   │   ├── login.js                     # 登录API
│   │   ├── monitor.js                   # 监控API
│   │   ├── outbound.js                  # 出库API
│   │   ├── product.js                   # 商品API
│   │   ├── stock.js                     # 库存API
│   │   ├── supplier.js                  # 供应商API
│   │   ├── system.js                    # 系统管理API
│   │   ├── user.js                      # 用户API
│   │   └── warehouse.js                 # 仓库API
│   ├── assets/                          # 资源文件
│   │   └── styles/                      # 样式文件
│   │       ├── element-ui.scss          # Element UI样式覆盖
│   │       ├── index.scss               # 主样式入口
│   │       ├── transition.scss          # 过渡动画
│   │       └── variables.scss           # 样式变量
│   ├── components/                      # 公共组件
│   ├── directives/                      # 自定义指令
│   │   └── permission.js                # 权限指令
│   ├── layout/                          # 布局组件
│   │   ├── index.vue                    # 主布局
│   │   └── components/                  # 布局子组件
│   │       ├── AppMain.vue              # 主内容区
│   │       ├── Breadcrumb.vue           # 面包屑
│   │       ├── Hamburger.vue            # 汉堡菜单
│   │       ├── Item.vue                 # 菜单项
│   │       ├── Link.vue                 # 菜单链接
│   │       ├── Navbar.vue               # 顶部导航
│   │       └── SidebarItem.vue          # 侧边栏菜单项
│   ├── router/                          # 路由配置
│   │   └── index.js                     # 路由定义
│   ├── store/                           # Vuex状态管理
│   │   ├── getters.js                   # Getters
│   │   ├── index.js                     # Store入口
│   │   └── modules/                     # 模块
│   │       ├── app.js                   # 应用状态
│   │       ├── permission.js            # 权限状态
│   │       └── user.js                  # 用户状态
│   ├── utils/                           # 工具函数
│   │   ├── auth.js                      # Token管理
│   │   ├── index.js                     # 通用工具
│   │   ├── permission.js                # 权限工具
│   │   ├── request.js                   # Axios封装
│   │   └── validate.js                  # 表单验证
│   ├── views/                           # 页面视图
│   │   ├── base/                        # 基础档案
│   │   │   ├── customer/                # 客户管理
│   │   │   ├── product/                 # 商品管理
│   │   │   ├── supplier/                # 供应商管理
│   │   │   └── warehouse/               # 仓库管理
│   │   ├── business/                    # 业务模块
│   │   │   ├── inbound/                 # 入库管理
│   │   │   ├── outbound/                # 出库管理
│   │   │   ├── purchase/                # 采购管理
│   │   │   └── sale/                    # 销售管理
│   │   ├── dashboard/                   # 首页仪表盘
│   │   ├── error/                       # 错误页面
│   │   │   └── 404.vue                  # 404页面
│   │   ├── login/                       # 登录模块
│   │   ├── monitor/                     # 系统监控
│   │   │   ├── log/                     # 操作日志
│   │   │   └── online/                  # 在线用户
│   │   ├── stock/                       # 库存管理
│   │   │   ├── check/                   # 库存盘点
│   │   │   └── inventory/               # 实时库存
│   │   └── system/                      # 系统管理
│   │       ├── dept/                    # 部门管理
│   │       ├── menu/                    # 菜单管理
│   │       ├── role/                    # 角色管理
│   │       └── user/                    # 用户管理
│   ├── App.vue                          # 根组件
│   ├── main.js                          # 入口文件
│   └── permission.js                    # 路由权限控制
├── babel.config.js                      # Babel配置
├── package.json                         # 依赖配置
└── vue.config.js                        # Vue CLI配置
```

## 环境要求

- **Node.js**: 14.x 及以上
- **npm**: 6.x 及以上 或 **yarn**: 1.22.x 及以上
- **浏览器**: Chrome、Firefox、Safari、Edge（现代浏览器）

## 安装运行步骤

### 1. 克隆项目

```bash
git clone <repository-url>
cd inventory-frontend
```

### 2. 安装依赖

```bash
# 使用npm
npm install

# 或使用yarn
yarn install
```

### 3. 配置后端API地址

编辑 `vue.config.js` 文件，修改后端API代理地址：

```javascript
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端服务地址
        changeOrigin: true
      }
    }
  }
}
```

### 4. 启动开发服务器

```bash
# 使用npm
npm run serve

# 或使用yarn
yarn serve
```

启动后访问：http://localhost:8081

### 5. 登录系统

默认管理员账号：
- 用户名: `admin`
- 密码: `123456`

## 功能模块说明

### 1. 首页仪表盘

- 数据统计概览
- 销售趋势图表
- 库存预警提醒
- 热销商品排行
- 分类占比分析

### 2. 系统管理

| 模块 | 功能 |
|------|------|
| 用户管理 | 用户增删改查、重置密码、分配角色、启用/禁用 |
| 角色管理 | 角色增删改查、分配菜单权限、数据权限设置 |
| 菜单管理 | 菜单增删改查、权限标识配置、菜单排序 |
| 部门管理 | 部门树形结构管理、部门增删改查 |

### 3. 基础档案

| 模块 | 功能 |
|------|------|
| 商品管理 | 商品SPU/SKU管理、规格配置、价格设置 |
| 仓库管理 | 仓库信息管理、仓库状态维护 |
| 供应商管理 | 供应商信息管理、联系方式维护 |
| 客户管理 | 客户信息管理、联系方式维护 |

### 4. 采购管理

| 模块 | 功能 |
|------|------|
| 采购订单 | 采购订单创建、审核、查询、导出 |
| 采购入库 | 入库单创建、审核、库存更新 |

### 5. 销售管理

| 模块 | 功能 |
|------|------|
| 销售订单 | 销售订单创建、审核、查询、导出 |
| 销售出库 | 出库单创建、审核、库存扣减 |

### 6. 库存管理

| 模块 | 功能 |
|------|------|
| 实时库存 | 库存查询、库存预警、库存导出 |
| 库存盘点 | 盘点单创建、盘点差异处理、盘点完成 |

### 7. 系统监控

| 模块 | 功能 |
|------|------|
| 在线用户 | 在线用户列表、强制下线 |
| 操作日志 | 操作记录查询、日志详情查看 |

## 构建部署

### 开发环境构建

```bash
npm run serve
```

### 生产环境构建

```bash
# 构建生产环境代码
npm run build
```

构建完成后，生成的静态文件位于 `dist/` 目录下。

### Nginx部署配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /usr/share/nginx/html/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### Docker部署

```dockerfile
# Dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```bash
# 构建镜像
docker build -t inventory-frontend:1.0.0 .

# 运行容器
docker run -d -p 80:80 --name inventory-frontend inventory-frontend:1.0.0
```

## 代码规范

- 使用 ESLint 进行代码检查
- 组件命名采用 PascalCase
- 文件命名采用 kebab-case
- API 接口统一放在 `src/api/` 目录

## 浏览器支持

| 浏览器 | 版本 |
|--------|------|
| Chrome | 最新版 |
| Firefox | 最新版 |
| Safari | 最新版 |
| Edge | 最新版 |
| IE | 不支持 |

## 作者信息

- **开发团队**: Graduation Team
- **联系方式**: example@example.com
- **创建时间**: 2024年

## 许可证

本项目仅供学习和研究使用。
