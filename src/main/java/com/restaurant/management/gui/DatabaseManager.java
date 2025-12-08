package com.restaurant.management.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./restaurant_db";  // 使用H2数据库，存储在本地文件
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            // 加载H2数据库驱动
            Class.forName("org.h2.Driver");
            connect();
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 连接数据库
    public void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // 初始化数据库表
    public void initializeDatabase() {
        try {
            Statement statement = connection.createStatement();

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
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM menu_items");
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                insertSampleMenuItems(statement);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 插入示例菜单项
    private void insertSampleMenuItems(Statement statement) {
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
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM menu_items WHERE available = TRUE");
            
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
            
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // 按类别获取菜单项
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM menu_items WHERE category = ? AND available = TRUE";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            
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
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM orders ORDER BY order_time DESC");
            
            while (rs.next()) {
                Order order = new Order(rs.getInt("id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setStatus(rs.getString("status"));
                
                // 获取订单项
                List<OrderItem> orderItems = getOrderItemsByOrderId(order.getOrderId());
                order.setItems(orderItems);
                
                orders.add(order);
            }
            
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // 获取指定订单的订单项
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
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
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    // 根据ID获取菜单项
    public MenuItem getMenuItemById(int id) {
        try {
            String sql = "SELECT * FROM menu_items WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                MenuItem item = new MenuItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getString("image_path")
                );
                rs.close();
                ps.close();
                return item;
            }
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 保存订单到数据库
    public void saveOrder(Order order) {
        try {
            // 插入订单主表
            String orderSql = "INSERT INTO orders (status, total_amount) VALUES (?, ?)";
            PreparedStatement orderPs = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setString(1, order.getStatus());
            orderPs.setDouble(2, order.getTotal());
            
            int affectedRows = orderPs.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = orderPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                    
                    // 插入订单项表
                    for (OrderItem item : order.getItems()) {
                        String itemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, notes) VALUES (?, ?, ?, ?)";
                        PreparedStatement itemPs = connection.prepareStatement(itemSql);
                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2, item.getMenuItem().getId());
                        itemPs.setInt(3, item.getQuantity());
                        itemPs.setString(4, item.getNotes());
                        itemPs.executeUpdate();
                        itemPs.close();
                    }
                }
                generatedKeys.close();
            }
            orderPs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 关闭数据库连接
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}