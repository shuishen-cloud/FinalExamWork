package com.restaurant.management.gui;

/**
 * 简单的订单金额计算测试
 */
public class OrderCalculationTest {
    public static void main(String[] args) {
        // 创建测试菜单项
        MenuItem menuItem1 = new MenuItem(1, "宫保鸡丁", "主菜", 28.50, "经典川菜", null);
        MenuItem menuItem2 = new MenuItem(2, "麻婆豆腐", "主菜", 18.00, "嫩滑豆腐配麻辣肉末", null);
        
        // 创建订单
        Order order = new Order(1);
        
        // 添加订单项
        OrderItem item1 = new OrderItem(menuItem1, 2); // 2份宫保鸡丁
        OrderItem item2 = new OrderItem(menuItem2, 1); // 1份麻婆豆腐
        
        order.addItem(item1);
        System.out.println("添加2份宫保鸡丁后:");
        System.out.println("小计: ¥" + String.format("%.2f", order.getSubtotal()));
        System.out.println("税费: ¥" + String.format("%.2f", order.getTax()));
        System.out.println("总计: ¥" + String.format("%.2f", order.getTotal()));
        
        order.addItem(item2);
        System.out.println("\n添加1份麻婆豆腐后:");
        System.out.println("小计: ¥" + String.format("%.2f", order.getSubtotal()));
        System.out.println("税费: ¥" + String.format("%.2f", order.getTax()));
        System.out.println("总计: ¥" + String.format("%.2f", order.getTotal()));
        
        // 验证计算是否正确
        double expectedSubtotal = 2 * 28.50 + 1 * 18.00; // 57.00 + 18.00 = 75.00
        double expectedTax = expectedSubtotal * 0.1; // 7.50
        double expectedTotal = expectedSubtotal + expectedTax; // 82.50
        
        System.out.println("\n预期值验证:");
        System.out.println("预期小计: ¥" + String.format("%.2f", expectedSubtotal));
        System.out.println("预期税费: ¥" + String.format("%.2f", expectedTax));
        System.out.println("预期总计: ¥" + String.format("%.2f", expectedTotal));
        
        System.out.println("\n计算结果" + (Math.abs(order.getTotal() - expectedTotal) < 0.01 ? "正确" : "错误"));
    }
}