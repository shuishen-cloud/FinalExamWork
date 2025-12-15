package com.restaurant.management.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 订单面板
 */
public class OrderPanel extends JPanel {
    private Order currentOrder;
    private DatabaseManager dbManager;
    private TableManager tableManager;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton removeItemButton;
    private JButton clearOrderButton;
    private JButton submitOrderButton;
    private JButton selectTableButton;  // 选择餐桌按钮
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;
    private JLabel tableInfoLabel;      // 餐桌信息标签
    private JTextArea notesArea;
    private JComboBox<String> tableComboBox;  // 餐桌选择下拉框

    public OrderPanel(Order currentOrder, DatabaseManager dbManager) {
        this.currentOrder = currentOrder;
        this.dbManager = dbManager;
        this.tableManager = new TableManager(dbManager);  // 初始化餐桌管理器
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateOrderDisplay();
        loadTables();  // 加载餐桌选项
    }

    private void initializeComponents() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        setBackground(new Color(0xECF0F1)); // 设置背景色

        // 初始化表格模型
        String[] columnNames = {"菜品", "单价", "数量", "小计", "备注"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        // 创建表格
        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        orderTable.setRowHeight(30); // 增加行高以提高可读性
        orderTable.setSelectionBackground(new Color(0xD6EAF8)); // 设置选中行背景色
        orderTable.setGridColor(new Color(0xBDC3C7)); // 设置网格颜色
        orderTable.setBackground(Color.WHITE);
        orderTable.setForeground(new Color(0x2C3E50));
        
        // 设置列宽
        TableColumn column = null;
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            column = orderTable.getColumnModel().getColumn(i);
            switch (i) {
                case 0: // 菜品名称
                    column.setPreferredWidth(150);
                    break;
                case 1: // 单价
                    column.setPreferredWidth(80);
                    break;
                case 2: // 数量
                    column.setPreferredWidth(60);
                    break;
                case 3: // 小计
                    column.setPreferredWidth(80);
                    break;
                case 4: // 备注
                    column.setPreferredWidth(120);
                    break;
            }
        }

        // 初始化按钮
        removeItemButton = new JButton("删除选中项");
        removeItemButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        removeItemButton.setBackground(new Color(0xE74C3C)); // 红色主题
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);
        removeItemButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加悬停效果
        removeItemButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeItemButton.setBackground(new Color(0xC0392B)); // 深红色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeItemButton.setBackground(new Color(0xE74C3C)); // 恢复原色
            }
        });
        
        clearOrderButton = new JButton("清空订单");
        clearOrderButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        clearOrderButton.setBackground(new Color(0xF39C12)); // 橙色主题
        clearOrderButton.setForeground(Color.WHITE);
        clearOrderButton.setFocusPainted(false);
        clearOrderButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加悬停效果
        clearOrderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearOrderButton.setBackground(new Color(0xD35400)); // 深橙色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearOrderButton.setBackground(new Color(0xF39C12)); // 恢复原色
            }
        });
        
        submitOrderButton = new JButton("提交订单");
        submitOrderButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        submitOrderButton.setBackground(new Color(0x27AE60)); // 绿色主题
        submitOrderButton.setForeground(Color.WHITE);
        submitOrderButton.setFocusPainted(false);
        submitOrderButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        // 添加悬停效果
        submitOrderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitOrderButton.setBackground(new Color(0x229954)); // 深绿色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitOrderButton.setBackground(new Color(0x27AE60)); // 恢复原色
            }
        });
        
        selectTableButton = new JButton("选择餐桌");
        selectTableButton.setFont(new Font("微软雅黑", Font.BOLD, 12));
        selectTableButton.setBackground(new Color(0x8E44AD)); // 紫色主题
        selectTableButton.setForeground(Color.WHITE);
        selectTableButton.setFocusPainted(false);
        selectTableButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加悬停效果
        selectTableButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                selectTableButton.setBackground(new Color(0x7D3C98)); // 深紫色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                selectTableButton.setBackground(new Color(0x8E44AD)); // 恢复原色
            }
        });

        // 初始化总计标签
        subtotalLabel = new JLabel("小计: ¥0.00");
        subtotalLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subtotalLabel.setForeground(new Color(0x2C3E50));
        
        taxLabel = new JLabel("税费: ¥0.00");
        taxLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        taxLabel.setForeground(new Color(0x2C3E50));
        
        totalLabel = new JLabel("总计: ¥0.00");
        totalLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0xE74C3C)); // 红色主题

        // 初始化餐桌信息标签
        tableInfoLabel = new JLabel("当前餐桌: 未选择");
        tableInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tableInfoLabel.setForeground(new Color(0x2C3E50));
        
        // 初始化餐桌选择下拉框
        tableComboBox = new JComboBox<>();
        tableComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tableComboBox.setBackground(Color.WHITE);
        tableComboBox.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));

        // 初始化备注区域
        notesArea = new JTextArea();
        notesArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        notesArea.setRows(3);
        notesArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        notesArea.setBackground(Color.WHITE);
        notesArea.setForeground(new Color(0x2C3E50));
    }

    private void setupLayout() {
        // 表格区域
        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        tableScrollPane.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        buttonPanel.add(removeItemButton);
        buttonPanel.add(clearOrderButton);
        buttonPanel.add(submitOrderButton);

        // 总计面板
        JPanel totalPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        totalPanel.setBorder(BorderFactory.createTitledBorder("订单总计"));
        totalPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        totalPanel.add(subtotalLabel);
        totalPanel.add(taxLabel);
        totalPanel.add(totalLabel);

        // 餐桌信息面板
        JPanel tableInfoPanel = new JPanel(new BorderLayout());
        tableInfoPanel.setBorder(BorderFactory.createTitledBorder("餐桌信息"));
        tableInfoPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        tableInfoPanel.add(tableInfoLabel, BorderLayout.NORTH);
        
        // 餐桌选择面板
        JPanel tableSelectionPanel = new JPanel(new FlowLayout());
        tableSelectionPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        tableSelectionPanel.add(new JLabel("选择餐桌: "));
        tableSelectionPanel.add(tableComboBox);
        tableSelectionPanel.add(selectTableButton);
        
        tableInfoPanel.add(tableSelectionPanel, BorderLayout.CENTER);

        // 备注面板
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("订单备注"));
        notesPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        // 右侧信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        infoPanel.add(tableInfoPanel, BorderLayout.NORTH);
        infoPanel.add(totalPanel, BorderLayout.CENTER);
        infoPanel.add(notesPanel, BorderLayout.SOUTH);

        // 主面板布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                             tableScrollPane, infoPanel);
        splitPane.setDividerLocation(500);
        splitPane.setBorder(BorderFactory.createEmptyBorder()); // 移除分割线边框

        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // 删除选中项按钮事件
        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });
        
        // 清空订单按钮事件
        clearOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });
        
        // 提交订单按钮事件
        submitOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitOrder();
            }
        });
        
        // 选择餐桌按钮事件
        selectTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTable();
            }
        });
    }

    // 加载餐桌选项到下拉框
    private void loadTables() {
        tableComboBox.removeAllItems();
        // 添加提示选项
        tableComboBox.addItem("请选择餐桌");
        
        List<Table> tables = tableManager.getAllTables();
        for (Table table : tables) {
            if (table.isAvailable() || table.getTableId() == currentOrder.getTableId()) {
                // 显示空闲餐桌或当前订单的餐桌
                tableComboBox.addItem("餐桌 #" + table.getTableId() + " (" + table.getTableName() + ")");
            }
        }
        
        // 如果当前订单已有餐桌，选中该餐桌
        if (currentOrder.getTableId() > 0) {
            updateTableInfoLabel();
        }
    }

    // 选择餐桌
    private void selectTable() {
        String selectedTableStr = (String) tableComboBox.getSelectedItem();
        if (selectedTableStr == null || "请选择餐桌".equals(selectedTableStr)) {
            JOptionPane.showMessageDialog(this, "请先选择一个餐桌", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 从字符串中提取餐桌ID
        String[] parts = selectedTableStr.split(" ");
        if (parts.length > 1) {
            String tableIdStr = parts[1].substring(1); // 去掉"#"号
            try {
                int selectedTableId = Integer.parseInt(tableIdStr);
                
                // 检查餐桌状态
                Table selectedTable = tableManager.getTableById(selectedTableId);
                if (selectedTable == null) {
                    JOptionPane.showMessageDialog(this, "餐桌不存在", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 如果餐桌非空闲且不是当前订单的餐桌，则不允许选择
                if (!selectedTable.isAvailable() && selectedTableId != currentOrder.getTableId()) {
                    JOptionPane.showMessageDialog(this, 
                        "餐桌 " + selectedTable.getTableName() + " 当前已被占用", 
                        "餐桌已被占用", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 更新订单的餐桌ID
                currentOrder.setTableId(selectedTableId);
                
                // 如果餐桌之前是空闲的，更新其状态为占用
                if (selectedTable.isAvailable()) {
                    tableManager.assignOrderToTable(selectedTableId, currentOrder);
                }
                
                // 更新界面
                updateTableInfoLabel();
                loadTables(); // 重新加载以更新可用餐桌列表
                
                JOptionPane.showMessageDialog(this, 
                    "成功选择餐桌: " + selectedTable.getTableName(), 
                    "餐桌选择成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "无效的餐桌ID", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 更新餐桌信息标签
    private void updateTableInfoLabel() {
        if (currentOrder.getTableId() > 0) {
            Table currentTable = tableManager.getTableById(currentOrder.getTableId());
            if (currentTable != null) {
                tableInfoLabel.setText("当前餐桌: " + currentTable.getTableName() + " (ID: " + currentTable.getTableId() + ")");
            } else {
                tableInfoLabel.setText("当前餐桌: ID " + currentOrder.getTableId() + " (餐桌信息不可用)");
            }
        } else {
            tableInfoLabel.setText("当前餐桌: 未选择");
        }
    }

    // 更新订单显示
    public void updateOrderDisplay() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 添加订单项到表格
        List<OrderItem> items = currentOrder.getItems();
        for (OrderItem item : items) {
            Object[] rowData = {
                item.getMenuItem().getName(),
                "¥" + String.format("%.2f", item.getMenuItem().getPrice()),
                item.getQuantity(),
                "¥" + String.format("%.2f", item.getSubtotal()),
                item.getNotes()
            };
            tableModel.addRow(rowData);
        }
        
        // 确保订单总额被计算
        currentOrder.calculateTotals();
        
        // 更新总计信息
        updateTotalLabels();
        
        // 更新餐桌信息
        updateTableInfoLabel();
    }

    // 更新总计标签
    private void updateTotalLabels() {
        currentOrder.calculateTotals();
        subtotalLabel.setText("小计: ¥" + String.format("%.2f", currentOrder.getSubtotal()));
        taxLabel.setText("税费: ¥" + String.format("%.2f", currentOrder.getTax()));
        totalLabel.setText("总计: ¥" + String.format("%.2f", currentOrder.getTotal()));
    }

    // 删除选中项
    private void removeSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的项目", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取要删除的订单项
        OrderItem itemToRemove = currentOrder.getItems().get(selectedRow);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除 " + itemToRemove.getMenuItem().getName() + " 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            currentOrder.removeItem(itemToRemove);
            updateOrderDisplay();
        }
    }

    // 清空订单
    private void clearOrder() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "订单已经是空的", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要清空整个订单吗？\n注意: 这不会影响餐桌状态。",
            "确认清空",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 创建新的订单列表来清空订单
            currentOrder.getItems().clear();
            updateOrderDisplay();
        }
    }

    // 提交订单
    private void submitOrder() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "订单为空，无法提交", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查是否选择了餐桌
        if (currentOrder.getTableId() <= 0) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "当前订单未关联餐桌，是否继续提交？\n" +
                "选择“是”将创建一个未分配餐桌的订单。\n" +
                "选择“否”将返回并选择餐桌。",
                "餐桌未选择",
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.NO_OPTION) {
                return; // 用户选择返回并选择餐桌
            }
        }

        // 保存备注
        String notes = notesArea.getText().trim();
        if (!notes.isEmpty()) {
            // 这里可以将备注与订单关联，当前实现中我们仅显示提醒
            JOptionPane.showMessageDialog(this, "订单备注已保存", "提示", JOptionPane.INFORMATION_MESSAGE);
        }

        // 显示订单摘要
        StringBuilder summary = new StringBuilder();
        summary.append("订单摘要:\n");
        List<OrderItem> items = currentOrder.getItems();
        for (OrderItem item : items) {
            summary.append("- ")
                   .append(item.getMenuItem().getName())
                   .append(" x")
                   .append(item.getQuantity())
                   .append(": ¥")
                   .append(String.format("%.2f", item.getSubtotal()))
                   .append("\n");
        }
        
        // 如果有关联餐桌，添加餐桌信息
        if (currentOrder.getTableId() > 0) {
            Table table = tableManager.getTableById(currentOrder.getTableId());
            if (table != null) {
                summary.append("\n餐桌: ").append(table.getTableName()).append("\n");
            }
        }
        
        summary.append("\n小计: ¥").append(String.format("%.2f", currentOrder.getSubtotal()))
               .append("\n税费: ¥").append(String.format("%.2f", currentOrder.getTax()))
               .append("\n总计: ¥").append(String.format("%.2f", currentOrder.getTotal()));

        int result = JOptionPane.showConfirmDialog(
            this,
            summary.toString() + "\n\n确定要提交订单吗？",
            "确认提交订单",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 更新订单状态
            currentOrder.setStatus("已提交");
            
            // 将订单保存到数据库
            dbManager.saveOrder(currentOrder);
            
            JOptionPane.showMessageDialog(this, 
                "订单已成功提交到厨房！\n订单号: " + currentOrder.getOrderId() + 
                "\n餐桌: " + (currentOrder.getTableId() > 0 ? 
                    tableManager.getTableById(currentOrder.getTableId()).getTableName() : "未分配"), 
                "提交成功", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // 为下一个订单创建一个新的Order实例
            // 在实际应用中，可能希望保留当前订单或创建新订单
            currentOrder.getItems().clear();
            updateOrderDisplay();
        }
    }

    // 刷新面板
    public void refresh() {
        updateOrderDisplay();
        loadTables();
    }
}