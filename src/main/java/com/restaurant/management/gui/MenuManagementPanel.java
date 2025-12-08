package com.restaurant.management.gui;

import com.restaurant.management.MenuItem;
import com.restaurant.management.RestaurantManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * 菜单管理面板
 */
public class MenuManagementPanel extends JPanel {
    private RestaurantManagementSystem rms;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, descriptionField, priceField, categoryField;

    public MenuManagementPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadMenuData();
    }

    private void initializeComponents() {
        // 创建表格模型
        String[] columnNames = {"ID", "名称", "描述", "价格", "分类"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 创建输入字段
        idField = new JTextField(5);
        nameField = new JTextField(15);
        descriptionField = new JTextField(20);
        priceField = new JTextField(10);
        categoryField = new JTextField(10);

        // 设置表格行高
        menuTable.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建顶部输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加/编辑菜单项"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加组件到输入面板
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; inputPanel.add(idField, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 3; inputPanel.add(nameField, gbc);
        gbc.gridx = 4; inputPanel.add(new JLabel("分类:"), gbc);
        gbc.gridx = 5; inputPanel.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; inputPanel.add(descriptionField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("价格:"), gbc);
        gbc.gridx = 1; inputPanel.add(priceField, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("添加");
        JButton updateButton = new JButton("更新");
        JButton deleteButton = new JButton("删除");
        JButton refreshButton = new JButton("刷新");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 添加按钮事件
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMenuItem();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMenuItem();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMenuItem();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMenuData();
            }
        });

        // 将按钮面板添加到输入面板
        inputPanel.add(buttonPanel, gbc);

        // 创建表格滚动面板
        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        // 将组件添加到主面板
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addMenuItem() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            String category = categoryField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写所有必填字段！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MenuItem menuItem = new MenuItem(id, name, description, price, category);
            rms.addMenuItem(menuItem);
            loadMenuData();

            // 清空输入字段
            clearInputFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的价格和ID！", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "添加菜品失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要更新的菜品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            String category = categoryField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写所有必填字段！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 从系统中删除原菜品
            rms.removeMenuItem(id);
            
            // 添加更新后的菜品
            MenuItem menuItem = new MenuItem(id, name, description, price, category);
            rms.addMenuItem(menuItem);
            
            loadMenuData();
            clearInputFields();
            
            JOptionPane.showMessageDialog(this, "菜品更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的价格和ID！", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "更新菜品失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的菜品！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除菜品 '" + tableModel.getValueAt(selectedRow, 1) + "' 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            rms.removeMenuItem(id);
            loadMenuData();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "菜品删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadMenuData() {
        // 清空表格
        tableModel.setRowCount(0);

        // 从系统中获取菜单数据并添加到表格
        List<MenuItem> menuItems = rms.getMenuItems();
        for (MenuItem item : menuItems) {
            Object[] rowData = {
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory()
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearInputFields() {
        idField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        categoryField.setText("");
    }
}