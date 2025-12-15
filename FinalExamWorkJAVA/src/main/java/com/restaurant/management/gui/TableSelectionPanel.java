package com.restaurant.management.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 餐桌选择面板
 */
public class TableSelectionPanel extends JPanel {
    private DatabaseManager dbManager;
    private TableManager tableManager;
    private Order currentOrder;
    private OrderPanel orderPanel;
    private JPanel tableGridPanel;
    private JButton confirmTableButton;
    private JLabel selectedTableLabel;
    private int selectedTableId = 0;

    public TableSelectionPanel(DatabaseManager dbManager, TableManager tableManager, Order currentOrder, OrderPanel orderPanel) {
        this.dbManager = dbManager;
        this.tableManager = tableManager;
        this.currentOrder = currentOrder;
        this.orderPanel = orderPanel;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTables();
    }

    private void initializeComponents() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        setBackground(new Color(0xECF0F1)); // 设置背景色

        // 初始化餐桌网格面板
        tableGridPanel = new JPanel(new GridLayout(0, 5, 10, 10)); // 5列，间距10px
        tableGridPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        JScrollPane scrollPane = new JScrollPane(tableGridPanel);
        scrollPane.setBorder(new LineBorder(new Color(0xBDC3C7), 1, true));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        // 初始化确认按钮
        confirmTableButton = new JButton("确认选择餐桌");
        confirmTableButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmTableButton.setBackground(new Color(0x27AE60)); // 绿色主题
        confirmTableButton.setForeground(Color.WHITE);
        confirmTableButton.setFocusPainted(false);
        confirmTableButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBDC3C7), 1, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        confirmTableButton.setEnabled(false); // 初始状态禁用，直到选择餐桌
        
        // 添加悬停效果
        confirmTableButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                confirmTableButton.setBackground(new Color(0x229954)); // 深绿色
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                confirmTableButton.setBackground(new Color(0x27AE60)); // 恢复原色
            }
        });

        // 初始化选中餐桌标签
        selectedTableLabel = new JLabel("未选择餐桌");
        selectedTableLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        selectedTableLabel.setForeground(new Color(0x2C3E50));
        selectedTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        // 餐桌选择区域
        JPanel tableSelectionPanel = new JPanel(new BorderLayout());
        tableSelectionPanel.setBorder(BorderFactory.createTitledBorder("选择餐桌"));
        tableSelectionPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        tableSelectionPanel.add(new JScrollPane(tableGridPanel), BorderLayout.CENTER);

        // 信息面板
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        infoPanel.add(selectedTableLabel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0xECF0F1)); // 设置背景色
        buttonPanel.add(confirmTableButton);

        // 主面板布局
        add(tableSelectionPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // 确认选择餐桌按钮事件
        confirmTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmTableSelection();
            }
        });
    }

    // 加载餐桌
    private void loadTables() {
        // 清空现有餐桌按钮
        tableGridPanel.removeAll();
        
        // 获取所有餐桌
        List<Table> tables = tableManager.getAllTables();
        
        for (Table table : tables) {
            JButton tableButton = createTableButton(table);
            tableGridPanel.add(tableButton);
        }
        
        // 重新验证和重绘
        tableGridPanel.revalidate();
        tableGridPanel.repaint();
    }

    // 创建餐桌按钮
    private JButton createTableButton(Table table) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        
        // 设置按钮文本
        JLabel tableLabel = new JLabel("餐桌 " + table.getTableId(), SwingConstants.CENTER);
        tableLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        JLabel capacityLabel = new JLabel("容量: " + table.getCapacity(), SwingConstants.CENTER);
        capacityLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        
        JLabel statusLabel = new JLabel(table.getStatus(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        
        // 根据状态设置颜色
        if (table.isAvailable()) {
            button.setBackground(new Color(0x27AE60)); // 绿色 - 空闲
            statusLabel.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(0xE74C3C)); // 红色 - 占用
            statusLabel.setForeground(Color.WHITE);
        }
        
        button.setBorder(BorderFactory.createLineBorder(new Color(0x2C3E50), 2));
        
        // 添加标签到按钮
        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.setOpaque(false);
        textPanel.add(tableLabel);
        textPanel.add(capacityLabel);
        textPanel.add(statusLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        // 添加点击事件
        button.addActionListener(e -> selectTable(table));
        
        return button;
    }

    // 选择餐桌
    private void selectTable(Table table) {
        // 如果餐桌被占用，不允许选择
        if (!table.isAvailable()) {
            JOptionPane.showMessageDialog(this, 
                "餐桌 " + table.getTableName() + " 当前已被占用，无法选择！", 
                "餐桌已被占用", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 取消之前选中的按钮样式
        for (Component comp : tableGridPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                Table t = getTableFromButton(btn);
                if (t != null) {
                    if (t.isAvailable()) {
                        btn.setBackground(new Color(0x27AE60)); // 绿色 - 空闲
                    } else {
                        btn.setBackground(new Color(0xE74C3C)); // 红色 - 占用
                    }
                }
            }
        }
        
        // 高亮选中的餐桌
        Component[] components = tableGridPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                Table t = getTableFromButton(btn);
                if (t != null && t.getTableId() == table.getTableId()) {
                    btn.setBackground(new Color(0xF39C12)); // 橙色 - 选中
                    break;
                }
            }
        }
        
        // 更新选中的餐桌ID和标签
        this.selectedTableId = table.getTableId();
        this.selectedTableLabel.setText("已选择: " + table.getTableName() + " (容量: " + table.getCapacity() + ")");
        
        // 启用确认按钮
        confirmTableButton.setEnabled(true);
    }
    
    // 从按钮获取餐桌对象
    private Table getTableFromButton(JButton button) {
        // 通过按钮的文本获取餐桌ID
        String buttonText = button.getText();
        if (buttonText.isEmpty()) {
            // 从按钮的子组件获取文本
            Component[] components = button.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    Component[] innerComponents = panel.getComponents();
                    if (innerComponents.length > 0 && innerComponents[0] instanceof JLabel) {
                        String label = ((JLabel) innerComponents[0]).getText();
                        // 从标签文本中提取餐桌ID
                        if (label.startsWith("餐桌 ")) {
                            String idStr = label.substring(3); // 去掉"餐桌 "前缀
                            try {
                                int tableId = Integer.parseInt(idStr);
                                return tableManager.getTableById(tableId);
                            } catch (NumberFormatException e) {
                                // 解析失败，继续处理其他组件
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // 确认选择餐桌
    private void confirmTableSelection() {
        if (selectedTableId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个餐桌", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中的餐桌
        Table table = tableManager.getTableById(selectedTableId);
        if (table == null) {
            JOptionPane.showMessageDialog(this, "选择的餐桌不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!table.isAvailable()) {
            JOptionPane.showMessageDialog(this, 
                "餐桌 " + table.getTableName() + " 当前已被占用，无法选择！", 
                "餐桌已被占用", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 更新订单的餐桌ID
        currentOrder.setTableId(selectedTableId);
        
        // 更新餐桌状态为占用
        tableManager.assignOrderToTable(selectedTableId, currentOrder);
        
        // 刷新界面
        loadTables();
        
        JOptionPane.showMessageDialog(this, 
            "餐桌 " + table.getTableName() + " 选择成功！\n" +
            "现在您可以为该餐桌点餐了。", 
            "选择成功", 
            JOptionPane.INFORMATION_MESSAGE);
            
        // 更新选中状态
        selectedTableLabel.setText("已选择: " + table.getTableName() + " (容量: " + table.getCapacity() + ")");
        confirmTableButton.setEnabled(false);
        
        // 刷新订单面板
        if (orderPanel != null) {
            orderPanel.refresh();
        }
    }

    // 刷新面板
    public void refresh() {
        loadTables();
    }
}