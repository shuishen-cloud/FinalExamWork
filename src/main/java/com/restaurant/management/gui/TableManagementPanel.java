package com.restaurant.management.gui;

import com.restaurant.management.RestaurantManagementSystem;
import com.restaurant.management.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 餐桌管理面板
 */
public class TableManagementPanel extends JPanel {
    private RestaurantManagementSystem rms;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tableIdField, capacityField;
    private JComboBox<String> statusComboBox;

    public TableManagementPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadTableData();
    }

    private void initializeComponents() {
        // 表格模型
        String[] columnNames = {"餐桌ID", "容量", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 输入字段
        tableIdField = new JTextField(10);
        capacityField = new JTextField(10);
        
        // 状态下拉框
        statusComboBox = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "RESERVED", "CLEANING"});
        
        // 设置表格行高
        this.table.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建顶部输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("餐桌管理"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("餐桌ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(tableIdField, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("容量:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(capacityField, gbc);
        gbc.gridx = 4;
        inputPanel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 5;
        inputPanel.add(statusComboBox, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("添加餐桌");
        JButton updateButton = new JButton("更新状态");
        JButton deleteButton = new JButton("删除餐桌");
        JButton refreshButton = new JButton("刷新");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 添加按钮事件
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTable();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTableStatus();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTable();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData();
            }
        });

        // 将按钮面板添加到输入面板
        inputPanel.add(buttonPanel, gbc);

        // 创建表格滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // 将组件添加到主面板
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addTable() {
        try {
            int tableId = Integer.parseInt(tableIdField.getText().trim());
            int capacity = Integer.parseInt(capacityField.getText().trim());

            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "餐桌容量必须大于0！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 检查是否已存在相同ID的餐桌
            if (rms.findTableById(tableId) != null) {
                JOptionPane.showMessageDialog(this, "餐桌ID已存在！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            com.restaurant.management.Table newTable = new com.restaurant.management.Table(tableId, capacity);
            rms.addTable(newTable);
            loadTableData();

            // 清空输入字段
            clearInputFields();
            JOptionPane.showMessageDialog(this, "餐桌添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的餐桌ID和容量！", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "添加餐桌失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要更新状态的餐桌！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tableId = (int) tableModel.getValueAt(selectedRow, 0);
            String newStatus = (String) statusComboBox.getSelectedItem();

            com.restaurant.management.Table tableToUpdate = rms.findTableById(tableId);
            if (tableToUpdate != null) {
                tableToUpdate.setStatus(newStatus);
                loadTableData();
                JOptionPane.showMessageDialog(this, "餐桌状态更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "找不到指定餐桌！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "更新餐桌状态失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的餐桌！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tableId = (int) tableModel.getValueAt(selectedRow, 0);
        String tableName = tableModel.getValueAt(selectedRow, 0).toString();
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除餐桌 '" + tableName + "' 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // 在实际应用中，可能需要先删除餐桌上的订单等关联数据
            // 这里简单地从列表中移除餐桌
            // 注意：在实际系统中，可能需要修改RestaurantManagementSystem类来支持删除餐桌
            // 由于当前系统没有提供删除餐桌的方法，我们暂时只显示提示
            JOptionPane.showMessageDialog(this, "由于系统限制，当前版本不支持删除餐桌功能。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTableData() {
        // 清空表格
        tableModel.setRowCount(0);

        // 从系统中获取餐桌数据并添加到表格
        List<com.restaurant.management.Table> tables = rms.getTables();
        for (com.restaurant.management.Table table : tables) {
            Object[] rowData = {
                table.getTableId(),
                table.getCapacity(),
                table.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearInputFields() {
        tableIdField.setText("");
        capacityField.setText("");
        statusComboBox.setSelectedItem("AVAILABLE");
    }
}