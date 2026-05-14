<div align="center">

# 🛡️ PermissionPro — 企業級權限管理系統

**基於 SSM + Apache Shiro 的全功能後台管理平台**

[![Java](https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring MVC](https://img.shields.io/badge/Spring_MVC-5.0.7-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.4.6-000000?style=for-the-badge&logo=mybatis&logoColor=white)](https://mybatis.org/)
[![Apache Shiro](https://img.shields.io/badge/Apache_Shiro-1.4.0-F26522?style=for-the-badge)](https://shiro.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Tomcat](https://img.shields.io/badge/Tomcat-8.5-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)

> 一個從零手刻的企業後台管理系統，深度整合 Spring MVC + MyBatis + Apache Shiro，實現員工管理、動態選單、角色權限指派與 AOP 操作日誌等完整功能。

**[🌐 線上展示 Demo](https://beesuperman.github.io/SSM-Shiro-Permission-System-my-frontend/)** 　

</div>

## ✨ 核心功能

### 🔐 認證與授權（Apache Shiro）
- **自訂 Realm**：`EmployeeRealm` 實現資料庫查詢的認證邏輯
- **密碼加密**：MD5 雙重 Hash（`HashIterations = 2`）
- **EhCache 快取**：快取授權資訊，降低資料庫查詢壓力
- **細粒度權限控制**：`@RequiresPermissions` 精確到每個按鈕操作（如 `employee:add`、`employee:edit`）
- **無權限頁面導向**：未授權請求自動跳轉或回傳 JSON 錯誤提示

### 👥 員工管理（Employee）
- 分頁查詢（整合 PageHelper）
- 新增 / 修改 / 離職停用（狀態管理）
- **Excel 匯出**：使用 Apache POI 匯出員工清單 `.xls`
- **Excel 匯入**：上傳範本並批次解析寫入資料庫
- **Excel 範本下載**：提供標準填寫範本

### 🧩 角色與選單管理
- 角色 CRUD（新增、修改、刪除）
- 動態選單樹（`getTreeData` API 回傳樹狀結構）
- 角色 ↔ 權限多對多關聯指派
- 員工 ↔ 角色多對多關聯指派

### 📋 操作日誌（AOP）
- 使用 Spring AOP `@Before` 切面，自動記錄每次寫操作
- 記錄欄位：**操作時間 / 操作員 IP / 目標方法全路徑 / 方法參數（JSON）**
- 與業務邏輯完全解耦，零侵入式設計

---

## 🏗️ 技術架構

```
PromissionPro/
├── src/main/java/com/itlike/
│   ├── web/                    # Controller 層（Spring MVC）
│   │   ├── EmployeeController  # 員工 CRUD + Excel 匯入匯出
│   │   ├── RoleController      # 角色管理 + 角色指派
│   │   ├── MenuController      # 動態選單
│   │   ├── PermissionController# 權限管理
│   │   ├── departmentController# 部門管理
│   │   ├── filter/             # 自訂 Shiro 表單過濾器
│   │   └── realm/              # 自訂 Shiro Realm
│   ├── service/                # Service 層（業務邏輯）
│   ├── mapper/                 # MyBatis Mapper 介面
│   ├── domain/                 # 實體類（Employee, Role, Permission, Menu...）
│   ├── aspect/                 # Spring AOP 操作日誌切面
│   ├── interceptor/            # Spring MVC 攔截器
│   └── util/                   # 工具類（RequestUtil 等）
├── src/main/resources/
│   ├── applicationContext.xml        # Spring 主設定
│   ├── application-mvc.xml           # Spring MVC 設定
│   ├── application-mybatis.xml       # MyBatis + 資料來源
│   ├── application-shiro.xml         # Shiro 安全設定
│   ├── db.properties                 # 資料庫連線設定
│   └── shiro-ehcache.xml             # EhCache 快取設定
└── src/main/webapp/
    ├── WEB-INF/
    │   └── views/              # JSP 視圖
    └── static/                 # 靜態資源（EasyUI / CSS / JS）
```

---

## 🛠️ 技術棧

| 層級 | 技術 | 版本 |
|---|---|---|
| 核心框架 | Spring MVC | 5.0.7.RELEASE |
| ORM 框架 | MyBatis | 3.4.6 |
| 安全框架 | Apache Shiro | 1.4.0 |
| 資料庫 | MySQL | 8.0 |
| 資料庫連線池 | Alibaba Druid | 1.0.14 |
| 分頁插件 | PageHelper | 4.1.4 |
| 前端 UI | jQuery EasyUI | - |
| 模板引擎 | JSP + JSTL | 1.2 |
| Excel 處理 | Apache POI | 4.0.1 |
| JSON 處理 | Jackson | 2.9.4 |
| 快取 | EhCache（via Shiro） | 1.4.0 |
| 建構工具 | Maven | - |
| 容器 | Tomcat | 8.5 |
| JDK | Java | 1.8 |

---

## 🚀 本地快速啟動

### 環境需求

- ☕ JDK 1.8+
- 📦 Maven 3.x
- 🐬 MySQL 8.0
- 🐱 Tomcat 8.5

### 步驟 1：Clone 專案

```bash
git clone https://github.com/BeeSuperman/SSM-Shiro-Permission-System-my.git
cd SSM-Shiro-Permission-System-my
```

### 步驟 2：建立資料庫

```sql
CREATE DATABASE promissionpro DEFAULT CHARACTER SET utf8mb4;
USE promissionpro;
-- 執行 SQL 腳本（位於 /sql/init.sql）
SOURCE sql/init.sql;
```

### 步驟 3：設定資料庫連線

編輯 `src/main/resources/db.properties`：

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/promissionpro?useSSL=false&serverTimezone=Asia/Taipei
jdbc.username=root
jdbc.password=你的密碼
```

### 步驟 4：Maven 建構

```bash
mvn clean package -DskipTests
```

### 步驟 5：部署至 Tomcat

將 `target/PromissionPro.war` 放入 Tomcat 的 `webapps/` 目錄，或使用 IntelliJ IDEA 設定 Tomcat 8.5 本地部署後直接啟動。

### 步驟 6：登入系統

```
URL:      http://localhost:8080/PromissionPro/
帳號:     admin
密碼:     admin123
```

---

## 🔑 Shiro 權限設計

本系統採用 **RBAC（Role-Based Access Control）** 模型：

```
Employee（員工）
    ↕ 多對多
Role（角色）
    ↕ 多對多
Permission（權限）
```

| 權限識別字 | 功能說明 |
|---|---|
| `employee:index` | 查看員工列表頁 |
| `employee:add` | 新增員工 |
| `employee:edit` | 修改員工資料 |
| `employee:delete` | 員工離職操作 |
| `role:*` | 角色管理相關操作 |
| `menu:*` | 選單管理相關操作 |

**密碼雜湊策略**：`MD5(MD5(明文密碼))` — 兩次迭代，儲存於資料庫前加密。

---

## 📌 亮點技術說明

### 1. 自訂 Shiro 表單過濾器
繼承 `FormAuthenticationFilter` 實現自訂登入邏輯，整合 Session 管理與登入失敗訊息回傳。

### 2. AOP 無侵入日誌記錄
```java
// 只需在 Spring XML 設定切入點，完全不修改業務程式碼
public void writeLog(JoinPoint joinPoint) {
    // 自動記錄：時間 / IP / 方法全路徑 / 參數 JSON
}
```

### 3. JSON 編碼問題修正
針對 Spring MVC 與 EasyUI 的 `406 Not Acceptable` 相容性問題，採用手動 `ObjectMapper` 序列化並設定 `produces = "text/html;charset=UTF-8"`，確保中文字符正確傳輸。

### 4. Excel 雙向整合
- **匯出**：Apache POI 動態生成 `.xls`，串流傳輸給瀏覽器下載
- **匯入**：解析上傳的 Excel 範本，逐行解析並寫入資料庫

---

## 📄 License

本專案為個人學習與求職展示用途。

---

<div align="center">

**Made with ❤️ — Spring MVC · MyBatis · Apache Shiro**

</div>
