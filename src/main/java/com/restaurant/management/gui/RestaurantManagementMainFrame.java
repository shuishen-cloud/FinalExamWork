package com.restaurant.management.gui;

import com.restaurant.management.RestaurantManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 餐厅管理系统主窗口类
 */
public class RestaurantManagementMainFrame extends JFrame {
    private RestaurantManagementSystem rms;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // 各功能面板
    private MenuManagementPanel menuPanel;
    private OrderManagementPanel orderPanel;
    private TableManagementPanel tablePanel;
    private CustomerManagementPanel customerPanel;
    private ReportPanel reportPanel;
    
    public RestaurantManagementMainFrame() {
        this.rms = new RestaurantManagementSystem();
        initializeComponents();
        setupLayout();
        setupMenu();
        setupEventHandlers();
        
        // 设置窗口属性
        setTitle("餐厅管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // 居中显示
        setVisible(true);
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // 初始化各个功能面板
        menuPanel = new MenuManagementPanel(rms);
        orderPanel = new OrderManagementPanel(rms);
        tablePanel = new TableManagementPanel(rms);
        customerPanel = new CustomerManagementPanel(rms);
        reportPanel = new ReportPanel(rms);
        
        // 添加面板到主面板
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(orderPanel, "ORDER");
        mainPanel.add(tablePanel, "TABLE");
        mainPanel.add(customerPanel, "CUSTOMER");
        mainPanel.add(reportPanel, "REPORT");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(exitItem);
        
        // 功能菜单
        JMenu functionMenu = new JMenu("功能");
        
        JMenuItem menuManagementItem = new JMenuItem("菜单管理");
        menuManagementItem.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        functionMenu.add(menuManagementItem);
        
        JMenuItem orderManagementItem = new JMenuItem("订单管理");
        orderManagementItem.addActionListener(e -> cardLayout.show(mainPanel, "ORDER"));
        functionMenu.add(orderManagementItem);
        
        JMenuItem tableManagementItem = new JMenuItem("餐桌管理");
        tableManagementItem.addActionListener(e -> cardLayout.show(mainPanel, "TABLE"));
        functionMenu.add(tableManagementItem);
        
        JMenuItem customerManagementItem = new JMenuItem("顾客管理");
        customerManagementItem.addActionListener(e -> cardLayout.show(mainPanel, "CUSTOMER"));
        functionMenu.add(customerManagementItem);
        
        JMenuItem reportItem = new JMenuItem("报表");
        reportItem.addActionListener(e -> cardLayout.show(mainPanel, "REPORT"));
        functionMenu.add(reportItem);
        
        menuBar.add(systemMenu);
        menuBar.add(functionMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // 添加窗口关闭事件处理
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int result = JOptionPane.showConfirmDialog(
                    RestaurantManagementMainFrame.this,
                    "确定要退出系统吗？",
                    "确认退出",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RestaurantManagementMainFrame();
        });
    }
}