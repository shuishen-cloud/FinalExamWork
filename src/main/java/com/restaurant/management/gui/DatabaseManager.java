package com.restaurant.management.gui;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据库管理类
 */
public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        try {
            // 加载配置文件
            Properties props = new Properties();
            try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    throw new IOException("无法找到 database.properties 配置文件");
                }
                props.load(input);
            }

            // 配置 HikariCP 连接池
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("db.driver", "org.h2.Driver"));
            config.setJdbcUrl(props.getProperty("db.url", "jdbc:h2:./restaurant_db"));
            config.setUsername(props.getProperty("db.username", "sa"));
            config.setPassword(props.getProperty("db.password", ""));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.max.connections", "10")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.connection.timeout", "30000")));
            
            // 其他连接池配置
            config.setMinimumIdle(2);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatabaseManager() {
        initializeDatabase();
    }

    // 初始化数据库表
    public void initializeDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // 创建菜单项表
            String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(100) NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "description TEXT, " +
                    "image_path VARCHAR(500), " +
                    "available BOOLEAN DEFAULT TRUE)";
            statement.execute(createMenuItemsTable);

            // 创建订单表
            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "total_amount DECIMAL(10,2) NOT NULL)";
            statement.execute(createOrdersTable);

            // 创建订单项表
            String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT, " +
                    "menu_item_id INT, " +
                    "quantity INT NOT NULL, " +
                    "notes TEXT, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                    "FOREIGN KEY (menu_item_id) REFERENCES menu_items(id))";
            statement.execute(createOrderItemsTable);

            // 插入一些示例菜单数据（如果表为空）
            try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM menu_items")) {
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insertSampleMenuItems(connection);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 插入示例菜单项
    private void insertSampleMenuItems(Connection connection) {
        try {
            String sql = "INSERT INTO menu_items (name, category, price, description) VALUES (?, ?, ?, ?)";
            
            PreparedStatement ps = connection.prepareStatement(sql);
            
            // 开胃菜
            ps.setString(1, "宫保鸡丁");
            ps.setString(2, "开胃菜");
            ps.setDouble(3, 28.00);
            ps.setString(4, "经典川菜，鸡肉嫩滑，花生香脆");
            ps.executeUpdate();
            
            ps.setString(1, "凉拌黄瓜");
            ps.setString(2, "开胃菜");
            ps.setDouble(3, 12.00);
            ps.setString(4, "清爽黄瓜，蒜香调味");
            ps.executeUpdate();
            
            // 主菜
            ps.setString(1, "红烧肉");
            ps.setString(2, "主菜");
            ps.setDouble(3, 48.00);
            ps.setString(4, "经典中式菜肴，肥瘦相间");
            ps.executeUpdate();
            
            ps.setString(1, "麻婆豆腐");
            ps.setString(2, "主菜");
            ps.setDouble(3, 26.00);
            ps.setString(4, "嫩滑豆腐配麻辣肉末");
            ps.executeUpdate();
            
            ps.setString(1, "清蒸鲈鱼");
            ps.setString(2, "主菜");
            ps.setDouble(3, 68.00);
            ps.setString(4, "新鲜鲈鱼，清淡鲜美");
            ps.executeUpdate();
            
            // 饮料
            ps.setString(1, "可乐");
            ps.setString(2, "饮料");
            ps.setDouble(3, 6.00);
            ps.setString(4, "冰镇可乐");
            ps.executeUpdate();
            
            ps.setString(1, "橙汁");
            ps.setString(2, "饮料");
            ps.setDouble(3, 12.00);
            ps.setString(4, "新鲜橙汁");
            ps.executeUpdate();
            
            // 甜点
            ps.setString(1, "红豆沙");
            ps.setString(2, "甜点");
            ps.setDouble(3, 15.00);
            ps.setString(4, "传统中式甜品");
            ps.executeUpdate();
            
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取所有菜单项
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM menu_items WHERE available = TRUE")) {
            
            while (rs.next()) {
                MenuItem item = new MenuItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getString("image_path")
                );
                menuItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // 按类别获取菜单项
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM menu_items WHERE category = ? AND available = TRUE")) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path")
                    );
                    menuItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM orders ORDER BY order_time DESC")) {
            
            while (rs.next()) {
                Order order = new Order(rs.getInt("id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setStatus(rs.getString("status"));
                
                // 获取订单项
                List<OrderItem> orderItems = getOrderItemsByOrderId(order.getOrderId());
                order.setItems(orderItems);
                
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // 获取指定订单的订单项
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM order_items WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int menuItemId = rs.getInt("menu_item_id");
                    int quantity = rs.getInt("quantity");
                    String notes = rs.getString("notes");
                    
                    // 获取菜单项详情
                    MenuItem menuItem = getMenuItemById(menuItemId);
                    if (menuItem != null) {
                        OrderItem orderItem = new OrderItem(menuItem, quantity, notes);
                        orderItems.add(orderItem);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    // 根据ID获取菜单项
    public MenuItem getMenuItemById(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM menu_items WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 保存订单到数据库
    public void saveOrder(Order order) {
        try (Connection connection = dataSource.getConnection()) {
            // 开启事务
            connection.setAutoCommit(false);
            
            try {
                // 插入订单主表
                String orderSql = "INSERT INTO orders (status, total_amount) VALUES (?, ?)";
                try (PreparedStatement orderPs = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    orderPs.setString(1, order.getStatus());
                    orderPs.setDouble(2, order.getTotal());
                    
                    int affectedRows = orderPs.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = orderPs.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int orderId = generatedKeys.getInt(1);
                                order.setOrderId(orderId);
                                
                                // 插入订单项表
                                String itemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, notes) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement itemPs = connection.prepareStatement(itemSql)) {
                                    for (OrderItem item : order.getItems()) {
                                        itemPs.setInt(1, orderId);
                                        itemPs.setInt(2, item.getMenuItem().getId());
                                        itemPs.setInt(3, item.getQuantity());
                                        itemPs.setString(4, item.getNotes());
                                        itemPs.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
                
                // 提交事务
                connection.commit();
            } catch (SQLException e) {
                // 回滚事务
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 关闭数据库连接
    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}