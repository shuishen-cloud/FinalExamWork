package com.restaurant.management.gui;

import java.util.List;

/**
 * 简单的数据库功能测试
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("开始测试数据库功能...");
        
        // 创建数据库管理器实例
        DatabaseManager dbManager = new DatabaseManager();
        
        // 测试获取所有菜单项
        System.out.println("获取所有菜单项...");
        List<MenuItem> menuItems = dbManager.getAllMenuItems();
        System.out.println("找到 " + menuItems.size() + " 个菜单项");
        
        for (MenuItem item : menuItems) {
            System.out.println("- " + item.getName() + " (" + item.getCategory() + "): ¥" + item.getPrice());
        }
        
        // 测试按类别获取菜单项
        System.out.println("\n获取主菜类别菜单项...");
        List<MenuItem> mainDishes = dbManager.getMenuItemsByCategory("主菜");
        System.out.println("找到 " + mainDishes.size() + " 个主菜");
        
        // 测试获取所有订单
        System.out.println("\n获取所有订单...");
        List<Order> orders = dbManager.getAllOrders();
        System.out.println("找到 " + orders.size() + " 个订单");
        
        // 关闭数据库连接
        dbManager.closeConnection();
        
        System.out.println("\n数据库功能测试完成！");
    }
}