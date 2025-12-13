# 餐厅点餐管理系统

一个基于Java Swing的餐厅点餐管理系统，支持菜单浏览、订单管理和账单处理功能。

## 功能特性

- **菜单浏览**：分类显示菜品，支持搜索和筛选
- **订单管理**：添加、修改、删除订单项，实时计算总价
- **账单处理**：查看历史订单，处理支付，打印账单
- **数据存储**：使用H2数据库存储菜单和订单信息

## 系统要求

- Java 8 或更高版本
- Maven (用于构建和依赖管理)

## 数据库配置

本系统使用H2数据库作为数据存储，并使用HikariCP作为数据库连接池。

### 数据库配置文件

系统通过 `src/main/resources/database.properties` 文件进行数据库配置：

```properties
# 数据库连接配置
db.driver=org.h2.Driver
db.url=jdbc:h2:./restaurant_db
db.username=sa
db.password=
db.max.connections=10
db.connection.timeout=30000
```

### 数据库表结构

系统会自动创建以下数据表：

1. **menu_items** - 存储菜单项信息
   - id: 菜品ID (主键，自增)
   - name: 菜品名称
   - category: 菜品类别
   - price: 价格
   - description: 描述
   - image_path: 图片路径
   - available: 是否可点 (默认true)

2. **orders** - 存储订单信息
   - id: 订单ID (主键，自增)
   - order_time: 下单时间 (默认当前时间)
   - status: 订单状态
   - total_amount: 总金额

3. **order_items** - 存储订单详情
   - id: 订单项ID (主键，自增)
   - order_id: 订单ID (外键)
   - menu_item_id: 菜品ID (外键)
   - quantity: 数量
   - notes: 备注

### 数据库初始化

系统启动时会自动创建数据库表结构，并在菜单表为空时插入示例数据，包括：
- 开胃菜：宫保鸡丁、凉拌黄瓜
- 主菜：红烧肉、麻婆豆腐、清蒸鲈鱼
- 饮料：可乐、橙汁
- 甜点：红豆沙

## 启动方式

### 方法一：使用Maven（推荐）

```bash
# 编译并运行
mvn compile exec:java -Dexec.mainClass="com.restaurant.management.gui.RestaurantManagementSystem"

# 或者先打包再运行
mvn package
java -jar target/restaurant-ordering-system-1.0-SNAPSHOT.jar
```

### 方法二：直接编译运行

```bash
# 编译Java文件
javac -d target/classes -cp ".:lib/*" src/main/java/com/restaurant/management/gui/*.java

# 运行应用程序
java -cp "target/classes:lib/*" com.restaurant.management.gui.RestaurantManagementSystem
```

### 方法三：使用Maven运行插件

```bash
# 使用Maven exec插件运行
mvn exec:java -Dexec.mainClass="com.restaurant.management.gui.RestaurantManagementSystem"
```

## 使用方法

### 1. 系统功能使用

#### 菜单浏览
- 通过顶部下拉菜单选择分类筛选菜品
- 点击菜品查看详细信息
- 点击"添加到订单"将菜品添加到当前订单

#### 订单管理
- 查看当前订单详情
- 修改菜品数量或删除订单项
- 提交订单到厨房
- 添加订单备注

#### 账单管理
- 查看历史订单列表
- 选择订单查看详细信息
- 选择支付方式处理支付
- 打印账单

### 2. 数据库 CRUD 操作功能

系统提供了完整的菜单项数据库管理功能：

#### 创建菜单项 (Create)
- 使用 `addMenuItem(MenuItem item)` 方法添加新菜品
- 需要提供菜品名称、类别、价格、描述等信息
- 示例代码：
```java
MenuItem newItem = new MenuItem(0, "新菜品", "主菜", 35.00, "美味的新菜品", null);
boolean success = dbManager.addMenuItem(newItem);
if (success) {
    System.out.println("菜品添加成功");
}
```

#### 读取菜单项 (Read)
- 使用 `getAllMenuItems()` 获取所有可用菜品
- 使用 `getMenuItemById(int id)` 根据ID获取特定菜品
- 使用 `getMenuItemsByCategory(String category)` 按类别获取菜品
- 使用 `searchMenuItems(String searchTerm)` 搜索菜品

#### 更新菜单项 (Update)
- 使用 `updateMenuItem(MenuItem item)` 更新菜品信息
- 可以更新菜品名称、类别、价格、描述、可用状态等
- 示例代码：
```java
MenuItem itemToUpdate = dbManager.getMenuItemById(1);
if (itemToUpdate != null) {
    itemToUpdate.setName("更新后的菜品名");
    itemToUpdate.setPrice(39.99);
    boolean success = dbManager.updateMenuItem(itemToUpdate);
    if (success) {
        System.out.println("菜品更新成功");
    }
}
```

#### 删除菜单项 (Delete)
- 使用 `deleteMenuItem(int itemId)` 根据ID删除菜品
- 注意：删除操作不可逆，请谨慎使用
- 示例代码：
```java
boolean success = dbManager.deleteMenuItem(1);
if (success) {
    System.out.println("菜品删除成功");
}
```

#### 搜索功能
- 系统支持按菜品名称或描述搜索菜品
- 使用 `searchMenuItems(String searchTerm)` 方法
- 支持模糊搜索，提高用户体验

## 项目结构

```
src/
├── main/
│   └── java/
│       └── com/
│           └── restaurant/
│               └── management/
│                   └── gui/
│                       ├── RestaurantManagementSystem.java  // 主系统
│                       ├── MenuPanel.java                   // 菜单面板
│                       ├── OrderPanel.java                  // 订单面板
│                       ├── BillPanel.java                   // 账单面板
│                       ├── MenuItem.java                    // 菜单项类
│                       ├── OrderItem.java                   // 订单项类
│                       ├── Order.java                       // 订单类
│                       └── DatabaseManager.java             // 数据库管理类
```

## 技术栈

- Java Swing (GUI界面)
- H2 Database (数据存储)
- HikariCP (数据库连接池)
- SLF4J (日志框架)
- Maven (项目构建)

## 开发说明

本系统采用MVC架构模式:
- Model: MenuItem, OrderItem, Order, DatabaseManager
- View: MenuPanel, OrderPanel, BillPanel
- Controller: RestaurantManagementSystem