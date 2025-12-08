package com.restaurant.management.gui;

import com.restaurant.management.*;

import com.restaurant.management.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 系统主界面，集成桌位和点菜功能
 */
public class MainDashboardPanel extends JPanel {
    private RestaurantManagementSystem rms;
    
    // 桌位面板组件
    private JPanel tableLayoutPanel;
    private JComboBox<String> tableStatusFilter;
    
    // 点菜面板组件
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTextField customerNameField;
    private JComboBox<MenuItem> menuItemComboBox;
    private JTextField quantityField;
    private JLabel currentOrderIdLabel;
    
    // 当前选中的订单
    private int currentOrderId = -1;
    
    public MainDashboardPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadTableData();
        loadMenuData();
    }

    private void initializeComponents() {
        // 初始化桌位面板组件
        tableStatusFilter = new JComboBox<>(new String[]{"全部", "AVAILABLE", "OCCUPIED", "RESERVED", "CLEANING"});
        
        // 初始化订单表格模型
        String[] orderColumnNames = {"ID", "菜品", "数量", "单价", "小计"};
        orderTableModel = new DefaultTableModel(orderColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        
        // 初始化菜单表格模型
        String[] menuColumnNames = {"ID", "名称", "价格", "分类"};
        menuTableModel = new DefaultTableModel(menuColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuTable = new JTable(menuTableModel);
        
        // 初始化输入组件
        customerNameField = new JTextField(15);
        menuItemComboBox = new JComboBox<>();
        quantityField = new JTextField("1", 5);
        currentOrderIdLabel = new JLabel("当前订单: 无");
        
        // 设置表格行高
        orderTable.setRowHeight(25);
        menuTable.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建顶部主面板，分为左右两部分
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(600);
        
        // 左侧：桌位管理
        JPanel tablePanel = createTableManagementPanel();
        mainSplitPane.setLeftComponent(tablePanel);
        
        // 右侧：点菜和订单管理
        JPanel orderPanel = createOrderManagementPanel();
        mainSplitPane.setRightComponent(orderPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
    }
    
    private JPanel createTableManagementPanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("餐桌管理"));
        
        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("状态过滤:"));
        controlPanel.add(tableStatusFilter);
        
        JButton refreshTableButton = new JButton("刷新");
        refreshTableButton.addActionListener(e -> loadTableData());
        controlPanel.add(refreshTableButton);
        
        // 餐桌布局面板 - 使用网格布局显示餐桌
        tableLayoutPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4列，间距10px
        tableLayoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(tableLayoutPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        tablePanel.add(controlPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加过滤事件
        tableStatusFilter.addActionListener(e -> loadTableData());
        
        return tablePanel;
    }
    
    private JPanel createOrderManagementPanel() {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createTitledBorder("订单管理"));
        
        // 顶部客户信息面板
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.add(new JLabel("顾客姓名:"));
        customerPanel.add(customerNameField);
        
        JButton createOrderButton = new JButton("创建新订单");
        createOrderButton.addActionListener(e -> createNewOrder());
        customerPanel.add(createOrderButton);
        
        customerPanel.add(Box.createHorizontalStrut(20)); // 添加间距
        customerPanel.add(currentOrderIdLabel);
        
        JButton completeOrderButton = new JButton("完成订单");
        completeOrderButton.addActionListener(e -> completeOrder());
        customerPanel.add(completeOrderButton);
        
        // 中间分为左右两部分
        JSplitPane middleSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        middleSplitPane.setDividerLocation(300);
        
        // 左侧：菜单选择
        JPanel menuSelectionPanel = new JPanel(new BorderLayout());
        menuSelectionPanel.setBorder(BorderFactory.createTitledBorder("菜单选择"));
        
        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        menuSelectionPanel.add(menuScrollPane, BorderLayout.CENTER);
        
        // 底部：添加菜品控制
        JPanel addDishPanel = new JPanel(new FlowLayout());
        addDishPanel.add(new JLabel("菜品:"));
        addDishPanel.add(menuItemComboBox);
        addDishPanel.add(new JLabel("数量:"));
        addDishPanel.add(quantityField);
        
        JButton addDishButton = new JButton("添加到订单");
        addDishButton.addActionListener(e -> addDishToOrder());
        addDishPanel.add(addDishButton);
        
        // 添加菜单表格选择事件，同步更新下拉框
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = menuTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int menuItemId = (int) menuTableModel.getValueAt(selectedRow, 0); // ID列
                    // 在下拉框中找到对应的MenuItem并选择
                    for (int i = 0; i < menuItemComboBox.getItemCount(); i++) {
                        MenuItem item = menuItemComboBox.getItemAt(i);
                        if (item.getId() == menuItemId) {
                            menuItemComboBox.setSelectedItem(item);
                            break;
                        }
                    }
                }
            }
        });
        
        menuSelectionPanel.add(addDishPanel, BorderLayout.SOUTH);
        
        // 右侧：当前订单
        JPanel currentOrderPanel = new JPanel(new BorderLayout());
        currentOrderPanel.setBorder(BorderFactory.createTitledBorder("当前订单"));
        
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        currentOrderPanel.add(orderScrollPane, BorderLayout.CENTER);
        
        // 订单总计面板
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("订单总计:"));
        JLabel totalAmountLabel = new JLabel("¥0.00");
        totalPanel.add(totalAmountLabel);
        currentOrderPanel.add(totalPanel, BorderLayout.SOUTH);
        
        middleSplitPane.setLeftComponent(menuSelectionPanel);
        middleSplitPane.setRightComponent(currentOrderPanel);
        
        orderPanel.add(customerPanel, BorderLayout.NORTH);
        orderPanel.add(middleSplitPane, BorderLayout.CENTER);
        
        return orderPanel;
    }
    
    private void createNewOrder() {
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入顾客姓名！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentOrderId = rms.createOrder(customerName);
        currentOrderIdLabel.setText("当前订单: " + currentOrderId);
        orderTableModel.setRowCount(0); // 清空订单表格
        
        JOptionPane.showMessageDialog(this, "订单创建成功，订单ID：" + currentOrderId, "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addDishToOrder() {
        if (currentOrderId == -1) {
            JOptionPane.showMessageDialog(this, "请先创建订单！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        MenuItem selectedMenuItem = (MenuItem) menuItemComboBox.getSelectedItem();
        if (selectedMenuItem == null) {
            JOptionPane.showMessageDialog(this, "请选择菜品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须大于0！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数量！", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        rms.addItemToOrder(currentOrderId, selectedMenuItem.getId(), quantity);
        
        // 更新订单表格
        updateOrderDisplay();
        
        // 清空数量输入框
        quantityField.setText("1");
        
        JOptionPane.showMessageDialog(this, "菜品已添加到订单！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void completeOrder() {
        if (currentOrderId == -1) {
            JOptionPane.showMessageDialog(this, "没有当前订单！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要完成订单 " + currentOrderId + " 吗？",
            "确认完成订单",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            rms.updateOrderStatus(currentOrderId, "COMPLETED");
            currentOrderId = -1;
            currentOrderIdLabel.setText("当前订单: 无");
            orderTableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "订单已完成！", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateOrderDisplay() {
        if (currentOrderId != -1) {
            Order order = rms.findOrderById(currentOrderId);
            if (order != null) {
                orderTableModel.setRowCount(0); // 清空表格
                
                // 添加订单项到表格
                for (OrderItem item : order.getOrderItems()) {
                    Object[] rowData = {
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getMenuItem().getPrice(),
                        item.getSubtotal()
                    };
                    orderTableModel.addRow(rowData);
                }
            }
        }
    }
    
    private void loadTableData() {
        tableLayoutPanel.removeAll();
        
        String selectedStatus = (String) tableStatusFilter.getSelectedItem();
        List<Table> tables = rms.getTables();
        
        for (Table table : tables) {
            // 如果不是"全部"，则过滤状态
            if (!"全部".equals(selectedStatus) && !table.getStatus().equals(selectedStatus)) {
                continue;
            }
            
            // 创建餐桌按钮
            JButton tableButton = new JButton("餐桌 " + table.getTableId());
            tableButton.setPreferredSize(new Dimension(100, 80));
            tableButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
            
            // 根据状态设置颜色
            switch (table.getStatus()) {
                case "AVAILABLE":
                    tableButton.setBackground(Color.GREEN);
                    tableButton.setText("餐桌 " + table.getTableId() + "\n空闲");
                    break;
                case "OCCUPIED":
                    tableButton.setBackground(Color.RED);
                    tableButton.setText("餐桌 " + table.getTableId() + "\n占用");
                    break;
                case "RESERVED":
                    tableButton.setBackground(Color.ORANGE);
                    tableButton.setText("餐桌 " + table.getTableId() + "\n预定");
                    break;
                case "CLEANING":
                    tableButton.setBackground(Color.GRAY);
                    tableButton.setText("餐桌 " + table.getTableId() + "\n清洁");
                    break;
                default:
                    tableButton.setBackground(Color.LIGHT_GRAY);
                    tableButton.setText("餐桌 " + table.getTableId());
            }
            
            tableButton.addActionListener(e -> handleTableClick(table));
            
            tableLayoutPanel.add(tableButton);
        }
        
        tableLayoutPanel.revalidate();
        tableLayoutPanel.repaint();
    }
    
    private void handleTableClick(Table table) {
        JOptionPane.showMessageDialog(this, 
            "餐桌信息:\n" +
            "ID: " + table.getTableId() + "\n" +
            "容量: " + table.getCapacity() + "\n" +
            "状态: " + table.getStatus(),
            "餐桌详情",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadMenuData() {
        // 清空菜单表格和下拉框
        menuTableModel.setRowCount(0);
        menuItemComboBox.removeAllItems();
        
        List<MenuItem> menuItems = rms.getMenuItems();
        for (MenuItem item : menuItems) {
            // 添加到表格
            Object[] rowData = {
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getCategory()
            };
            menuTableModel.addRow(rowData);
            
            // 添加到下拉框
            menuItemComboBox.addItem(item);
        }
    }
}