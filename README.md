# 餐厅点餐管理系统

一个基于Java Swing的餐厅点餐管理系统，支持菜单浏览、订单管理和账单处理功能。

## 功能特性

- **菜单浏览**：分类显示菜品，支持搜索和筛选
- **订单管理**：添加、修改、删除订单项，实时计算总价
- **账单处理**：查看历史订单，处理支付，打印账单
- **数据存储**：使用H2数据库存储菜单和订单信息

## 系统要求

- Java 8 或更高版本
- Maven (可选，用于构建)

## 使用方法

### 1. 编译项目

```bash
# 编译Java文件
javac -d target/classes -cp ".:lib/*" src/main/java/com/restaurant/management/gui/*.java
```

### 2. 运行应用程序

```bash
# 运行餐厅点餐管理系统
java -cp "target/classes:lib/*" com.restaurant.management.gui.RestaurantManagementSystem
```

### 3. 系统功能使用

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
- JDBC (数据库连接)

## 开发说明

本系统采用MVC架构模式:
- Model: MenuItem, OrderItem, Order, DatabaseManager
- View: MenuPanel, OrderPanel, BillPanel
- Controller: RestaurantManagementSystem