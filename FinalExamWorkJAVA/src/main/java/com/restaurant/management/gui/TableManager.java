package com.restaurant.management.gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 餐桌管理类
 */
public class TableManager {
    private DatabaseManager dbManager;

    public TableManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initializeTables();
    }

    // 初始化餐桌表
    private void initializeTables() {
        try (Connection connection = dbManager.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            // 创建餐桌表
            String createTablesTable = "CREATE TABLE IF NOT EXISTS tables (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "table_name VARCHAR(100) NOT NULL, " +
                    "capacity INT NOT NULL, " +
                    "status VARCHAR(50) NOT NULL DEFAULT '空闲', " +
                    "check_in_time TIMESTAMP NULL, " +
                    "check_out_time TIMESTAMP NULL)";
            statement.execute(createTablesTable);

            // 插入一些示例餐桌数据（如果表为空）
            try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM tables")) {
                rs.next();
                int count = rs.getInt(1);
                if (count == 0) {
                    insertSampleTables(connection);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 插入示例餐桌
    private void insertSampleTables(Connection connection) {
        try {
            String sql = "INSERT INTO tables (table_name, capacity, status) VALUES (?, ?, ?)";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                // 添加几张餐桌
                ps.setString(1, "餐桌 1");
                ps.setInt(2, 4);
                ps.setString(3, "空闲");
                ps.executeUpdate();
                
                ps.setString(1, "餐桌 2");
                ps.setInt(2, 4);
                ps.setString(3, "空闲");
                ps.executeUpdate();
                
                ps.setString(1, "餐桌 3");
                ps.setInt(2, 6);
                ps.setString(3, "空闲");
                ps.executeUpdate();
                
                ps.setString(1, "餐桌 4");
                ps.setInt(2, 8);
                ps.setString(3, "空闲");
                ps.executeUpdate();
                
                ps.setString(1, "餐桌 5");
                ps.setInt(2, 2);
                ps.setString(3, "空闲");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取所有餐桌
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        try (Connection connection = dbManager.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM tables ORDER BY id")) {
            
            while (rs.next()) {
                Table table = new Table(
                    rs.getInt("id"),
                    rs.getString("table_name"),
                    rs.getInt("capacity")
                );
                table.setStatus(rs.getString("status"));
                
                // 设置入住和离开时间
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                if (checkIn != null) {
                    table.setCheckInTime(new java.util.Date(checkIn.getTime()));
                }
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                if (checkOut != null) {
                    table.setCheckOutTime(new java.util.Date(checkOut.getTime()));
                }
                
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    // 根据ID获取餐桌
    public Table getTableById(int tableId) {
        try (Connection connection = dbManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM tables WHERE id = ?")) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Table table = new Table(
                        rs.getInt("id"),
                        rs.getString("table_name"),
                        rs.getInt("capacity")
                    );
                    table.setStatus(rs.getString("status"));
                    
                    // 设置入住和离开时间
                    Timestamp checkIn = rs.getTimestamp("check_in_time");
                    if (checkIn != null) {
                        table.setCheckInTime(new java.util.Date(checkIn.getTime()));
                    }
                    Timestamp checkOut = rs.getTimestamp("check_out_time");
                    if (checkOut != null) {
                        table.setCheckOutTime(new java.util.Date(checkOut.getTime()));
                    }
                    
                    return table;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 更新餐桌状态
    public boolean updateTableStatus(int tableId, String status) {
        String sql = "UPDATE tables SET status = ? WHERE id = ?";
        try (Connection connection = dbManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tableId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新餐桌的入住时间
    public boolean updateCheckInTime(int tableId, java.util.Date checkInTime) {
        String sql = "UPDATE tables SET check_in_time = ? WHERE id = ?";
        try (Connection connection = dbManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (checkInTime != null) {
                ps.setTimestamp(1, new Timestamp(checkInTime.getTime()));
            } else {
                ps.setTimestamp(1, null);
            }
            ps.setInt(2, tableId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新餐桌的离开时间
    public boolean updateCheckOutTime(int tableId, java.util.Date checkOutTime) {
        String sql = "UPDATE tables SET check_out_time = ? WHERE id = ?";
        try (Connection connection = dbManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (checkOutTime != null) {
                ps.setTimestamp(1, new Timestamp(checkOutTime.getTime()));
            } else {
                ps.setTimestamp(1, null);
            }
            ps.setInt(2, tableId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 为餐桌分配订单
    public boolean assignOrderToTable(int tableId, Order order) {
        // 首先更新餐桌状态为占用
        boolean statusUpdated = updateTableStatus(tableId, "占用");
        
        // 更新餐桌的入住时间为当前时间
        boolean timeUpdated = updateCheckInTime(tableId, new java.util.Date());
        
        return statusUpdated && timeUpdated;
    }

    // 释放餐桌（客户离开）
    public boolean releaseTable(int tableId) {
        // 更新餐桌状态为空闲
        boolean statusUpdated = updateTableStatus(tableId, "空闲");
        
        // 更新餐桌的离开时间为当前时间
        boolean timeUpdated = updateCheckOutTime(tableId, new java.util.Date());
        
        return statusUpdated && timeUpdated;
    }
}