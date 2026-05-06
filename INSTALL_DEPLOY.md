# Stock-Master 企业级智能库存管理系统 — 安装部署说明文档

> **文档版本**: V1.0
> **适用版本**: Stock-Master V2.0.0
> **编写日期**: 2026-05-06
> **操作系统支持**: Windows 10/11 (64位)、CentOS 7+/Ubuntu 18.04+ (64位)

---

## 目录

- [一、项目概述](#一项目概述)
- [二、环境要求与软件准备](#二环境要求与软件准备)
  - [2.1 硬件要求](#21-硬件要求)
  - [2.2 软件依赖清单](#22-软件依赖清单)
  - [2.3 JDK 1.8 安装与配置](#23-jdk-18-安装与配置)
  - [2.4 Maven 3.6 安装与配置](#24-maven-36-安装与配置)
  - [2.5 Node.js 安装与配置](#25-nodejs-安装与配置)
  - [2.6 MySQL 8.0 安装与配置](#26-mysql-80-安装与配置)
- [三、获取源码](#三获取源码)
- [四、数据库初始化](#四数据库初始化)
  - [4.1 创建数据库](#41-创建数据库)
  - [4.2 导入初始化脚本](#42-导入初始化脚本)
  - [4.3 验证数据库](#43-验证数据库)
- [五、后端部署](#五后端部署)
  - [5.1 项目结构说明](#51-项目结构说明)
  - [5.2 修改数据库配置](#52-修改数据库配置)
  - [5.3 编译打包](#53-编译打包)
  - [5.4 启动后端服务](#54-启动后端服务)
  - [5.5 验证后端服务](#55-验证后端服务)
  - [5.6 可选：使用 H2 内存数据库快速体验](#56-可选使用-h2-内存数据库快速体验)
- [六、前端部署](#六前端部署)
  - [6.1 安装前端依赖](#61-安装前端依赖)
  - [6.2 前端配置说明](#62-前端配置说明)
  - [6.3 启动前端开发服务器](#63-启动前端开发服务器)
  - [6.4 前端生产环境构建](#64-前端生产环境构建)
- [七、系统登录与验证](#七系统登录与验证)
- [八、Nginx 反向代理部署（生产环境）](#八nginx-反向代理部署生产环境)
  - [8.1 安装 Nginx](#81-安装-nginx)
  - [8.2 配置 Nginx](#82-配置-nginx)
  - [8.3 启动 Nginx](#83-启动-nginx)
- [九、常见问题与排查](#九常见问题与排查)
- [十、附录](#十附录)

---

## 一、项目概述

**Stock-Master（企业级智能库存管理系统）** 是一套采用前后端分离架构开发的完整库存管理解决方案，适用于中小企业的库存日常管理。系统基于 Java 后端 + Vue 前端的主流技术栈构建，涵盖了基础档案管理、采购管理、销售管理、库存管理、系统权限管理等核心业务模块。

### 技术架构总览

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| **前端** | Vue 2.6 + Element UI + Vuex + Vue Router | 渐进式 JavaScript 框架，饿了么 UI 组件库 |
| **后端** | Spring Boot 2.7.18 + Spring Security | 企业级 Java Web 开发框架 |
| **ORM** | MyBatis-Plus 3.5.3.1 | 增强 MyBatis 的 ORM 框架 |
| **数据库** | MySQL 8.0 | 关系型数据库（生产）/ H2 内存数据库（开发） |
| **连接池** | Druid 1.2.16 | 阿里巴巴数据库连接池 |
| **认证** | JWT 0.11.5 | 基于 Token 的无状态认证方案 |
| **缓存** | Caffeine 2.9.3 | 高性能本地缓存（无需额外安装 Redis） |
| **API 文档** | Knife4j 3.0.3 | Swagger 增强版 API 文档工具 |
| **数据可视化** | ECharts 5.4.3 | 百度开源数据可视化图表库 |

### 系统功能模块

- **系统管理**: 用户管理、角色管理、菜单管理、部门管理、操作日志
- **基础档案**: 商品 SPU/SKU 管理、仓库管理、供应商管理、客户管理
- **采购管理**: 采购订单创建与审核、采购入库
- **销售管理**: 销售订单创建与审核、销售出库
- **库存管理**: 实时库存查询、库存流水追踪、库存盘点
- **系统监控**: 在线用户监控、操作日志审计、数据可视化仪表盘

---

## 二、环境要求与软件准备

### 2.1 硬件要求

| 项目 | 最低配置 | 推荐配置 |
|------|---------|---------|
| CPU | 2 核 | 4 核及以上 |
| 内存 | 4 GB | 8 GB 及以上 |
| 磁盘空间 | 10 GB | 20 GB 及以上 |
| 网络 | 局域网环境 | 稳定的网络连接 |

### 2.2 软件依赖清单

| 软件 | 要求版本 | 用途 | 下载地址 |
|------|---------|------|---------|
| **JDK** | 1.8 (8u301+) | Java 运行环境 | [oracle.com/java/technologies/downloads](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.6+ (推荐 3.6.3) | Java 项目构建工具 | [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) |
| **Node.js** | 14+ (推荐 16.x) | 前端运行时环境 | [nodejs.org](https://nodejs.org/) |
| **MySQL** | 8.0+ (推荐 8.0.33) | 关系型数据库 | [dev.mysql.com/downloads](https://dev.mysql.com/downloads/) |
| **Nginx** (可选) | 1.18+ | 反向代理服务器（生产环境） | [nginx.org/en/download.html](https://nginx.org/en/download.html) |
| **Git** | 2.x | 版本控制工具 | [git-scm.com](https://git-scm.com/) |

> **说明**: 系统使用 Caffeine 本地缓存替代 Redis，因此无需额外安装 Redis 服务，大幅简化了部署流程。

### 2.3 JDK 1.8 安装与配置

#### Windows 系统

1. 下载 JDK 1.8 安装包（如 `jdk-8u301-windows-x64.exe`），双击运行安装程序，按提示完成安装。
2. 配置系统环境变量：
   - 新建系统变量 `JAVA_HOME`，值为 JDK 安装路径（例如 `C:\Program Files\Java\jdk1.8.0_301`）
   - 编辑系统变量 `Path`，新增 `%JAVA_HOME%\bin`
3. 验证安装：打开命令提示符，执行以下命令：

```bash
java -version
# 预期输出: java version "1.8.0_301"
javac -version
# 预期输出: javac 1.8.0_301
```

#### Linux 系统

```bash
# 使用 yum 安装 (CentOS)
sudo yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

# 或使用 apt 安装 (Ubuntu)
sudo apt update
sudo apt install -y openjdk-8-jdk

# 配置环境变量（编辑 ~/.bashrc 或 /etc/profile）
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# 使配置生效
source ~/.bashrc

# 验证
java -version
```

### 2.4 Maven 3.6 安装与配置

#### Windows 系统

1. 下载 Maven 安装包（如 `apache-maven-3.6.3-bin.zip`），解压到指定目录（例如 `D:\maven\apache-maven-3.6.3`）。
2. 配置系统环境变量：
   - 新建系统变量 `MAVEN_HOME`，值为 Maven 解压路径
   - 编辑系统变量 `Path`，新增 `%MAVEN_HOME%\bin`
3. （可选）配置 Maven 镜像源，编辑 `conf/settings.xml`，在 `<mirrors>` 节点中添加阿里云镜像：

```xml
<mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

4. 验证安装：

```bash
mvn -v
# 预期输出包含: Apache Maven 3.6.3
```

#### Linux 系统

```bash
# 下载并解压
cd /opt
sudo wget https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
sudo tar -zxvf apache-maven-3.6.3-bin.tar.gz

# 配置环境变量
export MAVEN_HOME=/opt/apache-maven-3.6.3
export PATH=$MAVEN_HOME/bin:$PATH

# 使配置生效
source ~/.bashrc

# 验证
mvn -v
```

### 2.5 Node.js 安装与配置

#### Windows 系统

1. 下载 Node.js 安装包（推荐 LTS 版本，如 `node-v16.20.2-x64.msi`），双击运行安装程序，按提示完成安装。npm 包管理器会随 Node.js 自动安装。
2. 验证安装：

```bash
node -v
# 预期输出: v16.20.2
npm -v
# 预期输出: 8.x.x
```

3. （可选）配置 npm 淘宝镜像加速：

```bash
npm config set registry https://registry.npmmirror.com
npm config get registry
# 预期输出: https://registry.npmmirror.com/
```

#### Linux 系统

```bash
# 使用 nvm 安装 (推荐)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.5/install.sh | bash
source ~/.bashrc
nvm install 16
nvm use 16

# 或直接安装
sudo apt install -y nodejs npm  # Ubuntu
sudo yum install -y nodejs npm  # CentOS

# 验证
node -v && npm -v
```

### 2.6 MySQL 8.0 安装与配置

#### Windows 系统

1. 下载 MySQL 安装包（如 `mysql-installer-community-8.0.33.msi`），运行安装程序。
2. 选择安装类型：
   - **Developer Default**（开发者默认，包含客户端工具）或
   - **Server only**（仅安装服务器）
3. 在配置向导中：
   - 设置 root 用户密码（请牢记此密码，后端配置中需要使用）
   - 端口保持默认 `3306`
   - 将 MySQL 配置为 Windows 服务，并设置为自动启动
4. 验证安装：

```bash
mysql -u root -p
# 输入密码后进入 MySQL 命令行
```

#### Linux 系统

```bash
# Ubuntu
sudo apt update
sudo apt install -y mysql-server-8.0
sudo systemctl start mysql
sudo systemctl enable mysql

# CentOS (需先添加 MySQL 仓库)
sudo yum localinstall https://dev.mysql.com/get/mysql80-community-release-el7-5.noarch.rpm
sudo yum install -y mysql-community-server
sudo systemctl start mysqld
sudo systemctl enable mysqld

# 获取临时密码 (CentOS)
sudo grep 'temporary password' /var/log/mysqld.log

# 修改 root 密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY '您的密码';
```

---

## 三、获取源码

### 方式一：通过 Git 克隆（推荐）

```bash
git clone https://github.com/LWJSTONE/Stock-Master.git
cd Stock-Master
```

### 方式二：直接下载 ZIP

访问项目 GitHub 页面 [https://github.com/LWJSTONE/Stock-Master](https://github.com/LWJSTONE/Stock-Master)，点击绿色 **"Code"** 按钮，选择 **"Download ZIP"**，下载后解压。

### 项目目录结构

```
Stock-Master/
├── inventory-backend/                  # 后端项目 (Spring Boot)
│   ├── pom.xml                         # Maven 依赖配置
│   ├── sql/                            # 数据库脚本
│   │   ├── complete_init.sql           # 完整初始化脚本（推荐使用）
│   │   ├── inventory_v2.sql            # 数据库结构脚本
│   │   ├── init_data.sql               # 初始数据脚本
│   │   └── patch_oper_log.sql          # 补丁脚本
│   └── src/main/
│       ├── java/com/graduation/inventory/
│       │   ├── InventoryApplication.java   # 启动类
│       │   ├── base/                      # 基础档案模块
│       │   ├── business/                  # 业务模块（采购/销售/库存）
│       │   ├── common/                    # 公共模块（工具类/异常/常量）
│       │   ├── config/                    # 配置类（安全/CORS/Swagger/缓存）
│       │   ├── monitor/                   # 监控模块（仪表盘/在线用户）
│       │   ├── security/                  # 安全模块（JWT/权限/认证）
│       │   └── system/                    # 系统模块（用户/角色/菜单/部门）
│       └── resources/
│           ├── application.yml            # 主配置文件
│           ├── application-druid.yml      # Druid 数据源配置
│           ├── application-h2.yml         # H2 内存数据库配置
│           └── mapper/                    # MyBatis XML 映射文件
│
├── inventory-frontend/                 # 前端项目 (Vue 2)
│   ├── package.json                    # Node.js 依赖配置
│   ├── vue.config.js                   # Vue CLI 配置
│   ├── babel.config.js                 # Babel 转译配置
│   └── src/
│       ├── main.js                     # 入口文件
│       ├── App.vue                     # 根组件
│       ├── router/index.js             # 路由配置
│       ├── store/                      # Vuex 状态管理
│       ├── api/                        # API 接口定义
│       ├── utils/                      # 工具函数
│       ├── directives/                 # 自定义指令（按钮权限）
│       ├── layout/                     # 布局组件
│       ├── views/                      # 页面视图
│       │   ├── login/                  # 登录页
│       │   ├── dashboard/              # 仪表盘
│       │   ├── system/                 # 系统管理
│       │   ├── base/                   # 基础档案
│       │   ├── business/               # 业务管理（采购/销售/入库/出库）
│       │   ├── stock/                  # 库存管理
│       │   └── monitor/                # 系统监控
│       └── components/                 # 公共组件
│
├── docs/                               # 项目文档
│   ├── 01-本地部署指南.md
│   ├── 02-数据库设计文档.md
│   ├── 03-API接口文档.md
│   └── 04-系统使用说明书.md
├── build.sh                            # 后端构建脚本
└── README.md                           # 项目说明
```

---

## 四、数据库初始化

### 4.1 创建数据库

使用命令行连接 MySQL 后执行以下 SQL 命令：

```sql
CREATE DATABASE `inventory_v2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

或者使用图形化管理工具（如 Navicat、DBeaver、MySQL Workbench），新建数据库 `inventory_v2`，字符集选择 `utf8mb4`，排序规则选择 `utf8mb4_unicode_ci`。

### 4.2 导入初始化脚本

项目提供了两种数据库初始化方式：

#### 方式一：使用完整初始化脚本（推荐）

完整初始化脚本包含数据库结构、基础数据和补丁修复，一条命令即可完成全部初始化：

```bash
mysql -u root -p < inventory-backend/sql/complete_init.sql
```

#### 方式二：分步初始化

如需分步控制初始化过程，可以按顺序执行以下脚本：

```bash
# 步骤 1：导入数据库结构
mysql -u root -p inventory_v2 < inventory-backend/sql/inventory_v2.sql

# 步骤 2：导入初始数据
mysql -u root -p inventory_v2 < inventory-backend/sql/init_data.sql

# 步骤 3：执行补丁脚本（如需要）
mysql -u root -p inventory_v2 < inventory-backend/sql/patch_oper_log.sql
```

### 4.3 验证数据库

初始化完成后，连接数据库执行以下命令进行验证：

```sql
USE inventory_v2;

-- 检查表数量（预期约 21 张表）
SHOW TABLES;

-- 验证管理员账号
SELECT username, real_name FROM sys_user WHERE username = 'admin';
-- 预期输出: admin | 管理员

-- 验证基础数据
SELECT COUNT(*) AS spu_count FROM base_product_spu;   -- 预期: 10
SELECT COUNT(*) AS sku_count FROM base_product_sku;   -- 预期: 24
SELECT COUNT(*) AS warehouse_count FROM base_warehouse; -- 预期: 3
SELECT COUNT(*) AS supplier_count FROM base_supplier;  -- 预期: 6
SELECT COUNT(*) AS customer_count FROM base_customer;  -- 预期: 5
```

---

## 五、后端部署

### 5.1 项目结构说明

后端项目采用标准的 Maven 工程结构，基于 Spring Boot 2.7.18 构建，使用 MyBatis-Plus 作为 ORM 框架，Druid 作为数据库连接池，Spring Security + JWT 实现认证鉴权。系统分为六个核心模块：`system`（系统管理）、`base`（基础档案）、`business`（业务处理）、`stock`（库存管理）、`monitor`（系统监控）和 `security`（安全认证）。

### 5.2 修改数据库配置

根据实际环境修改后端数据库连接配置。需要编辑两个配置文件：

#### 文件一：`inventory-backend/src/main/resources/application.yml`

找到以下配置段并修改数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${DB_URL:jdbc:mysql://localhost:3306/inventory_v2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
```

**配置说明：**
- `url`: 将 `localhost` 替换为实际的 MySQL 服务器地址；如果 MySQL 端口不是默认的 3306，需一并修改
- `username`: 数据库用户名，默认为 `root`
- `password`: 数据库密码，请修改为您安装 MySQL 时设置的密码

#### 文件二：`inventory-backend/src/main/resources/application-druid.yml`

找到 Druid 连接池配置段并同步修改：

```yaml
spring:
  datasource:
    druid:
      url: ${DB_URL:jdbc:mysql://localhost:3306/inventory_v2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}
      username: ${DB_USERNAME:root}
      password: ${DB_PASSWORD:123456}
```

> **提示**: 两个文件中的数据库连接信息必须保持一致。系统支持通过环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖默认配置，适用于容器化部署等场景。

### 5.3 编译打包

在后端项目根目录 `inventory-backend/` 下执行 Maven 打包命令：

```bash
cd inventory-backend

# 执行 Maven 打包（跳过测试以加快速度）
mvn clean package -DskipTests
```

**执行过程说明：**
- 首次打包会自动下载所有依赖包，耗时取决于网络速度（通常 3~10 分钟）
- 打包成功后，`target/` 目录下会生成 `inventory-backend.jar` 可执行 JAR 文件
- 控制台输出 `BUILD SUCCESS` 表示打包成功

> **常见问题**: 如果网络缓慢导致依赖下载失败，可配置 Maven 阿里云镜像源（参见 2.4 节），或使用离线依赖包。

### 5.4 启动后端服务

#### 方式一：开发环境启动（推荐开发时使用）

```bash
cd inventory-backend
mvn spring-boot:run
```

#### 方式二：生产环境启动（运行打包后的 JAR 文件）

```bash
cd inventory-backend
java -jar target/inventory-backend.jar
```

#### 方式三：后台运行（Linux 生产环境）

```bash
nohup java -jar target/inventory-backend.jar \
  --spring.profiles.active=druid \
  > app.log 2>&1 &
```

#### 方式四：指定自定义配置启动

```bash
java -jar target/inventory-backend.jar \
  --server.port=8080 \
  --spring.datasource.url=jdbc:mysql://your-server:3306/inventory_v2?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai \
  --spring.datasource.username=root \
  --spring.datasource.password=your_password
```

### 5.5 验证后端服务

后端启动成功后，控制台会输出以下关键日志信息：

```
Started InventoryApplication in X.XXX seconds (JVM running for X.XXX)
```

通过以下方式验证服务是否正常运行：

| 验证方式 | 访问地址 | 预期结果 |
|---------|---------|---------|
| 基础连通测试 | `http://localhost:8080/api` | 返回 401 或页面（非连接拒绝） |
| API 接口文档 (Knife4j) | `http://localhost:8080/api/doc.html` | 显示 Swagger API 文档页面 |
| Druid 监控面板 | `http://localhost:8080/api/druid` | 显示 Druid 数据源监控页面（账号: admin / admin123） |

### 5.6 可选：使用 H2 内存数据库快速体验

如果暂时不想安装 MySQL，可以使用项目内置的 H2 内存数据库进行快速体验。只需切换 Spring Profile 即可：

```bash
# 启动时指定 H2 Profile
java -jar target/inventory-backend.jar --spring.profiles.active=h2
```

或者修改 `application.yml` 中的 `spring.profiles.active` 为 `h2`：

```yaml
spring:
  profiles:
    active: h2    # 将默认的 druid 改为 h2
```

使用 H2 模式启动后，数据库初始化脚本会自动执行，无需手动导入 SQL。H2 控制台可通过 `http://localhost:8080/api/h2-console` 访问（JDBC URL: `jdbc:h2:mem:inventory_v2`，用户名: `sa`，密码: 空）。

> **注意**: H2 内存数据库仅适用于开发测试，数据在服务重启后会丢失，生产环境请务必使用 MySQL。

---

## 六、前端部署

### 6.1 安装前端依赖

进入前端项目目录，使用 npm 安装所有依赖包：

```bash
cd inventory-frontend

# 安装依赖
npm install
```

如果网络较慢，可以使用淘宝镜像源加速：

```bash
npm install --registry=https://registry.npmmirror.com
```

**安装过程说明：**
- 首次安装需要下载约 300MB 的依赖包，请耐心等待
- 安装完成后，项目目录下会生成 `node_modules/` 文件夹
- 如果安装失败，可以尝试清除缓存后重新安装：

```bash
# 清除 npm 缓存
npm cache clean --force

# 删除残留文件
rm -rf node_modules package-lock.json   # Linux / macOS
# 或
rmdir /s /q node_modules & del package-lock.json  # Windows

# 重新安装
npm install
```

### 6.2 前端配置说明

#### 开发环境 API 代理配置

前端开发服务器默认通过 `vue.config.js` 中配置的代理将 `/api` 请求转发到后端服务：

```javascript
// inventory-frontend/vue.config.js
devServer: {
  port: 8081,                    // 前端开发服务器端口
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端服务地址
      changeOrigin: true,
      pathRewrite: {
        '^/api': '/api'
      }
    }
  }
}
```

**重要配置项：**
- `port`: 前端开发服务器端口，默认为 `8081`
- `target`: 后端 API 地址，如果后端部署在其他地址或端口，请相应修改

#### API 请求封装

前端所有 API 请求通过 `src/utils/request.js` 中的 Axios 实例统一管理，请求会自动携带 JWT Token（从 Cookie 中读取并添加到 `Authorization` 请求头）。

### 6.3 启动前端开发服务器

```bash
cd inventory-frontend
npm run serve
```

启动成功后控制台会输出：

```
  App running at:
  - Local:   http://localhost:8081/
  - Network: http://192.168.x.x:8081/
```

此时在浏览器中访问 `http://localhost:8081/` 即可看到系统登录页面。

> **开发提示**: 在开发模式下，前端代码的热更新功能已启用，修改 Vue 文件后浏览器会自动刷新显示最新效果。

### 6.4 前端生产环境构建

生产环境部署需要先构建前端静态文件：

```bash
cd inventory-frontend
npm run build
```

构建成功后，`inventory-frontend/dist/` 目录下会生成生产环境静态文件，包含 `index.html`、`static/` 目录下的 JS、CSS 等资源文件。将 `dist` 目录下的所有文件部署到 Nginx 或其他 Web 服务器即可。

---

## 七、系统登录与验证

系统启动完成后，在浏览器中访问前端地址（开发环境为 `http://localhost:8081`），使用以下默认管理员账号登录：

| 项目 | 值 |
|------|-----|
| 访问地址 | `http://localhost:8081`（开发环境） |
| 默认用户名 | `admin` |
| 默认密码 | `123456` |

### 功能验证清单

登录成功后可按以下清单验证各模块功能是否正常：

**系统管理模块**
- [ ] 用户管理：查看用户列表，测试新增、编辑、删除、重置密码、角色分配
- [ ] 角色管理：查看角色列表，测试角色配置和菜单权限分配
- [ ] 菜单管理：查看菜单树，测试菜单增删改和按钮权限配置
- [ ] 部门管理：查看部门树形结构，测试部门的增删改

**基础档案模块**
- [ ] 商品管理：查看 SPU/SKU 列表，测试新增商品和 SKU 规格配置
- [ ] 仓库管理：查看仓库列表，测试仓库信息维护
- [ ] 供应商管理：查看供应商列表，测试供应商档案管理
- [ ] 客户管理：查看客户列表，测试客户档案管理

**采购与销售模块**
- [ ] 采购管理：创建采购订单，执行审核，确认入库
- [ ] 销售管理：创建销售订单，执行审核，确认出库

**库存管理模块**
- [ ] 库存查询：查看实时库存列表，验证库存数据正确
- [ ] 库存盘点：创建盘点单，录入实际数量，确认盘点差异处理
- [ ] 库存预警：查看库存预警列表

**系统监控模块**
- [ ] 仪表盘：查看首页数据统计图表（库存概览、销售趋势、库存预警）
- [ ] 在线用户：查看当前在线用户列表
- [ ] 操作日志：查看系统操作日志记录

---

## 八、Nginx 反向代理部署（生产环境）

在生产环境中，推荐使用 Nginx 作为反向代理服务器，将前端静态文件和后端 API 统一通过 Nginx 提供服务。

### 8.1 安装 Nginx

```bash
# Ubuntu
sudo apt install -y nginx

# CentOS
sudo yum install -y nginx

# 验证
nginx -v
```

### 8.2 配置 Nginx

创建或修改 Nginx 配置文件（通常位于 `/etc/nginx/conf.d/` 或 `/etc/nginx/sites-available/`）：

```nginx
server {
    listen       80;
    server_name  localhost;  # 替换为您的域名或 IP

    # 前端静态文件
    location / {
        root   /home/z/my-project/Stock-Master/inventory-frontend/dist;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;  # Vue Router history 模式支持
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
        proxy_send_timeout 60s;
    }

    # Druid 监控（生产环境建议关闭或限制访问）
    location /api/druid/ {
        deny all;  # 安全考虑，默认拒绝外部访问
    }
}
```

### 8.3 启动 Nginx

```bash
# 测试配置文件语法
nginx -t

# 启动 Nginx
sudo systemctl start nginx
sudo systemctl enable nginx  # 设置开机自启

# 重新加载配置（修改配置后）
sudo nginx -s reload
```

配置完成后，通过 `http://您的IP或域名/` 即可访问系统，无需再指定端口号 8081。

---

## 九、常见问题与排查

### 后端启动失败

**问题：端口 8080 被占用**

错误信息：`Port 8080 was already in use`

解决方案：
```bash
# Windows: 查找并结束占用进程
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F

# Linux: 查找并结束占用进程
lsof -i :8080
kill -9 <进程ID>
```

或修改 `application.yml` 中的 `server.port` 为其他端口。

**问题：数据库连接失败**

错误信息：`Communications link failure` 或 `Access denied`

排查步骤：
1. 确认 MySQL 服务已启动（`systemctl status mysql` 或在 Windows 服务管理器中检查）
2. 检查 `application.yml` 和 `application-druid.yml` 中的数据库地址、用户名、密码是否正确
3. 确认数据库 `inventory_v2` 已创建且已导入初始化脚本
4. 检查 MySQL 是否允许远程连接（如为远程 MySQL）
5. 检查防火墙是否放行了 3306 端口

**问题：Java 版本不匹配**

错误信息：`Unsupported major.minor version 52.0` 或类似

解决方案：确认当前使用的是 JDK 1.8：
```bash
java -version
# 应输出 1.8.x 版本
echo $JAVA_HOME  # 确认 JAVA_HOME 指向正确的 JDK
```

### 前端启动失败

**问题：npm install 失败**

解决方案：
```bash
# 1. 清除缓存和残留文件
npm cache clean --force
rm -rf node_modules package-lock.json

# 2. 切换镜像源
npm config set registry https://registry.npmmirror.com

# 3. 重新安装
npm install
```

**问题：端口 8081 被占用**

修改 `inventory-frontend/vue.config.js` 中的 `devServer.port` 为其他未占用的端口。

**问题：前端页面空白或接口报错**

排查步骤：
1. 确认后端服务已正常启动且可以访问
2. 打开浏览器开发者工具（F12），检查 Console 和 Network 面板中的错误信息
3. 确认 `vue.config.js` 中代理的 `target` 地址与后端实际地址一致
4. 确认后端 CORS 跨域配置已启用（项目已配置 `CorsConfig`，默认允许所有来源）

### 其他问题

**问题：Maven 依赖下载缓慢或失败**

解决方案：配置 Maven 阿里云镜像源（参见 2.4 节），或手动下载缺少的依赖并安装到本地仓库。

**问题：API 文档页面无法访问**

确保 `application.yml` 中 Knife4j 配置为启用状态（`knife4j.enable: true`，`springfox.documentation.enabled: true`），且生产环境保护未开启（`knife4j.production: false`）。

---

## 十、附录

### A. 端口使用说明

| 服务 | 默认端口 | 说明 | 可修改配置文件 |
|------|---------|------|---------------|
| 后端 API 服务 | 8080 | Spring Boot 应用端口 | `application.yml` → `server.port` |
| 前端开发服务器 | 8081 | Vue CLI DevServer 端口 | `vue.config.js` → `devServer.port` |
| MySQL 数据库 | 3306 | 数据库默认端口 | MySQL 配置文件 `my.cnf` |
| Nginx (可选) | 80 | Web 服务器端口 | Nginx 配置文件 |
| Druid 监控面板 | 8080/api/druid | 数据源监控 | `application-druid.yml` |
| Knife4j API 文档 | 8080/api/doc.html | 接口文档 | `application.yml` |
| H2 控制台 (可选) | 8080/api/h2-console | 内存数据库控制台 | `application-h2.yml` |

### B. 环境变量配置参考

系统支持通过环境变量覆盖默认配置，适用于 Docker 容器部署或 CI/CD 场景：

| 环境变量名 | 说明 | 默认值 |
|-----------|------|--------|
| `DB_URL` | 数据库连接 URL | `jdbc:mysql://localhost:3306/inventory_v2?...` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `123456` |
| `DB_DRIVER` | 数据库驱动类名 | `com.mysql.cj.jdbc.Driver` |
| `JWT_SECRET` | JWT 签名密钥 | `inventory-management-system-jwt-secret-key-2024` |
| `JWT_EXPIRATION` | JWT 过期时间（毫秒） | `86400000`（24小时） |
| `FILE_UPLOAD_PATH` | 文件上传路径 | `/home/z/my-project/inventory-backend/uploads` |

### C. 日志文件位置

| 日志类型 | 位置 | 说明 |
|---------|------|------|
| 后端运行日志 | `inventory-backend/logs/inventory.log` | 主日志文件，记录 INFO 及以上级别 |
| 后端错误日志 | 控制台输出 | 在控制台查看 ERROR 级别日志 |
| 前端运行日志 | 浏览器开发者工具 Console | 按 F12 打开 |
| Druid 监控 | `http://localhost:8080/api/druid` | SQL 执行统计和慢查询监控 |

### D. 数据库表结构概览

系统共包含 **21 张数据表**，按模块划分如下：

| 模块 | 表名 | 说明 |
|------|------|------|
| 系统权限 | `sys_user` | 用户表 |
| 系统权限 | `sys_role` | 角色表 |
| 系统权限 | `sys_menu` | 菜单权限表 |
| 系统权限 | `sys_dept` | 部门表 |
| 系统权限 | `sys_user_role` | 用户-角色关联表 |
| 系统权限 | `sys_role_menu` | 角色-菜单关联表 |
| 系统权限 | `sys_oper_log` | 操作日志表 |
| 基础档案 | `base_category` | 商品分类表 |
| 基础档案 | `base_product_spu` | 商品 SPU 表 |
| 基础档案 | `base_product_sku` | 商品 SKU 表 |
| 基础档案 | `base_warehouse` | 仓库表 |
| 基础档案 | `base_supplier` | 供应商表 |
| 基础档案 | `base_customer` | 客户表 |
| 业务管理 | `bus_purchase_order` | 采购订单主表 |
| 业务管理 | `bus_purchase_item` | 采购订单明细表 |
| 业务管理 | `bus_sale_order` | 销售订单主表 |
| 业务管理 | `bus_sale_item` | 销售订单明细表 |
| 业务管理 | `bus_stock_check` | 库存盘点主表 |
| 业务管理 | `bus_stock_check_item` | 库存盘点明细表 |
| 库存管理 | `stock_main` | 实时库存表 |
| 库存管理 | `stock_record` | 库存流水表 |

### E. 快速启动命令速查

以下命令假设您已在项目根目录 `Stock-Master/` 下：

```bash
# === 数据库初始化（仅首次需要） ===
mysql -u root -p < inventory-backend/sql/complete_init.sql

# === 启动后端 ===
cd inventory-backend
mvn spring-boot:run
# 或使用 JAR 启动: java -jar target/inventory-backend.jar

# === 启动前端（新开一个终端） ===
cd inventory-frontend
npm install          # 仅首次需要
npm run serve

# === 访问系统 ===
# 前端: http://localhost:8081
# API 文档: http://localhost:8080/api/doc.html
# Druid 监控: http://localhost:8080/api/druid
# 默认账号: admin / 123456
```
