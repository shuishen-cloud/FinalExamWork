# Java图形界面餐厅点餐系统文档

## 项目概述

餐厅点餐系统是一个基于Java图形用户界面（GUI）开发的桌面应用程序，旨在简化餐厅的点餐流程。该系统为服务员提供了一个直观的界面来处理客户订单，包括菜单浏览、订单管理、账单计算等功能。

## 系统架构

### 项目结构
```
src/
├── main/
│   └── java/
│       └── com/
│           └── restaurant/
│               └── management/
│                   └── gui/
│                       ├── RestaurantManagementSystem.java      // 主类
│                       ├── MenuPanel.java                       // 菜单面板
│                       ├── OrderPanel.java                      // 订单面板
│                       ├── BillPanel.java                       // 账单面板
│                       ├── MenuItem.java                        // 菜单项类
│                       ├── Order.java                           // 订单类
│                       ├── OrderItem.java                       // 订单项类
│                       └── DatabaseManager.java                 // 数据库管理类
```

## 功能模块

### 1. 主界面 (RestaurantManagementSystem.java)

主界面是整个应用程序的入口点，提供以下功能：

- 系统标题和品牌标识
- 主导航菜单，包含菜单浏览、订单管理、账单查看等选项
- 状态栏显示系统信息
- 退出系统功能

### 2. 菜单面板 (MenuPanel.java)

菜单面板用于显示餐厅的菜单项，功能包括：

- 按类别（如开胃菜、主菜、饮料、甜点等）分类显示菜品
- 每个菜品包含名称、价格、描述和图片
- 搜索功能，方便快速查找特定菜品
- 菜品详情查看
- 一键添加菜品到当前订单

### 3. 订单面板 (OrderPanel.java)

订单面板用于管理当前订单，功能包括：

- 显示当前订单中的所有菜品及其数量
- 修改菜品数量（增加/减少/删除）
- 计算订单小计
- 订单备注功能（如特殊要求）
- 清空当前订单
- 提交订单到厨房

### 4. 账单面板 (BillPanel.java)

账单面板用于生成和显示订单账单，功能包括：

- 显示订单详细信息
- 计算总金额（包括税费和折扣）
- 支付方式选择（现金、信用卡、移动支付等）
- 打印账单功能
- 交易记录保存

## 核心类设计

### MenuItem.java - 菜单项类
```java
public class MenuItem {
    private int id;           // 菜品ID
    private String name;      // 菜品名称
    private String category;  // 菜品类别
    private double price;     // 价格
    private String description; // 描述
    private String imagePath;   // 图片路径
    private boolean available;  // 是否可点
    
    // 构造函数、getter和setter方法
}
```

### OrderItem.java - 订单项类
```java
public class OrderItem {
    private MenuItem menuItem;  // 关联的菜单项
    private int quantity;       // 数量
    private String notes;       // 备注
    
    // 构造函数、getter和setter方法
}
```

### Order.java - 订单类
```java
public class Order {
    private int orderId;              // 订单ID
    private List<OrderItem> items;    // 订单项列表
    private Date orderTime;           // 下单时间
    private String status;            // 订单状态（如“进行中”、“已完成”）
    private double subtotal;          // 小计
    private double tax;               // 税费
    private double total;             // 总计
    
    // 构造函数、getter和setter方法
    // 计算订单总额的方法
}
```

## GUI组件使用

### 使用的Java Swing组件

1. **JFrame** - 主窗口容器
2. **JPanel** - 面板容器，用于组织界面元素
3. **JButton** - 按钮组件，用于各种操作
4. **JLabel** - 标签组件，用于显示文本和图片
5. **JTable** - 表格组件，用于显示订单详情
6. **JScrollPane** - 滚动面板，用于处理大量内容
7. **JMenuBar/JMenu/JMenuItem** - 菜单组件
8. **JTextArea** - 多行文本区域，用于备注
9. **JSpinner** - 数量选择器

### 布局管理器

- **BorderLayout** - 用于主窗口布局
- **GridLayout** - 用于菜单项网格布局
- **FlowLayout** - 用于按钮面板布局
- **BoxLayout** - 用于垂直或水平布局
- **CardLayout** - 用于切换不同面板

## 数据库设计

### 主要表结构

1. **menu_items** - 存储菜单项信息
   - id (主键)
   - name (菜品名称)
   - category (类别)
   - price (价格)
   - description (描述)
   - available (是否可点)

2. **orders** - 存储订单信息
   - id (主键)
   - order_time (下单时间)
   - status (订单状态)
   - total_amount (总金额)

3. **order_items** - 存储订单详情
   - id (主键)
   - order_id (订单外键)
   - menu_item_id (菜品外键)
   - quantity (数量)
   - notes (备注)

## 系统流程

### 点餐流程
1. 服务员启动系统并登录
2. 浏览菜单并选择菜品
3. 添加菜品到当前订单
4. 修改订单（如需要）
5. 提交订单到厨房
6. 完成后生成账单
7. 客户支付并结束服务

## 特色功能

1. **实时库存跟踪** - 自动更新菜品可用性
2. **订单状态跟踪** - 实时更新订单状态
3. **快速搜索** - 快速查找特定菜品
4. **折扣和促销** - 支持折扣和优惠活动
5. **报表生成** - 销售报表和菜品流行度分析
6. **多语言支持** - 支持不同语言界面

## 技术特点

- 采用面向对象设计，代码结构清晰
- 使用MVC模式分离界面和业务逻辑
- 实现数据持久化存储
- 用户友好的图形界面设计
- 响应式布局，适应不同屏幕尺寸
- 错误处理和输入验证

## 编译与运行

### 环境要求
- Java 8 或更高版本
- Maven 或 Gradle（用于依赖管理）
- MySQL 或其他数据库（用于数据存储）

### 编译步骤
1. 确保已安装Java开发环境
2. 使用IDE（如IntelliJ IDEA或Eclipse）导入项目
3. 编译源代码
4. 配置数据库连接信息
5. 运行主类 `RestaurantManagementSystem.java`

## 测试计划

1. 单元测试 - 测试各个类的功能
2. 集成测试 - 测试模块间的交互
3. GUI测试 - 测试用户界面的响应
4. 用户验收测试 - 验证系统满足业务需求

## 扩展性考虑

- 模块化设计便于功能扩展
- 数据库设计支持多餐厅管理
- 可集成支付网关
- 可扩展移动应用版本
- 可添加在线点餐功能