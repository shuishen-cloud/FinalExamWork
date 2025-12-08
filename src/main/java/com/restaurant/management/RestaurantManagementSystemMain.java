package com.restaurant.management;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * 餐厅管理系统主类 - 提供用户界面和演示功能
 */
public class RestaurantManagementSystemMain {
    private static RestaurantManagementSystem rms = new RestaurantManagementSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("欢迎使用餐厅管理系统！");
        
        // 初始化一些示例数据
        initializeSampleData();
        
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    manageMenu();
                    break;
                case 2:
                    manageTables();
                    break;
                case 3:
                    manageOrders();
                    break;
                case 4:
                    manageCustomers();
                    break;
                case 5:
                    generateReports();
                    break;
                case 0:
                    running = false;
                    System.out.println("感谢使用餐厅管理系统！");
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
        
        scanner.close();
    }
    
    private static void initializeSampleData() {
        // 添加示例菜品
        rms.addMenuItem(new MenuItem(1, "宫保鸡丁", "经典川菜，鸡肉配花生米", new BigDecimal("28.00"), "主菜"));
        rms.addMenuItem(new MenuItem(2, "麻婆豆腐", "嫩豆腐配麻辣肉末", new BigDecimal("18.00"), "主菜"));
        rms.addMenuItem(new MenuItem(3, "酸辣汤", "酸辣开胃汤品", new BigDecimal("12.00"), "汤类"));
        rms.addMenuItem(new MenuItem(4, "米饭", "优质东北大米", new BigDecimal("3.00"), "主食"));
        rms.addMenuItem(new MenuItem(5, "可乐", "冰镇可乐", new BigDecimal("6.00"), "饮料"));
        
        // 添加示例餐桌
        rms.addTable(new Table(1, 4));
        rms.addTable(new Table(2, 2));
        rms.addTable(new Table(3, 6));
        rms.addTable(new Table(4, 8));
        
        // 添加示例顾客
        rms.addCustomer("张三", "13800138001", "zhangsan@email.com", "北京市朝阳区");
        rms.addCustomer("李四", "13800138002", "lisi@email.com", "上海市浦东新区");
        rms.addCustomer("王五", "13800138003", "wangwu@email.com", "广州市天河区");
    }
    
    private static void showMainMenu() {
        System.out.println("\n========== 餐厅管理系统 ==========\n");
        System.out.println("1. 菜单管理");
        System.out.println("2. 餐桌管理");
        System.out.println("3. 订单管理");
        System.out.println("4. 顾客管理");
        System.out.println("5. 报表生成");
        System.out.println("0. 退出系统");
        System.out.println("================================");
        System.out.print("请选择操作: ");
    }
    
    private static void manageMenu() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== 菜单管理 ==========\n");
            System.out.println("1. 查看菜单");
            System.out.println("2. 添加菜品");
            System.out.println("3. 删除菜品");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    rms.printMenu();
                    break;
                case 2:
                    addMenuItem();
                    break;
                case 3:
                    removeMenuItem();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
    }
    
    private static void addMenuItem() {
        System.out.print("请输入菜品ID: ");
        int id = getIntInput();
        System.out.print("请输入菜品名称: ");
        String name = getStringInput();
        System.out.print("请输入菜品描述: ");
        String description = getStringInput();
        System.out.print("请输入菜品价格: ");
        String priceStr = getStringInput();
        BigDecimal price = new BigDecimal(priceStr);
        System.out.print("请输入菜品分类: ");
        String category = getStringInput();
        
        rms.addMenuItem(new MenuItem(id, name, description, price, category));
        System.out.println("菜品添加成功！");
    }
    
    private static void removeMenuItem() {
        System.out.print("请输入要删除的菜品ID: ");
        int id = getIntInput();
        rms.removeMenuItem(id);
        System.out.println("菜品删除成功！");
    }
    
    private static void manageTables() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== 餐桌管理 ==========\n");
            System.out.println("1. 查看餐桌状态");
            System.out.println("2. 添加餐桌");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    rms.printTables();
                    break;
                case 2:
                    addTable();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
    }
    
    private static void addTable() {
        System.out.print("请输入餐桌ID: ");
        int id = getIntInput();
        System.out.print("请输入餐桌容量: ");
        int capacity = getIntInput();
        
        rms.addTable(new Table(id, capacity));
        System.out.println("餐桌添加成功！");
    }
    
    private static void manageOrders() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== 订单管理 ==========\n");
            System.out.println("1. 查看所有订单");
            System.out.println("2. 创建新订单");
            System.out.println("3. 添加菜品到订单");
            System.out.println("4. 更新订单状态");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    rms.printOrders();
                    break;
                case 2:
                    createOrder();
                    break;
                case 3:
                    addItemToOrder();
                    break;
                case 4:
                    updateOrderStatus();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
    }
    
    private static void createOrder() {
        System.out.print("请输入顾客姓名: ");
        String customerName = getStringInput();
        rms.createOrder(customerName);
        System.out.println("订单创建成功！");
    }
    
    private static void addItemToOrder() {
        System.out.print("请输入订单ID: ");
        int orderId = getIntInput();
        System.out.print("请输入菜品ID: ");
        int menuItemId = getIntInput();
        System.out.print("请输入数量: ");
        int quantity = getIntInput();
        
        rms.addItemToOrder(orderId, menuItemId, quantity);
    }
    
    private static void updateOrderStatus() {
        System.out.print("请输入订单ID: ");
        int orderId = getIntInput();
        System.out.println("可选状态: PENDING, PREPARING, READY, COMPLETED, CANCELLED");
        System.out.print("请输入新状态: ");
        String status = getStringInput();
        
        rms.updateOrderStatus(orderId, status);
    }
    
    private static void manageCustomers() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== 顾客管理 ==========\n");
            System.out.println("1. 查看顾客列表");
            System.out.println("2. 添加顾客");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    rms.printCustomers();
                    break;
                case 2:
                    addCustomer();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
    }
    
    private static void addCustomer() {
        System.out.print("请输入顾客姓名: ");
        String name = getStringInput();
        System.out.print("请输入顾客电话: ");
        String phone = getStringInput();
        System.out.print("请输入顾客邮箱: ");
        String email = getStringInput();
        System.out.print("请输入顾客地址: ");
        String address = getStringInput();
        
        rms.addCustomer(name, phone, email, address);
        System.out.println("顾客添加成功！");
    }
    
    private static void generateReports() {
        boolean reporting = true;
        while (reporting) {
            System.out.println("\n========== 报表生成 ==========\n");
            System.out.println("1. 查看菜单");
            System.out.println("2. 查看订单");
            System.out.println("3. 查看餐桌状态");
            System.out.println("4. 查看顾客列表");
            System.out.println("5. 查看日报");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    rms.printMenu();
                    break;
                case 2:
                    rms.printOrders();
                    break;
                case 3:
                    rms.printTables();
                    break;
                case 4:
                    rms.printCustomers();
                    break;
                case 5:
                    rms.printDailyReport();
                    break;
                case 0:
                    reporting = false;
                    break;
                default:
                    System.out.println("无效选择，请重试。");
            }
        }
    }
    
    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("输入格式错误，请输入数字。");
            return -1;
        }
    }
    
    private static String getStringInput() {
        return scanner.nextLine();
    }
}