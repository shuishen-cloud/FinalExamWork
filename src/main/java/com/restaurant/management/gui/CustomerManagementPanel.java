package com.restaurant.management.gui;

import com.restaurant.management.Customer;
import com.restaurant.management.RestaurantManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 顾客管理面板
 */
public class CustomerManagementPanel extends JPanel {
    private RestaurantManagementSystem rms;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField customerIdField, nameField, phoneField, emailField, addressField;

    public CustomerManagementPanel(RestaurantManagementSystem rms) {
        this.rms = rms;
        initializeComponents();
        setupLayout();
        loadCustomerData();
    }

    private void initializeComponents() {
        // 表格模型
        String[] columnNames = {"顾客ID", "姓名", "电话", "邮箱", "地址"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 输入字段
        customerIdField = new JTextField(10);
        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(20);
        addressField = new JTextField(25);
        
        // 设置表格行高
        customerTable.setRowHeight(25);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建顶部输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("顾客管理"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 第一行：ID和姓名
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("顾客ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(customerIdField, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(nameField, gbc);

        // 第二行：电话和邮箱
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("电话:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(phoneField, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("邮箱:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(emailField, gbc);

        // 第三行：地址
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("地址:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        inputPanel.add(addressField, gbc);
        gbc.gridwidth = 1;

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("添加顾客");
        JButton updateButton = new JButton("更新顾客");
        JButton deleteButton = new JButton("删除顾客");
        JButton refreshButton = new JButton("刷新");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 添加按钮事件
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerData();
            }
        });

        // 将按钮面板添加到输入面板
        inputPanel.add(buttonPanel, gbc);

        // 创建表格滚动面板
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // 将组件添加到主面板
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名和电话为必填项！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int customerId = rms.addCustomer(name, phone, email, address);
            loadCustomerData();

            // 清空输入字段
            clearInputFields();
            JOptionPane.showMessageDialog(this, "顾客添加成功，顾客ID：" + customerId, "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "添加顾客失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要更新的顾客！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名和电话为必填项！", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 在实际应用中，需要在RestaurantManagementSystem中添加更新顾客的方法
            // 由于当前系统没有提供此方法，我们显示提示信息
            JOptionPane.showMessageDialog(this, "当前版本不支持直接更新顾客信息，可通过删除后重新添加实现。", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "更新顾客失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的顾客！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除顾客 '" + customerName + "' 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // 在实际应用中，需要在RestaurantManagementSystem中添加删除顾客的方法
            // 由于当前系统没有提供此方法，我们显示提示信息
            JOptionPane.showMessageDialog(this, "当前版本不支持删除顾客功能。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadCustomerData() {
        // 清空表格
        tableModel.setRowCount(0);

        // 从系统中获取顾客数据并添加到表格
        List<Customer> customers = rms.getCustomers();
        for (Customer customer : customers) {
            Object[] rowData = {
                customer.getCustomerId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress()
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearInputFields() {
        customerIdField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
    }
}