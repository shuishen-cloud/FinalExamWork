package com.restaurant.management;

import java.math.BigDecimal;
import java.util.*;

/**
 * 餐厅管理系统核心类
 */
public class RestaurantManagementSystem {
    private List<MenuItem> menuItems;
    private List<Order> orders;
    private List<Table> tables;
    private List<Customer> customers;
    private int nextOrderId;
    private int nextCustomerId;

    public RestaurantManagementSystem() {
        this.menuItems = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.nextOrderId = 1;
        this.nextCustomerId = 1;
    }

    // 菜单管理功能
    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        System.out.println("菜单项已添加: " + menuItem.getName());
    }

    public void removeMenuItem(int menuItemId) {
        menuItems.removeIf(item -> item.getId() == menuItemId);
        System.out.println("菜单项已删除，ID: " + menuItemId);
    }

    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public MenuItem findMenuItemById(int id) {
        return menuItems.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // 餐桌管理功能
    public void addTable(Table table) {
        tables.add(table);
        System.out.println("餐桌已添加: " + table.getTableId());
    }

    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }

    public Table findTableById(int tableId) {
        return tables.stream()
                .filter(table -> table.getTableId() == tableId)
                .findFirst()
                .orElse(null);
    }

    // 顾客管理功能
    public int addCustomer(String name, String phone, String email, String address) {
        Customer customer = new Customer(nextCustomerId, name, phone, email, address);
        customers.add(customer);
        System.out.println("顾客已添加: " + customer.getName() + ", ID: " + nextCustomerId);
        return nextCustomerId++;
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomerById(int customerId) {
        return customers.stream()
                .filter(customer -> customer.getCustomerId() == customerId)
                .findFirst()
                .orElse(null);
    }

    // 订单管理功能
    public int createOrder(String customerName) {
        Order order = new Order(nextOrderId, customerName);
        orders.add(order);
        System.out.println("订单已创建，ID: " + nextOrderId + ", 顾客: " + customerName);
        return nextOrderId++;
    }

    public Order findOrderById(int orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
    }

    public void addItemToOrder(int orderId, int menuItemId, int quantity) {
        Order order = findOrderById(orderId);
        MenuItem menuItem = findMenuItemById(menuItemId);

        if (order != null && menuItem != null) {
            OrderItem orderItem = new OrderItem(menuItem, quantity);
            order.addItem(orderItem);
            System.out.println("已添加菜品到订单 " + orderId + ": " + menuItem.getName() + " x" + quantity);
        } else {
            System.out.println("无法添加菜品到订单: 订单或菜品不存在");
        }
    }

    public void updateOrderStatus(int orderId, String status) {
        Order order = findOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            System.out.println("订单 " + orderId + " 状态已更新为: " + status);
        } else {
            System.out.println("订单不存在: " + orderId);
        }
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orders.stream()
                .filter(order -> order.getStatus().equals(status))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // 报表功能
    public void printMenu() {
        System.out.println("\n========== 餐厅菜单 ==========\n");
        if (menuItems.isEmpty()) {
            System.out.println("菜单为空");
        } else {
            for (MenuItem item : menuItems) {
                System.out.println(item);
            }
        }
        System.out.println("================================\n");
    }

    public void printOrders() {
        System.out.println("\n========== 所有订单 ==========\n");
        if (orders.isEmpty()) {
            System.out.println("没有订单");
        } else {
            for (Order order : orders) {
                System.out.println(order);
            }
        }
        System.out.println("================================\n");
    }

    public void printTables() {
        System.out.println("\n========== 餐桌状态 ==========\n");
        if (tables.isEmpty()) {
            System.out.println("没有餐桌");
        } else {
            for (Table table : tables) {
                System.out.println(table);
            }
        }
        System.out.println("================================\n");
    }

    public void printCustomers() {
        System.out.println("\n========== 顾客列表 ==========\n");
        if (customers.isEmpty()) {
            System.out.println("没有顾客");
        } else {
            for (Customer customer : customers) {
                System.out.println(customer);
            }
        }
        System.out.println("================================\n");
    }

    public BigDecimal calculateDailyRevenue() {
        return orders.stream()
                .filter(order -> order.getStatus().equals("COMPLETED"))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void printDailyReport() {
        System.out.println("\n========== 日报 ==========\n");
        System.out.println("总订单数: " + orders.size());
        System.out.println("已完成订单数: " + getOrdersByStatus("COMPLETED").size());
        System.out.println("总收入: " + calculateDailyRevenue());
        System.out.println("待处理订单数: " + getOrdersByStatus("PENDING").size());
        System.out.println("================================\n");
    }
}