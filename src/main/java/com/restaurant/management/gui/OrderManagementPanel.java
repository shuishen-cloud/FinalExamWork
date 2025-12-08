package com.restaurant.management.gui;

import com.restaurant.management.MenuItem;
import com.restaurant.management.Order;
import com.restaurant.management.OrderItem;
import com.restaurant.management.RestaurantManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单管理面板
 */
public class OrderManagementPanel extends JPanel {
    private RestaurantManagementSystem rms;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTable orderItemTable;
    private DefaultTableModel orderItemTableModel;
    private JTextField orderIdField, customerNameField, itemQuantityField;
    private JComboBox<MenuItem> menuItemComboBox;
    private JComboBox<String> statusComboBox;

    public OrderManagementPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadOrderData();
        loadMenuItems();
    }

    private void initializeComponents() {
        // 订单表格模型
        String[] orderColumnNames = {"订单ID", "顾客姓名", "订单时间", "状态", "总金额"};
        orderTableModel = new DefaultTableModel(orderColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 订单项表格模型
        String[] itemColumnNames = {"菜品ID", "菜品名称", "数量", "单价", "小计"};
        orderItemTableModel = new DefaultTableModel(itemColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderItemTable = new JTable(orderItemTableModel);

        // 输入字段
        orderIdField = new JTextField(10);
        customerNameField = new JTextField(15);
        itemQuantityField = new JTextField(5);
        
        // 下拉框
        menuItemComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "PREPARING", "READY", "COMPLETED", "CANCELLED"});
        
        // 设置表格行高
        orderTable.setRowHeight(25);
        orderItemTable.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建顶部面板 - 订单操作
        JPanel orderOperationPanel = new JPanel(new GridBagLayout());
        orderOperationPanel.setBorder(BorderFactory.createTitledBorder("订单操作"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        orderOperationPanel.add(new JLabel("订单ID:"), gbc);
        gbc.gridx = 1;
        orderOperationPanel.add(orderIdField, gbc);
        gbc.gridx = 2;
        orderOperationPanel.add(new JLabel("顾客姓名:"), gbc);
        gbc.gridx = 3;
        orderOperationPanel.add(customerNameField, gbc);

        gbc.gridx = 4;
        JButton createOrderButton = new JButton("创建订单");
        orderOperationPanel.add(createOrderButton, gbc);

        gbc.gridx = 5;
        JButton refreshOrderButton = new JButton("刷新订单");
        orderOperationPanel.add(refreshOrderButton, gbc);

        // 订单项操作面板
        JPanel itemOperationPanel = new JPanel(new GridBagLayout());
        itemOperationPanel.setBorder(BorderFactory.createTitledBorder("订单项操作"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);

        gbc2.gridx = 0; gbc2.gridy = 0;
        itemOperationPanel.add(new JLabel("选择菜品:"), gbc2);
        gbc2.gridx = 1;
        itemOperationPanel.add(menuItemComboBox, gbc2);
        gbc2.gridx = 2;
        itemOperationPanel.add(new JLabel("数量:"), gbc2);
        gbc2.gridx = 3;
        itemOperationPanel.add(itemQuantityField, gbc2);

        gbc2.gridx = 4;
        JButton addItemButton = new JButton("添加到订单");
        itemOperationPanel.add(addItemButton, gbc2);

        gbc2.gridx = 5;
        JButton updateStatusButton = new JButton("更新状态");
        itemOperationPanel.add(updateStatusButton, gbc2);
        
        gbc2.gridx = 6;
        itemOperationPanel.add(new JLabel("状态:"), gbc2);
        gbc2.gridx = 7;
        itemOperationPanel.add(statusComboBox, gbc2);

        // 按钮事件
        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createOrder();
            }
        });

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemToOrder();
            }
        });

        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOrderStatus();
            }
        });

        refreshOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOrderData();
            }
        });

        // 创建左右分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // 上半部分：订单表格
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setPreferredSize(new Dimension(800, 200));
        
        // 下半部分：订单项表格和操作面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JScrollPane itemScrollPane = new JScrollPane(orderItemTable);
        itemScrollPane.setPreferredSize(new Dimension(800, 150));
        
        bottomPanel.add(itemScrollPane, BorderLayout.CENTER);
        bottomPanel.add(itemOperationPanel, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(orderScrollPane);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setDividerLocation(250);

        // 添加到主面板
        add(orderOperationPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // 添加表格选择监听器
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderItems();
            }
        });
    }

    private void createOrder() {
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入顾客姓名！", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int orderId = rms.createOrder(customerName);
        loadOrderData();
        clearOrderFields();
        JOptionPane.showMessageDialog(this, "订单创建成功，订单ID：" + orderId, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addItemToOrder() {
        int selectedOrderId;
        try {
            selectedOrderId = Integer.parseInt(orderIdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的订单ID！", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MenuItem selectedMenuItem = (MenuItem) menuItemComboBox.getSelectedItem();
        if (selectedMenuItem == null) {
            JOptionPane.showMessageDialog(this, "请先选择菜品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(itemQuantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "数量必须大于0！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数量！", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rms.addItemToOrder(selectedOrderId, selectedMenuItem.getId(), quantity);
        showOrderItems(); // 更新当前订单的项目显示
        clearItemFields();
        loadOrderData(); // 重新加载订单数据以更新总金额
        JOptionPane.showMessageDialog(this, "菜品已添加到订单！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateOrderStatus() {
        int selectedOrderId;
        try {
            selectedOrderId = Integer.parseInt(orderIdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的订单ID！", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedStatus = (String) statusComboBox.getSelectedItem();
        rms.updateOrderStatus(selectedOrderId, selectedStatus);
        loadOrderData();
        JOptionPane.showMessageDialog(this, "订单状态已更新！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadOrderData() {
        orderTableModel.setRowCount(0);
        List<Order> orders = rms.getOrders();
        for (Order order : orders) {
            Object[] rowData = {
                order.getOrderId(),
                order.getCustomerName(),
                order.getOrderTime(),
                order.getStatus(),
                order.getTotalAmount()
            };
            orderTableModel.addRow(rowData);
        }
    }

    private void loadMenuItems() {
        menuItemComboBox.removeAllItems();
        List<MenuItem> menuItems = rms.getMenuItems();
        for (MenuItem item : menuItems) {
            menuItemComboBox.addItem(item);
        }
    }

    private void showOrderItems() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
            Order order = rms.findOrderById(orderId);
            
            if (order != null) {
                // 显示订单ID
                orderIdField.setText(String.valueOf(orderId));
                
                // 清空订单项表格
                orderItemTableModel.setRowCount(0);
                
                // 添加订单项到表格
                for (OrderItem item : order.getOrderItems()) {
                    Object[] rowData = {
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getMenuItem().getPrice(),
                        item.getSubtotal()
                    };
                    orderItemTableModel.addRow(rowData);
                }
            }
        }
    }

    private void clearOrderFields() {
        customerNameField.setText("");
    }

    private void clearItemFields() {
        itemQuantityField.setText("");
    }
}