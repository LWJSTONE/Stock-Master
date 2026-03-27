const { Document, Paragraph, TextRun, Table, TableRow, TableCell, WidthType, BorderStyle, AlignmentType, HeadingLevel, PageBreak, Header, Footer, PageNumber, NumberFormat, Tab, convertInchesToTwip, TableOfContents, StyleLevel, LevelFormat, Packer } = require('docx');
const fs = require('fs');

// 创建文档
const doc = new Document({
  sections: [
    // ==================== 封面 ====================
    {
      properties: {
        page: {
          margin: {
            top: convertInchesToTwip(1),
            right: convertInchesToTwip(1),
            bottom: convertInchesToTwip(1),
            left: convertInchesToTwip(1),
          },
        },
      },
      children: [
        // 空行
        new Paragraph({ spacing: { before: 2000 } }),
        // 主标题
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '企业级智能库存管理系统',
              bold: true,
              size: 56,
              font: 'SimHei',
            }),
          ],
        }),
        // 版本号
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 400 },
          children: [
            new TextRun({
              text: 'V2.0.0',
              bold: true,
              size: 44,
              font: 'SimHei',
            }),
          ],
        }),
        // 空行
        new Paragraph({ spacing: { before: 2000 } }),
        // 分隔线
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━',
              size: 24,
              color: '1E90FF',
            }),
          ],
        }),
        // 副标题
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 600 },
          children: [
            new TextRun({
              text: '本地部署零基础傻瓜式指南',
              bold: true,
              size: 36,
              font: 'SimHei',
              color: '333333',
            }),
          ],
        }),
        // 副标题说明
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200 },
          children: [
            new TextRun({
              text: '（Windows一键部署）',
              size: 28,
              font: 'SimHei',
              color: '666666',
            }),
          ],
        }),
        // 分隔线
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 600 },
          children: [
            new TextRun({
              text: '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━',
              size: 24,
              color: '1E90FF',
            }),
          ],
        }),
        // 空行
        new Paragraph({ spacing: { before: 3000 } }),
        // 版本信息
        new Paragraph({
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({
              text: '版本：V2.0.0',
              size: 24,
              font: 'SimSun',
            }),
          ],
        }),
        // 日期
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { before: 200 },
          children: [
            new TextRun({
              text: '日期：2026年3月',
              size: 24,
              font: 'SimSun',
            }),
          ],
        }),
      ],
    },
    // ==================== 目录 ====================
    {
      properties: {
        page: {
          margin: {
            top: convertInchesToTwip(1),
            right: convertInchesToTwip(1),
            bottom: convertInchesToTwip(1),
            left: convertInchesToTwip(1),
          },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              alignment: AlignmentType.CENTER,
              children: [
                new TextRun({
                  text: '企业级智能库存管理系统 V2.0.0 - 本地部署指南',
                  size: 18,
                  font: 'SimSun',
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
                  size: 18,
                  font: 'SimSun',
                }),
                new TextRun({
                  children: [PageNumber.CURRENT],
                  size: 18,
                }),
                new TextRun({
                  text: ' 页',
                  size: 18,
                  font: 'SimSun',
                }),
              ],
            }),
          ],
        }),
      },
      children: [
        // 目录标题
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 400 },
          children: [
            new TextRun({
              text: '目  录',
              bold: true,
              size: 36,
              font: 'SimHei',
            }),
          ],
        }),
        // 目录内容
        createTocEntry('第一章 部署前准备', 1),
        createTocEntry('1.1 系统要求', 2),
        createTocEntry('1.2 需要下载的软件清单', 2),
        createTocEntry('1.3 软件安装步骤', 2),
        createTocEntry('第二章 数据库初始化', 1),
        createTocEntry('2.1 创建数据库', 2),
        createTocEntry('2.2 导入初始化脚本', 2),
        createTocEntry('2.3 验证数据库', 2),
        createTocEntry('第三章 后端部署', 1),
        createTocEntry('3.1 配置数据库连接', 2),
        createTocEntry('3.2 编译打包', 2),
        createTocEntry('3.3 启动后端服务', 2),
        createTocEntry('第四章 前端部署', 1),
        createTocEntry('4.1 安装依赖', 2),
        createTocEntry('4.2 配置后端地址', 2),
        createTocEntry('4.3 启动前端服务', 2),
        createTocEntry('第五章 系统登录与验证', 1),
        createTocEntry('5.1 登录系统', 2),
        createTocEntry('5.2 功能验证清单', 2),
        createTocEntry('第六章 常见问题排查', 1),
        createTocEntry('6.1 后端启动失败', 2),
        createTocEntry('6.2 前端启动失败', 2),
        createTocEntry('6.3 数据库连接失败', 2),
        createTocEntry('6.4 端口占用问题', 2),
        createTocEntry('附录', 1),
        createTocEntry('A. 端口使用说明', 2),
        createTocEntry('B. 环境变量配置详解', 2),
        createTocEntry('C. 日志文件位置', 2),
        new Paragraph({ spacing: { before: 400 } }),
        new PageBreak(),
      ],
    },
    // ==================== 正文内容 ====================
    {
      properties: {
        page: {
          margin: {
            top: convertInchesToTwip(1),
            right: convertInchesToTwip(1),
            bottom: convertInchesToTwip(1),
            left: convertInchesToTwip(1),
          },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              alignment: AlignmentType.CENTER,
              children: [
                new TextRun({
                  text: '企业级智能库存管理系统 V2.0.0 - 本地部署指南',
                  size: 18,
                  font: 'SimSun',
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
                  size: 18,
                  font: 'SimSun',
                }),
                new TextRun({
                  children: [PageNumber.CURRENT],
                  size: 18,
                }),
                new TextRun({
                  text: ' 页',
                  size: 18,
                  font: 'SimSun',
                }),
              ],
            }),
          ],
        }),
      },
      children: [
        // ==================== 第一章 ====================
        createChapterTitle('第一章 部署前准备'),
        
        createSectionTitle('1.1 系统要求'),
        createBodyParagraph('在开始部署之前，请确保您的计算机满足以下系统要求：'),
        createBodyParagraph('• 操作系统：Windows 10/11 (64位)', true),
        createBodyParagraph('• 内存：最低4GB，推荐8GB以上', true),
        createBodyParagraph('• 磁盘空间：至少10GB可用空间', true),
        createBodyParagraph('• 处理器：Intel Core i3 或同等性能以上', true),
        createBodyParagraph('• 网络：局域网环境（如需多用户访问）', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('1.2 需要下载的软件清单'),
        createBodyParagraph('请提前下载以下软件安装包：'),
        new Paragraph({ spacing: { before: 200 } }),
        
        // 软件清单表格
        createSoftwareTable(),
        new Paragraph({ spacing: { before: 200 } }),
        
        createBodyParagraph('注意：Redis替代方案使用内置的Caffeine本地缓存，无需额外安装Redis服务，大大简化了部署流程。'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('1.3 软件安装步骤'),
        createSubSectionTitle('1.3.1 JDK安装与环境变量配置'),
        createBodyParagraph('步骤1：双击运行 jdk-8u301-windows-x64.exe 安装程序'),
        createBodyParagraph('步骤2：选择安装目录（建议使用默认路径）'),
        createBodyParagraph('步骤3：配置环境变量'),
        createBodyParagraph('    • 新建系统变量 JAVA_HOME，值为JDK安装路径（如：C:\\Program Files\\Java\\jdk1.8.0_301）', true),
        createBodyParagraph('    • 编辑系统变量 Path，添加 %JAVA_HOME%\\bin', true),
        createBodyParagraph('步骤4：验证安装 - 打开命令提示符，输入 java -version'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSubSectionTitle('1.3.2 MySQL安装与配置'),
        createBodyParagraph('步骤1：运行 mysql-installer-community-8.0.33.msi'),
        createBodyParagraph('步骤2：选择 "Developer Default" 或 "Server only" 安装类型'),
        createBodyParagraph('步骤3：设置root用户密码（请牢记此密码，后续配置需要使用）'),
        createBodyParagraph('步骤4：配置MySQL服务为Windows服务，并设置为自动启动'),
        createBodyParagraph('步骤5：验证安装 - 打开命令提示符，输入 mysql -u root -p'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSubSectionTitle('1.3.3 Node.js安装'),
        createBodyParagraph('步骤1：运行 node-v16.20.2-x64.msi 安装程序'),
        createBodyParagraph('步骤2：选择安装目录（建议使用默认路径）'),
        createBodyParagraph('步骤3：安装完成后，npm会自动安装'),
        createBodyParagraph('步骤4：验证安装 - 打开命令提示符，输入 node -v 和 npm -v'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSubSectionTitle('1.3.4 Maven安装与配置'),
        createBodyParagraph('步骤1：解压 apache-maven-3.6.3-bin.zip 到指定目录（如：D:\\maven）'),
        createBodyParagraph('步骤2：配置环境变量'),
        createBodyParagraph('    • 新建系统变量 MAVEN_HOME，值为Maven解压路径', true),
        createBodyParagraph('    • 编辑系统变量 Path，添加 %MAVEN_HOME%\\bin', true),
        createBodyParagraph('步骤3：验证安装 - 打开命令提示符，输入 mvn -v'),
        
        new PageBreak(),
        
        // ==================== 第二章 ====================
        createChapterTitle('第二章 数据库初始化'),
        
        createSectionTitle('2.1 创建数据库'),
        createSubSectionTitle('2.1.1 启动MySQL服务'),
        createBodyParagraph('方式一：通过服务管理器'),
        createBodyParagraph('    • 按 Win + R，输入 services.msc', true),
        createBodyParagraph('    • 找到 MySQL 服务，确保状态为"正在运行"', true),
        createBodyParagraph('方式二：通过命令行'),
        createBodyParagraph('    • 以管理员身份运行命令提示符', true),
        createBodyParagraph('    • 输入：net start mysql', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSubSectionTitle('2.1.2 使用Navicat或命令行创建数据库'),
        createBodyParagraph('方式一：使用Navicat图形化工具'),
        createBodyParagraph('    • 打开Navicat，连接到MySQL服务器', true),
        createBodyParagraph('    • 右键点击连接名，选择"新建数据库"', true),
        createBodyParagraph('    • 数据库名：inventory_v2', true),
        createBodyParagraph('    • 字符集：utf8mb4', true),
        createBodyParagraph('    • 排序规则：utf8mb4_unicode_ci', true),
        createBodyParagraph('方式二：使用命令行'),
        createBodyParagraph('    • 打开命令提示符，输入：mysql -u root -p', true),
        createBodyParagraph('    • 输入密码后，执行以下SQL命令：', true),
        createCodeBlock('CREATE DATABASE inventory_v2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('2.2 导入初始化脚本'),
        createBodyParagraph('步骤1：定位SQL脚本文件'),
        createBodyParagraph('    脚本位置：inventory-backend/sql/inventory_v2.sql', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：使用Navicat导入'),
        createBodyParagraph('    • 右键点击 inventory_v2 数据库', true),
        createBodyParagraph('    • 选择"运行SQL文件"', true),
        createBodyParagraph('    • 选择 inventory_v2.sql 文件', true),
        createBodyParagraph('    • 点击"开始"执行导入', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤3：或使用命令行导入'),
        createCodeBlock('mysql -u root -p inventory_v2 < inventory_v2.sql'),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('2.3 验证数据库'),
        createBodyParagraph('步骤1：检查表结构'),
        createBodyParagraph('导入成功后，数据库应包含以下表（共20张表）：'),
        new Paragraph({ spacing: { before: 100 } }),
        createTableList(),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：检查初始数据'),
        createBodyParagraph('    • 系统预置了管理员账号', true),
        createBodyParagraph('    • 用户名：admin', true),
        createBodyParagraph('    • 密码：123456', true),
        createBodyParagraph('    • 包含基础菜单和权限数据', true),
        
        new PageBreak(),
        
        // ==================== 第三章 ====================
        createChapterTitle('第三章 后端部署'),
        
        createSectionTitle('3.1 配置数据库连接'),
        createBodyParagraph('步骤1：打开配置文件'),
        createBodyParagraph('    文件位置：inventory-backend/src/main/resources/application-druid.yml', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：修改数据库连接配置'),
        createCodeBlock(`spring:
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/inventory_v2?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
      username: root
      password: 您设置的MySQL密码`),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('配置项说明：'),
        createBodyParagraph('    • url：数据库连接地址，localhost表示本机，3306为MySQL默认端口', true),
        createBodyParagraph('    • username：数据库用户名，默认为root', true),
        createBodyParagraph('    • password：数据库密码，请修改为您设置的密码', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('3.2 编译打包'),
        createBodyParagraph('步骤1：打开命令提示符'),
        createBodyParagraph('    切换到后端项目目录：cd inventory-backend', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：执行Maven打包命令'),
        createCodeBlock('mvn clean package -DskipTests'),
        createBodyParagraph('参数说明：'),
        createBodyParagraph('    • clean：清理之前的构建文件', true),
        createBodyParagraph('    • package：打包项目', true),
        createBodyParagraph('    • -DskipTests：跳过测试（加快打包速度）', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤3：等待打包完成'),
        createBodyParagraph('    • 首次打包会下载依赖，可能需要较长时间', true),
        createBodyParagraph('    • 打包成功后，在 target 目录下生成 inventory-backend.jar', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('3.3 启动后端服务'),
        createSubSectionTitle('3.3.1 命令行启动'),
        createBodyParagraph('方式一：使用Maven直接运行'),
        createCodeBlock('mvn spring-boot:run'),
        createBodyParagraph('方式二：运行打包后的JAR文件'),
        createCodeBlock('java -jar target/inventory-backend.jar'),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('3.3.2 验证服务状态'),
        createBodyParagraph('启动成功后，控制台会显示类似以下信息：'),
        createCodeBlock(`Started InventoryApplication in X.XXX seconds
Application is running! Access URLs:
Local:      http://localhost:8080
Swagger:    http://localhost:8080/swagger-ui.html`),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('访问Swagger文档验证：'),
        createBodyParagraph('    打开浏览器访问 http://localhost:8080/swagger-ui.html', true),
        createBodyParagraph('    如能正常显示API文档页面，说明后端启动成功', true),
        
        new PageBreak(),
        
        // ==================== 第四章 ====================
        createChapterTitle('第四章 前端部署'),
        
        createSectionTitle('4.1 安装依赖'),
        createBodyParagraph('步骤1：打开命令提示符'),
        createBodyParagraph('    切换到前端项目目录：cd inventory-frontend', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：安装项目依赖'),
        createCodeBlock('npm install'),
        createBodyParagraph('或者使用国内镜像加速：'),
        createCodeBlock('npm install --registry=https://registry.npmmirror.com'),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤3：等待安装完成'),
        createBodyParagraph('    • 安装过程可能需要几分钟', true),
        createBodyParagraph('    • 安装完成后会生成 node_modules 目录', true),
        new Paragraph({ spacing: { before: 100 } }),
        createSubSectionTitle('常见问题解决'),
        createBodyParagraph('问题1：npm install 失败'),
        createBodyParagraph('    • 尝试删除 node_modules 目录和 package-lock.json 文件后重新安装', true),
        createBodyParagraph('    • 使用管理员权限运行命令提示符', true),
        createBodyParagraph('问题2：依赖下载缓慢'),
        createBodyParagraph('    • 使用淘宝镜像：npm config set registry https://registry.npmmirror.com', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('4.2 配置后端地址'),
        createBodyParagraph('步骤1：打开环境配置文件'),
        createBodyParagraph('    文件位置：inventory-frontend/.env.development', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：配置后端API地址'),
        createCodeBlock(`# 开发环境配置
VUE_APP_BASE_API = 'http://localhost:8080'`),
        createBodyParagraph('配置说明：'),
        createBodyParagraph('    • VUE_APP_BASE_API：后端服务地址', true),
        createBodyParagraph('    • 默认为 localhost:8080，与后端配置一致', true),
        createBodyParagraph('    • 如后端部署在其他地址，请相应修改', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('4.3 启动前端服务'),
        createBodyParagraph('步骤1：启动开发服务器'),
        createCodeBlock('npm run serve'),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤2：等待编译完成'),
        createBodyParagraph('编译成功后会显示：'),
        createCodeBlock(`App running at:
- Local:   http://localhost:8081
- Network: http://192.168.x.x:8081`),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('步骤3：访问系统'),
        createBodyParagraph('    打开浏览器访问 http://localhost:8081', true),
        createBodyParagraph('    如能正常显示登录页面，说明前端启动成功', true),
        
        new PageBreak(),
        
        // ==================== 第五章 ====================
        createChapterTitle('第五章 系统登录与验证'),
        
        createSectionTitle('5.1 登录系统'),
        createBodyParagraph('打开浏览器访问 http://localhost:8081，使用以下默认账号登录：'),
        new Paragraph({ spacing: { before: 100 } }),
        createLoginTable(),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('登录步骤：'),
        createBodyParagraph('    1. 在用户名输入框中输入 admin', true),
        createBodyParagraph('    2. 在密码输入框中输入 123456', true),
        createBodyParagraph('    3. 点击"登录"按钮', true),
        createBodyParagraph('    4. 成功后自动跳转至系统首页', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('5.2 功能验证清单'),
        createBodyParagraph('登录成功后，请按以下清单验证系统功能：'),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('5.2.1 系统管理功能'),
        createBodyParagraph('□ 用户管理：新增、编辑、删除用户', true),
        createBodyParagraph('□ 角色管理：角色分配、权限配置', true),
        createBodyParagraph('□ 菜单管理：菜单配置、权限控制', true),
        createBodyParagraph('□ 部门管理：组织架构管理', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('5.2.2 基础档案功能'),
        createBodyParagraph('□ 产品管理：产品信息维护', true),
        createBodyParagraph('□ 分类管理：产品分类管理', true),
        createBodyParagraph('□ 仓库管理：仓库信息管理', true),
        createBodyParagraph('□ 供应商管理：供应商档案管理', true),
        createBodyParagraph('□ 客户管理：客户档案管理', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('5.2.3 采购销售功能'),
        createBodyParagraph('□ 采购入库：采购订单创建、审核', true),
        createBodyParagraph('□ 销售出库：销售订单创建、审核', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('5.2.4 库存管理功能'),
        createBodyParagraph('□ 库存查询：实时库存查看', true),
        createBodyParagraph('□ 库存盘点：盘点单创建、盘点差异处理', true),
        createBodyParagraph('□ 库存预警：低库存、高库存预警', true),
        
        new PageBreak(),
        
        // ==================== 第六章 ====================
        createChapterTitle('第六章 常见问题排查'),
        
        createSectionTitle('6.1 后端启动失败'),
        createSubSectionTitle('问题：端口8080被占用'),
        createBodyParagraph('现象：启动时报错 "Port 8080 was already in use"'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    方式一：修改后端端口', true),
        createBodyParagraph('        在 application.yml 中修改 server.port 为其他端口', true),
        createBodyParagraph('    方式二：关闭占用端口的程序', true),
        createCodeBlock('netstat -ano | findstr :8080    # 查找占用进程\ntaskkill /PID 进程ID /F         # 结束进程'),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('问题：数据库连接失败'),
        createBodyParagraph('现象：启动时报错 "Communications link failure"'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    1. 确认MySQL服务已启动', true),
        createBodyParagraph('    2. 检查数据库连接配置是否正确', true),
        createBodyParagraph('    3. 确认数据库用户名密码正确', true),
        createBodyParagraph('    4. 检查防火墙是否阻止了3306端口', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('问题：Java版本不正确'),
        createBodyParagraph('现象：启动时报错 "Unsupported major.minor version"'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    1. 确认JDK版本为1.8', true),
        createBodyParagraph('    2. 检查JAVA_HOME环境变量配置', true),
        createBodyParagraph('    3. 运行 java -version 确认当前JDK版本', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('6.2 前端启动失败'),
        createSubSectionTitle('问题：npm install 失败'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    1. 删除 node_modules 目录和 package-lock.json', true),
        createBodyParagraph('    2. 清除npm缓存：npm cache clean --force', true),
        createBodyParagraph('    3. 重新执行 npm install', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('问题：Node Sass 编译失败'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    1. 安装构建工具：npm install -g windows-build-tools', true),
        createBodyParagraph('    2. 或使用 dart-sass 替代 node-sass', true),
        new Paragraph({ spacing: { before: 100 } }),
        
        createSubSectionTitle('问题：端口8081被占用'),
        createBodyParagraph('解决方案：'),
        createBodyParagraph('    在 vue.config.js 中修改 devServer.port', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('6.3 数据库连接失败'),
        createBodyParagraph('常见原因及解决方案：'),
        createBodyParagraph('    1. MySQL服务未启动 → 启动MySQL服务', true),
        createBodyParagraph('    2. 密码错误 → 检查配置文件中的密码', true),
        createBodyParagraph('    3. 数据库不存在 → 按第二章创建数据库', true),
        createBodyParagraph('    4. 防火墙阻止 → 添加MySQL到防火墙例外', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('6.4 端口占用问题'),
        createBodyParagraph('查看端口占用情况：'),
        createCodeBlock('netstat -ano | findstr :端口号'),
        createBodyParagraph('结束占用进程：'),
        createCodeBlock('taskkill /PID 进程ID /F'),
        
        new PageBreak(),
        
        // ==================== 附录 ====================
        createChapterTitle('附录'),
        
        createSectionTitle('A. 端口使用说明'),
        createPortTable(),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('B. 环境变量配置详解'),
        createEnvTable(),
        new Paragraph({ spacing: { before: 200 } }),
        
        createSectionTitle('C. 日志文件位置'),
        createBodyParagraph('后端日志：'),
        createBodyParagraph('    • 位置：inventory-backend/logs/', true),
        createBodyParagraph('    • 文件：inventory.log（主日志文件）', true),
        createBodyParagraph('    • 文件：inventory-error.log（错误日志）', true),
        new Paragraph({ spacing: { before: 100 } }),
        createBodyParagraph('前端日志：'),
        createBodyParagraph('    • 位置：浏览器开发者工具控制台（F12）', true),
        new Paragraph({ spacing: { before: 200 } }),
        
        createBodyParagraph('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━', false, true),
        createBodyParagraph('文档结束', false, true),
        createBodyParagraph('如有问题，请联系技术支持', false, true),
      ],
    },
  ],
});

// 辅助函数
function createTocEntry(text, level) {
  const indent = level === 1 ? 0 : 400;
  return new Paragraph({
    spacing: { before: 100, after: 100 },
    indent: { left: indent },
    children: [
      new TextRun({
        text: text,
        size: level === 1 ? 24 : 22,
        font: level === 1 ? 'SimHei' : 'SimSun',
        bold: level === 1,
      }),
    ],
  });
}

function createChapterTitle(text) {
  return new Paragraph({
    spacing: { before: 400, after: 300 },
    children: [
      new TextRun({
        text: text,
        bold: true,
        size: 36,
        font: 'SimHei',
        color: '1E3A8A',
      }),
    ],
  });
}

function createSectionTitle(text) {
  return new Paragraph({
    spacing: { before: 300, after: 200 },
    children: [
      new TextRun({
        text: text,
        bold: true,
        size: 28,
        font: 'SimHei',
        color: '1E40AF',
      }),
    ],
  });
}

function createSubSectionTitle(text) {
  return new Paragraph({
    spacing: { before: 200, after: 150 },
    children: [
      new TextRun({
        text: text,
        bold: true,
        size: 24,
        font: 'SimHei',
      }),
    ],
  });
}

function createBodyParagraph(text, indent = false, center = false) {
  return new Paragraph({
    spacing: { before: 100, after: 100, line: 360 },
    indent: indent ? { left: 400 } : undefined,
    alignment: center ? AlignmentType.CENTER : AlignmentType.LEFT,
    children: [
      new TextRun({
        text: text,
        size: 24,
        font: 'SimSun',
      }),
    ],
  });
}

function createCodeBlock(code) {
  const lines = code.split('\n');
  return new Paragraph({
    spacing: { before: 100, after: 100 },
    indent: { left: 400 },
    shading: { fill: 'F5F5F5' },
    children: lines.map((line, index) => 
      new TextRun({
        text: (index > 0 ? '\n' : '') + line,
        size: 20,
        font: 'Consolas',
        color: '333333',
      })
    ),
  });
}

function createSoftwareTable() {
  const headerStyle = { bold: true, size: 22, font: 'SimHei', color: 'FFFFFF' };
  const cellStyle = { size: 22, font: 'SimSun' };
  
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          createTableCell('软件', headerStyle, '1E3A8A'),
          createTableCell('版本', headerStyle, '1E3A8A'),
          createTableCell('用途', headerStyle, '1E3A8A'),
          createTableCell('下载地址', headerStyle, '1E3A8A'),
        ],
      }),
      createTableDataRow(['JDK', '1.8.0_301', 'Java运行环境', 'oracle.com'], cellStyle),
      createTableDataRow(['MySQL', '8.0.33', '数据库', 'mysql.com'], cellStyle),
      createTableDataRow(['Node.js', '16.20.2', '前端运行环境', 'nodejs.org'], cellStyle),
      createTableDataRow(['Maven', '3.6.3', '后端构建工具', 'maven.apache.org'], cellStyle),
      createTableDataRow(['Redis替代方案', 'Caffeine(内置)', '本地缓存', '无需安装'], cellStyle),
    ],
  });
}

function createTableCell(text, style, bgColor = null) {
  return new TableCell({
    shading: bgColor ? { fill: bgColor } : undefined,
    children: [
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text, ...style })],
      }),
    ],
  });
}

function createTableDataRow(data, style) {
  return new TableRow({
    children: data.map(text => createTableCell(text, style)),
  });
}

function createTableList() {
  const tables = [
    'sys_user（用户表）', 'sys_role（角色表）', 'sys_menu（菜单表）', 'sys_dept（部门表）',
    'sys_user_role（用户角色关联表）', 'sys_role_menu（角色菜单关联表）', 'sys_oper_log（操作日志表）',
    'base_category（分类表）', 'base_product_spu（产品SPU表）', 'base_product_sku（产品SKU表）',
    'base_warehouse（仓库表）', 'base_supplier（供应商表）', 'base_customer（客户表）',
    'bus_purchase_order（采购订单表）', 'bus_purchase_item（采购明细表）',
    'bus_sale_order（销售订单表）', 'bus_sale_item（销售明细表）',
    'bus_stock_check（盘点单表）', 'bus_stock_check_item（盘点明细表）',
    'stock_record（库存记录表）'
  ];
  
  return new Paragraph({
    spacing: { before: 100, after: 100 },
    children: tables.map((t, i) => 
      new TextRun({
        text: (i > 0 ? '、' : '') + t,
        size: 22,
        font: 'SimSun',
      })
    ),
  });
}

function createLoginTable() {
  const headerStyle = { bold: true, size: 22, font: 'SimHei', color: 'FFFFFF' };
  const cellStyle = { size: 22, font: 'SimSun' };
  
  return new Table({
    width: { size: 50, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          createTableCell('项目', headerStyle, '1E3A8A'),
          createTableCell('值', headerStyle, '1E3A8A'),
        ],
      }),
      createTableDataRow(['默认账号', 'admin'], cellStyle),
      createTableDataRow(['默认密码', '123456'], cellStyle),
    ],
  });
}

function createPortTable() {
  const headerStyle = { bold: true, size: 22, font: 'SimHei', color: 'FFFFFF' };
  const cellStyle = { size: 22, font: 'SimSun' };
  
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          createTableCell('服务', headerStyle, '1E3A8A'),
          createTableCell('端口', headerStyle, '1E3A8A'),
          createTableCell('说明', headerStyle, '1E3A8A'),
        ],
      }),
      createTableDataRow(['后端服务', '8080', 'Spring Boot应用端口'], cellStyle),
      createTableDataRow(['前端服务', '8081', 'Vue开发服务器端口'], cellStyle),
      createTableDataRow(['MySQL', '3306', 'MySQL数据库默认端口'], cellStyle),
    ],
  });
}

function createEnvTable() {
  const headerStyle = { bold: true, size: 22, font: 'SimHei', color: 'FFFFFF' };
  const cellStyle = { size: 22, font: 'SimSun' };
  
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: [
      new TableRow({
        tableHeader: true,
        children: [
          createTableCell('变量名', headerStyle, '1E3A8A'),
          createTableCell('示例值', headerStyle, '1E3A8A'),
          createTableCell('说明', headerStyle, '1E3A8A'),
        ],
      }),
      createTableDataRow(['JAVA_HOME', 'C:\\Program Files\\Java\\jdk1.8.0_301', 'JDK安装路径'], cellStyle),
      createTableDataRow(['MAVEN_HOME', 'D:\\maven\\apache-maven-3.6.3', 'Maven安装路径'], cellStyle),
      createTableDataRow(['Path', '%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin', '系统路径'], cellStyle),
    ],
  });
}

// 生成文档
Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync('/home/z/my-project/inventory-v2/docs/01-本地部署指南.docx', buffer);
  console.log('文档已生成: /home/z/my-project/inventory-v2/docs/01-本地部署指南.docx');
}).catch(err => {
  console.error('生成文档失败:', err);
});
