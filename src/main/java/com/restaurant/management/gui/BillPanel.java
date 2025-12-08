package com.restaurant.management.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 账单面板
 */
public class BillPanel extends JPanel {
    private DatabaseManager dbManager;
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JTextArea billDetailsArea;
    private JButton printBillButton;
    private JButton payBillButton;
    private JButton refreshButton;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JComboBox<String> paymentMethodCombo;
    private Order selectedOrder;

    public BillPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAllOrders();
    }

    private void initializeComponents() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());

        // 初始化表格模型
        String[] columnNames = {"订单ID", "下单时间", "状态", "总计"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        // 创建表格
        billTable = new JTable(tableModel);
        billTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        billTable.setRowHeight(25);
        
        // 设置列宽
        javax.swing.table.TableColumn column = null;
        for (int i = 0; i < billTable.getColumnCount(); i++) {
            column = billTable.getColumnModel().getColumn(i);
            switch (i) {
                case 0: // 订单ID
                    column.setPreferredWidth(80);
                    break;
                case 1: // 下单时间
                    column.setPreferredWidth(150);
                    break;
                case 2: // 状态
                    column.setPreferredWidth(100);
                    break;
                case 3: // 总计
                    column.setPreferredWidth(100);
                    break;
            }
        }

        // 初始化按钮
        printBillButton = new JButton("打印账单");
        printBillButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        printBillButton.setBackground(new Color(70, 130, 180)); // 钢蓝色
        printBillButton.setForeground(Color.WHITE);
        
        payBillButton = new JButton("处理支付");
        payBillButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        payBillButton.setBackground(new Color(34, 139, 34)); // 绿色
        payBillButton.setForeground(Color.WHITE);
        
        refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(255, 165, 0)); // 橙色
        refreshButton.setForeground(Color.WHITE);

        // 初始化总计标签
        subtotalLabel = new JLabel("小计: ¥0.00");
        subtotalLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        taxLabel = new JLabel("税费: ¥0.00");
        taxLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        totalLabel = new JLabel("总计: ¥0.00");
        totalLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 69, 0)); // 红色

        // 初始化支付方式下拉框
        paymentMethodCombo = new JComboBox<>();
        paymentMethodCombo.addItem("现金");
        paymentMethodCombo.addItem("信用卡");
        paymentMethodCombo.addItem("支付宝");
        paymentMethodCombo.addItem("微信支付");
        paymentMethodCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 初始化账单详情区域
        billDetailsArea = new JTextArea();
        billDetailsArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        billDetailsArea.setEditable(false);
        billDetailsArea.setWrapStyleWord(true);
        billDetailsArea.setLineWrap(true);
    }

    private void setupLayout() {
        // 表格区域
        JScrollPane tableScrollPane = new JScrollPane(billTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 200));

        // 账单详情区域
        JScrollPane detailsScrollPane = new JScrollPane(billDetailsArea);
        detailsScrollPane.setPreferredSize(new Dimension(300, 200));

        // 左侧：订单列表
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("订单列表"));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 右侧：账单详情
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("账单详情"));
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // 主列表区域
        JSplitPane listSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                                 leftPanel, rightPanel);
        listSplitPane.setDividerLocation(500);

        // 总计面板
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("支付方式: "));
        totalPanel.add(paymentMethodCombo);
        totalPanel.add(Box.createHorizontalStrut(20));
        totalPanel.add(subtotalLabel);
        totalPanel.add(Box.createHorizontalStrut(10));
        totalPanel.add(taxLabel);
        totalPanel.add(Box.createHorizontalStrut(10));
        totalPanel.add(totalLabel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        buttonPanel.add(printBillButton);
        buttonPanel.add(payBillButton);

        add(listSplitPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // 表格选择事件
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderIdStr = (String) tableModel.getValueAt(selectedRow, 0);
                    int orderId = Integer.parseInt(orderIdStr.replace("订单 #", ""));
                    
                    // 从数据库获取完整订单信息
                    List<Order> allOrders = dbManager.getAllOrders();
                    for (Order order : allOrders) {
                        if (order.getOrderId() == orderId) {
                            displayOrderDetails(order);
                            break;
                        }
                    }
                }
            }
        });
        
        // 刷新按钮事件
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBillList();
            }
        });
        
        // 打印账单按钮事件
        printBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printBill();
            }
        });
        
        // 处理支付按钮事件
        payBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
    }

    // 加载所有订单
    private void loadAllOrders() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 从数据库获取所有订单
        List<Order> orders = dbManager.getAllOrders();
        
        for (Order order : orders) {
            Object[] rowData = {
                "订单 #" + order.getOrderId(),
                order.getOrderTime().toString(),
                order.getStatus(),
                "¥" + String.format("%.2f", order.getTotal())
            };
            tableModel.addRow(rowData);
        }
    }

    // 显示订单详情
    private void displayOrderDetails(Order order) {
        this.selectedOrder = order;
        
        StringBuilder details = new StringBuilder();
        details.append("订单详情\n");
        details.append("==================\n");
        details.append("订单号: ").append(order.getOrderId()).append("\n");
        details.append("下单时间: ").append(order.getOrderTime()).append("\n");
        details.append("状态: ").append(order.getStatus()).append("\n");
        details.append("\n订单项:\n");
        
        for (OrderItem item : order.getItems()) {
            details.append("  ")
                   .append(item.getMenuItem().getName())
                   .append(" x")
                   .append(item.getQuantity())
                   .append(" - ¥")
                   .append(String.format("%.2f", item.getMenuItem().getPrice()))
                   .append(" = ¥")
                   .append(String.format("%.2f", item.getSubtotal()))
                   .append("\n");
        }
        
        details.append("\n小计: ¥").append(String.format("%.2f", order.getSubtotal())).append("\n");
        details.append("税费: ¥").append(String.format("%.2f", order.getTax())).append("\n");
        details.append("总计: ¥").append(String.format("%.2f", order.getTotal())).append("\n");
        
        billDetailsArea.setText(details.toString());
        
        // 更新总计标签
        updateTotalLabels(order);
    }

    // 更新总计标签
    private void updateTotalLabels(Order order) {
        subtotalLabel.setText("小计: ¥" + String.format("%.2f", order.getSubtotal()));
        taxLabel.setText("税费: ¥" + String.format("%.2f", order.getTax()));
        totalLabel.setText("总计: ¥" + String.format("%.2f", order.getTotal()));
    }

    // 刷新账单列表
    private void refreshBillList() {
        loadAllOrders();
        billDetailsArea.setText("");
        selectedOrder = null;
    }

    // 打印账单
    private void printBill() {
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 生成打印格式的账单
        StringBuilder billContent = new StringBuilder();
        billContent.append("===================\n");
        billContent.append("    餐厅账单\n");
        billContent.append("===================\n");
        billContent.append("订单号: ").append(selectedOrder.getOrderId()).append("\n");
        billContent.append("时间: ").append(selectedOrder.getOrderTime()).append("\n");
        billContent.append("-------------------\n");
        
        for (OrderItem item : selectedOrder.getItems()) {
            String itemLine = String.format("%-20s x%d  ¥%.2f\n", 
                item.getMenuItem().getName(), 
                item.getQuantity(), 
                item.getSubtotal());
            billContent.append(itemLine);
        }
        
        billContent.append("-------------------\n");
        billContent.append(String.format("%-23s ¥%.2f\n", "小计:", selectedOrder.getSubtotal()));
        billContent.append(String.format("%-23s ¥%.2f\n", "税费:", selectedOrder.getTax()));
        billContent.append(String.format("%-23s ¥%.2f\n", "总计:", selectedOrder.getTotal()));
        billContent.append("===================\n");
        billContent.append("    谢谢惠顾!\n");
        billContent.append("===================\n");

        // 在实际应用中，这里会调用打印机
        // 现在我们显示一个消息框来模拟打印
        JTextArea printArea = new JTextArea(billContent.toString());
        printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        printArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(printArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "账单预览 (模拟打印)",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 处理支付
    private void processPayment() {
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("已完成".equals(selectedOrder.getStatus())) {
            JOptionPane.showMessageDialog(this, "该订单已完成支付", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        double total = selectedOrder.getTotal();
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "支付方式: " + paymentMethod + "\n" +
            "金额: ¥" + String.format("%.2f", total) + "\n\n" +
            "确认收款吗？",
            "确认支付",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 更新订单状态
            selectedOrder.setStatus("已完成");
            
            // 在实际应用中，这里会更新数据库
            // 暂时我们只更新本地订单对象
            
            // 刷新界面
            refreshBillList();
            
            JOptionPane.showMessageDialog(this, 
                "支付成功！\n订单号: " + selectedOrder.getOrderId() + 
                "\n支付方式: " + paymentMethod + 
                "\n金额: ¥" + String.format("%.2f", total), 
                "支付成功", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 刷新面板
    public void refresh() {
        refreshBillList();
    }
}