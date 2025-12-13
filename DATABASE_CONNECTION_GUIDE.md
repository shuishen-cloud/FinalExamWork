# 餐厅管理系统 - 数据库连接文档

## 1. 概述

餐厅管理系统使用 H2 嵌入式数据库进行数据存储。系统通过 `DatabaseManager` 类实现数据库连接、表初始化、数据操作等功能。H2 数据库是一个轻量级的 Java SQL 数据库引擎，适合桌面应用程序使用。

## 2. 数据库配置

### 2.1 连接参数

```java
private static final String DB_URL = "jdbc:h2:./restaurant_db";  // 数据库文件存储路径
private static final String DB_USER = "sa";                      // 数据库用户名
private static final String DB_PASSWORD = "";                    // 数据库密码（默认为空）
```

**说明：**
- `DB_URL`：使用 H2 嵌入式数据库，数据文件存储在项目根目录下的 `restaurant_db` 文件中
- `DB_USER`：H2 数据库默认管理员用户 "sa"
- `DB_PASSWORD`：H2 默认密码为空

### 2.2 驱动类

```java
Class.forName("org.h2.Driver");  // 加载 H2 数据库驱动
```

## 3. 数据库表结构

### 3.1 菜单项表 (menu_items)

| 字段名        | 数据类型       | 说明                   |
|---------------|----------------|------------------------|
| id            | INT            | 主键，自动递增         |
| name          | VARCHAR(255)   | 菜品名称（必填）       |
| category      | VARCHAR(100)   | 菜品分类（必填）       |
| price         | DECIMAL(10,2)  | 价格（必填）           |
| description   | TEXT           | 菜品描述               |
| image_path    | VARCHAR(500)   | 图片路径               |
| available     | BOOLEAN        | 是否可用（默认 TRUE）  |

### 3.2 订单表 (orders)

| 字段名        | 数据类型       | 说明                   |
|---------------|----------------|------------------------|
| id            | INT            | 主键，自动递增         |
| order_time    | TIMESTAMP      | 下单时间（默认当前时间）|
| status        | VARCHAR(50)    | 订单状态（必填）       |
| total_amount  | DECIMAL(10,2)  | 订单总金额（必填）     |

### 3.3 订单项表 (order_items)

| 字段名        | 数据类型       | 说明                   |
|---------------|----------------|------------------------|
| id            | INT            | 主键，自动递增         |
| order_id      | INT            | 订单ID（外键）         |
| menu_item_id  | INT            | 菜品ID（外键）         |
| quantity      | INT            | 数量（必填）           |
| notes         | TEXT           | 备注                   |

## 4. 数据库初始化

### 4.1 自动建表

当 `DatabaseManager` 类实例化时，会自动执行以下操作：

1. 连接到数据库
2. 创建 `menu_items`、`orders`、`order_items` 三张表（如果不存在）
3. 检查 `menu_items` 表是否为空，如果为空则插入示例数据

### 4.2 示例数据

系统会自动插入以下示例菜品：

- **开胃菜**：宫保鸡丁、凉拌黄瓜
- **主菜**：红烧肉、麻婆豆腐、清蒸鲈鱼
- **饮料**：可乐、橙汁
- **甜点**：红豆沙

## 5. 主要功能

### 5.1 连接管理

```java
// 创建连接
public void connect() throws SQLException

// 关闭连接
public void closeConnection()
```

### 5.2 数据操作

#### 获取数据
- `getAllMenuItems()`: 获取所有可用菜单项
- `getMenuItemsByCategory(String category)`: 按分类获取菜单项
- `getAllOrders()`: 获取所有订单
- `getOrderItemsByOrderId(int orderId)`: 获取指定订单的订单项
- `getMenuItemById(int id)`: 根据ID获取菜单项

#### 保存数据
- `saveOrder(Order order)`: 保存订单到数据库

## 6. 配置和部署

### 6.1 依赖项

项目使用 Maven 管理依赖，在 `pom.xml` 中需要包含 H2 数据库驱动：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
</dependency>
```

### 6.2 数据库文件

- 数据库文件：`restaurant_db.mv.db`
- 位置：项目根目录
- 该文件包含了所有数据，可以备份或迁移

## 7. 常见问题

### 7.1 连接问题
- 确保 H2 驱动已正确加载
- 检查数据库文件路径是否正确
- 确认数据库文件没有被其他进程占用

### 7.2 权限问题
- 确保应用程序有读写数据库文件的权限
- 检查项目目录的访问权限

### 7.3 数据库锁定
- H2 数据库在同一时间只允许一个连接
- 确保在使用完毕后调用 `closeConnection()` 方法

## 8. 扩展建议

### 8.1 连接池
对于生产环境，建议使用连接池管理数据库连接，如 HikariCP。

### 8.2 数据库迁移
可以使用 Flyway 或 Liquibase 管理数据库版本和迁移。

### 8.3 备份策略
- 定期备份 `restaurant_db.mv.db` 文件
- 考虑实现自动备份功能