package com.restaurant.management.gui;

import com.restaurant.management.Order;
import com.restaurant.management.RestaurantManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 报表面板
 */
public class ReportPanel extends JPanel {
    private RestaurantManagementSystem rms;
    private JTable orderReportTable;
    private DefaultTableModel orderReportModel;
    private JTextArea summaryArea;

    public ReportPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadReportData();
    }

    private void initializeComponents() {
        // 订单报表表格模型
        String[] columnNames = {"订单ID", "顾客姓名", "订单时间", "状态", "总金额"};
        orderReportModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderReportTable = new JTable(orderReportModel);

        // 摘要区域
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        
        // 设置表格行高
        orderReportTable.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建顶部摘要面板
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("经营摘要"));
        summaryPanel.setPreferredSize(new Dimension(1000, 120));
        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("刷新报表");
        JButton printSummaryButton = new JButton("打印摘要");

        refreshButton.addActionListener(e -> loadReportData());
        printSummaryButton.addActionListener(e -> printSummary());

        buttonPanel.add(refreshButton);
        buttonPanel.add(printSummaryButton);

        // 创建报表表格面板
        JScrollPane tableScrollPane = new JScrollPane(orderReportTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 400));

        // 将组件添加到主面板
        add(summaryPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReportData() {
        // 清空表格
        orderReportModel.setRowCount(0);

        // 从系统中获取订单数据并添加到表格
        List<Order> orders = rms.getOrders();
        for (Order order : orders) {
            Object[] rowData = {
                order.getOrderId(),
                order.getCustomerName(),
                order.getOrderTime(),
                order.getStatus(),
                order.getTotalAmount()
            };
            orderReportModel.addRow(rowData);
        }

        // 更新摘要信息
        updateSummary();
    }

    private void updateSummary() {
        List<Order> allOrders = rms.getOrders();
        List<Order> completedOrders = rms.getOrdersByStatus("COMPLETED");
        
        BigDecimal totalRevenue = rms.calculateDailyRevenue();
        int totalOrders = allOrders.size();
        int completedOrderCount = completedOrders.size();
        int pendingOrders = rms.getOrdersByStatus("PENDING").size();
        int preparingOrders = rms.getOrdersByStatus("PREPARING").size();
        int readyOrders = rms.getOrdersByStatus("READY").size();

        StringBuilder summary = new StringBuilder();
        summary.append("餐厅经营报表\n");
        summary.append("============================\n");
        summary.append(String.format("总订单数: %d\n", totalOrders));
        summary.append(String.format("已完成订单: %d\n", completedOrderCount));
        summary.append(String.format("待处理订单: %d\n", pendingOrders));
        summary.append(String.format("准备中订单: %d\n", preparingOrders));
        summary.append(String.format("待取餐订单: %d\n", readyOrders));
        summary.append(String.format("总收入: ¥%.2f\n", totalRevenue.doubleValue()));
        
        if (completedOrderCount > 0) {
            BigDecimal avgOrderValue = totalRevenue.divide(BigDecimal.valueOf(completedOrderCount), 2, BigDecimal.ROUND_HALF_UP);
            summary.append(String.format("平均订单价值: ¥%.2f\n", avgOrderValue.doubleValue()));
        } else {
            summary.append("平均订单价值: ¥0.00\n");
        }
        
        summary.append("============================\n");
        summary.append("数据更新时间: " + java.time.LocalDateTime.now());

        summaryArea.setText(summary.toString());
        summaryArea.setCaretPosition(0); // 滚动到顶部
    }

    private void printSummary() {
        try {
            summaryArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "打印失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}