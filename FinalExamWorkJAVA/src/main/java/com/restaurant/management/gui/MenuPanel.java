package com.restaurant.management.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 菜单面板
 */
public class MenuPanel extends JPanel {
    private DatabaseManager dbManager;
    private Order currentOrder;
    private JList<MenuItem> menuItemsList;
    private DefaultListModel<MenuItem> listModel;
    private JComboBox<String> categoryFilter;
    private JTextArea itemDetails;
    private JButton addToOrderButton;
    private JLabel selectedCategoryLabel;
    private JLabel tableInfoLabel;  // 餐桌信息标签

    private OrderPanel orderPanel;

    public MenuPanel(DatabaseManager dbManager, Order currentOrder, OrderPanel orderPanel) {
        this.dbManager = dbManager;
        this.currentOrder = currentOrder;
        this.orderPanel = orderPanel;
        initializeComponents();
        setupLayout();
        loadMenuItems();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        setBackground(new Color(0xECF0F1)); // 设置背景色

        // 初始化组件
        listModel = new DefaultListModel<>();
        menuItemsList = new JList<>(listModel);
        menuItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuItemsList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        menuItemsList.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true)); // 添加边框
        // 设置选中背景色
        menuItemsList.setSelectionBackground(new Color(0xD6EAF8));
        
        // 分类筛选下拉框
        categoryFilter = new JComboBox<>();
        categoryFilter.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        
        // 添加所有分类到筛选器
        categoryFilter.addItem("所有分类");
        List<String> categories = getMenuCategories();
        for (String category : categories) {
            categoryFilter.addItem(category);
        }
        
        // 详情区域
        itemDetails = new JTextArea();
        itemDetails.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        itemDetails.setEditable(false);
        itemDetails.setWrapStyleWord(true);
        itemDetails.setLineWrap(true);
        itemDetails.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        itemDetails.setBackground(Color.WHITE);
        
        // 按钮
        addToOrderButton = new JButton("添加到订单");
        addToOrderButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addToOrderButton.setBackground(new Color(0x3498DB)); // 蓝色主题
        addToOrderButton.setForeground(Color.WHITE);
        addToOrderButton.setFocusPainted(false);
        addToOrderButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加悬停效果
        addToOrderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addToOrderButton.setBackground(new Color(0x2980B9)); // 深蓝色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                addToOrderButton.setBackground(new Color(0x3498DB)); // 恢复原色
            }
        });
        
        selectedCategoryLabel = new JLabel("当前分类: 所有分类");
        selectedCategoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        selectedCategoryLabel.setForeground(new Color(0x2C3E50));
        
        // 餐桌信息标签
        tableInfoLabel = new JLabel("餐桌信息: 未选择餐桌");
        tableInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tableInfoLabel.setForeground(new Color(0x2C3E50));
        tableInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        // 顶部分类筛选区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        
        // 左侧分类筛选
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        filterPanel.add(new JLabel("筛选分类:", SwingConstants.LEFT));
        filterPanel.add(categoryFilter);
        filterPanel.add(selectedCategoryLabel);
        
        // 右侧餐桌信息
        JPanel tableInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableInfoPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        tableInfoPanel.add(tableInfoLabel);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(tableInfoPanel, BorderLayout.EAST);
        
        // 菜单列表区域
        JScrollPane listScrollPane = new JScrollPane(menuItemsList);
        listScrollPane.setPreferredSize(new Dimension(250, 400));
        listScrollPane.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        
        // 详情区域
        JScrollPane detailScrollPane = new JScrollPane(itemDetails);
        detailScrollPane.setPreferredSize(new Dimension(300, 200));
        detailScrollPane.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        buttonPanel.add(addToOrderButton);
        
        // 主面板布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                             listScrollPane, detailScrollPane);
        splitPane.setDividerLocation(250);
        splitPane.setBorder(BorderFactory.createEmptyBorder()); // 移除分割线边框
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // 菜单项选择事件
        menuItemsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MenuItem selectedItem = menuItemsList.getSelectedValue();
                if (selectedItem != null) {
                    updateItemDetails(selectedItem);
                }
            }
        });
        
        // 分类筛选事件
        categoryFilter.addActionListener(e -> {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            selectedCategoryLabel.setText("当前分类: " + selectedCategory);
            loadMenuItemsByCategory(selectedCategory);
        });
        
        // 添加到订单按钮事件
        addToOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToOrder();
            }
        });
    }

    // 加载所有菜单项
    private void loadMenuItems() {
        listModel.clear();
        List<MenuItem> menuItems = dbManager.getAllMenuItems();
        for (MenuItem item : menuItems) {
            listModel.addElement(item);
        }
    }

    // 根据分类加载菜单项
    private void loadMenuItemsByCategory(String category) {
        listModel.clear();
        if ("所有分类".equals(category)) {
            loadMenuItems();
        } else {
            List<MenuItem> menuItems = dbManager.getMenuItemsByCategory(category);
            for (MenuItem item : menuItems) {
                listModel.addElement(item);
            }
        }
    }

    // 获取所有菜单分类
    private List<String> getMenuCategories() {
        // 从数据库中获取所有唯一的分类
        // 这里简单返回一些示例分类，实际应该从数据库中查询
        java.util.ArrayList<String> categories = new java.util.ArrayList<>();
        categories.add("开胃菜");
        categories.add("主菜");
        categories.add("饮料");
        categories.add("甜点");
        return categories;
    }

    // 更新选中菜单项的详情
    private void updateItemDetails(MenuItem item) {
        if (item != null) {
            StringBuilder details = new StringBuilder();
            details.append("名称: ").append(item.getName()).append("\n");
            details.append("分类: ").append(item.getCategory()).append("\n");
            details.append("价格: ¥").append(String.format("%.2f", item.getPrice())).append("\n");
            details.append("描述: ").append(item.getDescription()).append("\n");
            details.append("可用: ").append(item.isAvailable() ? "是" : "否");
            
            itemDetails.setText(details.toString());
        }
    }

    // 添加选中的菜单项到订单
    private void addToOrder() {
        MenuItem selectedItem = menuItemsList.getSelectedValue();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个菜单项", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查当前订单是否关联了餐桌
        if (currentOrder.getTableId() <= 0) {
            int choice = JOptionPane.showConfirmDialog(this,
                "当前订单未关联餐桌，是否继续添加菜品？\n" +
                "选择“是”将把菜品添加到未分配餐桌的订单中。\n" +
                "选择“否”将返回订单面板先选择餐桌。",
                "餐桌未选择",
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.NO_OPTION) {
                // 提示用户转到订单面板选择餐桌
                JOptionPane.showMessageDialog(this,
                    "请到订单管理面板中选择餐桌后再添加菜品。",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // 弹出对话框获取数量
        String quantityStr = JOptionPane.showInputDialog(this, 
            "请输入数量 (餐桌: " + 
            (currentOrder.getTableId() > 0 ? "餐桌 #" + currentOrder.getTableId() : "未分配") + "):", 
            "1");
        if (quantityStr == null) {
            return; // 用户取消
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建订单项并添加到当前订单
            OrderItem orderItem = new OrderItem(selectedItem, quantity);
            currentOrder.addItem(orderItem);
            
            // 通知订单面板更新显示
            if (orderPanel != null) {
                orderPanel.updateOrderDisplay();
            }
            
            JOptionPane.showMessageDialog(this, 
                selectedItem.getName() + " 已添加到订单 (数量: " + quantity + 
                ", 餐桌: " + (currentOrder.getTableId() > 0 ? "餐桌 #" + currentOrder.getTableId() : "未分配") + ")", 
                "成功", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // 清除选择
            menuItemsList.clearSelection();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数量", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 刷新餐桌信息显示
    public void refreshTableInfo() {
        if (currentOrder != null && currentOrder.getTableId() > 0) {
            TableManager tableManager = new TableManager(dbManager);
            Table table = tableManager.getTableById(currentOrder.getTableId());
            if (table != null) {
                tableInfoLabel.setText("餐桌信息: " + table.getTableName() + " (ID: " + table.getTableId() + ", 容量: " + table.getCapacity() + "人)");
            } else {
                tableInfoLabel.setText("餐桌信息: ID " + currentOrder.getTableId() + " (餐桌信息不可用)");
            }
        } else {
            tableInfoLabel.setText("餐桌信息: 未选择餐桌");
        }
    }

    // 刷新面板
    public void refresh() {
        loadMenuItems();
        refreshTableInfo();  // 更新餐桌信息
    }
}