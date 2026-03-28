# 库存管理系统后端服务

## 项目信息

- **项目名称**: 库存管理系统后端服务 (Inventory Backend)
- **版本**: V1.0.0
- **作者**: Graduation Team

## 项目介绍

本项目是一个企业级智能库存管理系统的后端服务，基于 Spring Boot 框架开发，提供完善的库存管理、采购管理、销售管理、系统权限管理等功能。系统采用前后端分离架构，通过 RESTful API 对外提供服务。

### 核心特性

- 完整的 RBAC 权限模型（用户-角色-菜单）
- JWT Token 认证机制
- 基于Spring Security的安全框架
- 支持多仓库、多批次库存管理
- 采购/销售订单全流程管理
- 库存盘点与实时库存监控
- 操作日志审计
- API接口文档自动生成

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Security | - | 安全框架 |
| MyBatis-Plus | 3.5.3.1 | ORM框架 |
| MySQL | 8.0.33 | 数据库 |
| Druid | 1.2.16 | 数据库连接池 |
| JWT | 0.11.5 | Token认证 |
| Caffeine | 3.1.8 | 本地缓存 |
| Knife4j | 3.0.3 | API文档 |
| Hutool | 5.8.11 | 工具类库 |
| Lombok | - | 代码简化 |

## 项目结构

```
inventory-backend/
├── pom.xml                                    # Maven配置文件
├── sql/                                       # 数据库脚本
│   └── inventory_v2.sql                       # 数据库初始化脚本
└── src/
    └── main/
        ├── java/
        │   └── com/graduation/inventory/
        │       ├── InventoryApplication.java  # 启动类
        │       ├── base/                      # 基础档案模块
        │       │   ├── controller/            # 控制器层
        │       │   ├── entity/                # 实体类
        │       │   ├── mapper/                # 数据访问层
        │       │   └── service/               # 业务逻辑层
        │       ├── business/                  # 核心业务模块
        │       │   ├── controller/            # 控制器层
        │       │   ├── entity/                # 实体类
        │       │   ├── mapper/                # 数据访问层
        │       │   └── service/               # 业务逻辑层
        │       ├── common/                    # 公共模块
        │       │   ├── annotation/            # 自定义注解
        │       │   ├── aspectj/               # AOP切面
        │       │   ├── constant/              # 常量定义
        │       │   ├── domain/                # 领域对象
        │       │   ├── enums/                 # 枚举类
        │       │   ├── exception/             # 异常处理
        │       │   └── utils/                 # 工具类
        │       ├── config/                    # 配置类
        │       │   ├── CacheConfig.java       # 缓存配置
        │       │   ├── CorsConfig.java        # 跨域配置
        │       │   ├── MyMetaObjectHandler.java # MyBatis自动填充
        │       │   ├── SecurityConfig.java    # 安全配置
        │       │   └── SwaggerConfig.java     # API文档配置
        │       ├── monitor/                   # 系统监控模块
        │       │   ├── controller/            # 控制器层
        │       │   ├── entity/                # 实体类
        │       │   └── service/               # 业务逻辑层
        │       ├── security/                  # 安全模块
        │       │   ├── AccessDeniedHandlerImpl.java
        │       │   ├── AuthenticationEntryPointImpl.java
        │       │   ├── JwtProperties.java
        │       │   ├── JwtTokenFilter.java
        │       │   ├── LoginUser.java
        │       │   ├── LogoutSuccessHandlerImpl.java
        │       │   ├── PermissionService.java
        │       │   └── UserDetailsServiceImpl.java
        │       └── system/                    # 系统管理模块
        │           ├── controller/            # 控制器层
        │           ├── entity/                # 实体类
        │           ├── mapper/                # 数据访问层
        │           └── service/               # 业务逻辑层
        └── resources/
            ├── application.yml                # 主配置文件
            ├── application-druid.yml          # 数据源配置
            └── mapper/                        # MyBatis XML映射文件
```

## 环境要求

- **JDK**: 1.8+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **操作系统**: Windows / Linux / macOS

## 安装部署步骤

### 1. 克隆项目

```bash
git clone <repository-url>
cd inventory-backend
```

### 2. 数据库配置

```bash
# 创建数据库并导入初始数据
mysql -u root -p < sql/inventory_v2.sql
```

### 3. 修改配置文件

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory_v2?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 编译打包

```bash
# 编译项目
mvn clean compile

# 打包项目
mvn clean package -DskipTests
```

### 5. 运行项目

```bash
# 开发环境运行
mvn spring-boot:run

# 或者直接运行JAR包
java -jar target/inventory-backend.jar
```

### 6. Docker部署（可选）

```bash
# 构建镜像
docker build -t inventory-backend:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 --name inventory-backend inventory-backend:1.0.0
```

## 数据库配置

### 数据库连接参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| 数据库地址 | localhost:3306 | MySQL服务地址 |
| 数据库名称 | inventory_v2 | 数据库名 |
| 用户名 | root | 数据库用户名 |
| 密码 | root | 数据库密码 |
| 字符集 | utf8mb4 | 字符编码 |

### 数据库表结构

系统包含以下核心数据表：

**系统权限模块**
- `sys_user` - 用户表
- `sys_dept` - 部门表
- `sys_role` - 角色表
- `sys_menu` - 菜单权限表
- `sys_user_role` - 用户角色关联表
- `sys_role_menu` - 角色菜单关联表
- `sys_oper_log` - 操作日志表

**基础档案模块**
- `base_product_spu` - 商品标准单元表
- `base_product_sku` - 商品库存单元表
- `base_warehouse` - 仓库表
- `base_supplier` - 供应商表
- `base_customer` - 客户表

**业务模块**
- `stock_main` - 实时库存表
- `stock_record` - 库存流水表
- `bus_purchase_order` - 采购订单主表
- `bus_purchase_item` - 采购订单明细表
- `bus_sale_order` - 销售订单主表
- `bus_sale_item` - 销售订单明细表
- `bus_stock_check` - 库存盘点主表
- `bus_stock_check_item` - 盘点明细表

## 接口文档访问

项目集成了 Knife4j 接口文档工具，启动项目后可通过以下地址访问：

- **Knife4j文档**: http://localhost:8080/api/doc.html
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

### 接口认证

大部分接口需要 JWT Token 认证，获取Token步骤：

1. 调用登录接口：`POST /api/auth/login`
2. 在请求头中添加：`Authorization: Bearer {token}`

## 功能模块说明

### 1. 系统管理模块

| 功能 | 接口前缀 | 说明 |
|------|----------|------|
| 用户管理 | `/api/system/user` | 用户增删改查、重置密码、分配角色 |
| 角色管理 | `/api/system/role` | 角色增删改查、分配菜单权限 |
| 菜单管理 | `/api/system/menu` | 菜单增删改查、权限配置 |
| 部门管理 | `/api/system/dept` | 部门树形结构管理 |
| 操作日志 | `/api/system/log` | 操作日志查询 |

### 2. 基础档案模块

| 功能 | 接口前缀 | 说明 |
|------|----------|------|
| 商品管理 | `/api/base/product` | 商品SPU/SKU管理 |
| 仓库管理 | `/api/base/warehouse` | 仓库信息管理 |
| 供应商管理 | `/api/base/supplier` | 供应商信息管理 |
| 客户管理 | `/api/base/customer` | 客户信息管理 |

### 3. 业务模块

| 功能 | 接口前缀 | 说明 |
|------|----------|------|
| 采购管理 | `/api/business/purchase` | 采购订单管理、采购入库 |
| 销售管理 | `/api/business/sale` | 销售订单管理、销售出库 |
| 库存管理 | `/api/stock` | 实时库存查询、库存流水 |
| 库存盘点 | `/api/stock/check` | 库存盘点管理 |

### 4. 监控模块

| 功能 | 接口前缀 | 说明 |
|------|----------|------|
| 首页仪表盘 | `/api/monitor/dashboard` | 数据统计、图表展示 |
| 在线用户 | `/api/monitor/online` | 在线用户管理 |

## 作者信息

- **开发团队**: Graduation Team
- **联系方式**: example@example.com
- **创建时间**: 2024年

## 许可证

本项目仅供学习和研究使用。
