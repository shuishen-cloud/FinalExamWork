package com.restaurant.management.gui;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 餐厅管理系统主类
 */
public class RestaurantManagementSystem extends JFrame {
    private DatabaseManager dbManager;
    private TableManager tableManager;
    private Order currentOrder;
    
    // 面板
    private MenuPanel menuPanel;
    private OrderPanel orderPanel;
    private BillPanel billPanel;
    private TableSelectionPanel tableSelectionPanel;
    
    // 选项卡面板
    private JTabbedPane tabbedPane;
    
    // 菜单栏
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem exitItem;
    private JMenu viewMenu;
    private JMenuItem menuViewItem;
    private JMenuItem orderViewItem;
    private JMenuItem billViewItem;
    private JMenuItem tableSelectionViewItem;
    private JMenu helpMenu;
    private JMenuItem aboutItem;

    public RestaurantManagementSystem() {
        // 初始化第一个订单
        this.currentOrder = new Order(generateOrderId());
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }

    private void initializeComponents() {
        // 设置窗口标题
        setTitle("餐厅点餐管理系统");
        // 设置窗口图标（如果有的话）
        
        // 初始化数据库管理器
        dbManager = new DatabaseManager();
        // 初始化餐桌管理器
        tableManager = new TableManager(dbManager);
        
        // 初始化面板
        orderPanel = new OrderPanel(currentOrder, dbManager);
        menuPanel = new MenuPanel(dbManager, currentOrder, orderPanel);
        billPanel = new BillPanel(dbManager);
        tableSelectionPanel = new TableSelectionPanel(dbManager, tableManager, currentOrder, orderPanel);
        
        // 初始化选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        // 设置选项卡面板的背景色
        tabbedPane.setBackground(new Color(0xECF0F1));
        
        // 添加面板到选项卡
        tabbedPane.addTab("餐桌选择", null, tableSelectionPanel, "选择餐桌并开始点餐");
        tabbedPane.addTab("菜单浏览", null, menuPanel, "浏览和选择菜品");
        tabbedPane.addTab("订单管理", null, orderPanel, "管理当前订单");
        tabbedPane.addTab("账单管理", null, billPanel, "查看和处理账单");
        
        // 初始化菜单栏
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0x2C3E50));
        menuBar.setForeground(Color.WHITE);
        
        // 文件菜单
        fileMenu = new JMenu("文件");
        fileMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fileMenu.setForeground(Color.WHITE);
        exitItem = new JMenuItem("退出");
        exitItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitItem.setBackground(new Color(0x2C3E50));
        exitItem.setForeground(Color.WHITE);
        fileMenu.add(exitItem);
        
        // 视图菜单
        viewMenu = new JMenu("视图");
        viewMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        viewMenu.setForeground(Color.WHITE);
        tableSelectionViewItem = new JMenuItem("餐桌选择");
        tableSelectionViewItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tableSelectionViewItem.setBackground(new Color(0x2C3E50));
        tableSelectionViewItem.setForeground(Color.WHITE);
        menuViewItem = new JMenuItem("菜单浏览");
        menuViewItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        menuViewItem.setBackground(new Color(0x2C3E50));
        menuViewItem.setForeground(Color.WHITE);
        orderViewItem = new JMenuItem("订单管理");
        orderViewItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        orderViewItem.setBackground(new Color(0x2C3E50));
        orderViewItem.setForeground(Color.WHITE);
        billViewItem = new JMenuItem("账单管理");
        billViewItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        billViewItem.setBackground(new Color(0x2C3E50));
        billViewItem.setForeground(Color.WHITE);
        
        viewMenu.add(tableSelectionViewItem);
        viewMenu.add(menuViewItem);
        viewMenu.add(orderViewItem);
        viewMenu.add(billViewItem);
        
        // 帮助菜单
        helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        helpMenu.setForeground(Color.WHITE);
        aboutItem = new JMenuItem("关于");
        aboutItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        aboutItem.setBackground(new Color(0x2C3E50));
        aboutItem.setForeground(Color.WHITE);
        helpMenu.add(aboutItem);
        
        // 添加菜单到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }

    private void setupLayout() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 防止直接关闭
        setSize(1000, 700);
        setLocationRelativeTo(null); // 窗口居中
        getContentPane().setBackground(new Color(0xECF0F1)); // 设置主窗口背景色
        
        // 设置主面板
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // 添加状态栏
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(0x2C3E50));
        statusBar.setForeground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        JLabel statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel);
        
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // 退出菜单项事件
        exitItem.addActionListener(e -> exitApplication());
        
        // 视图菜单项事件
        tableSelectionViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        menuViewItem.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            // 切换到菜单面板时，更新餐桌信息显示
            if (menuPanel != null) {
                menuPanel.refreshTableInfo();
            }
        });
        orderViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        billViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        // 关于菜单项事件
        aboutItem.addActionListener(e -> showAboutDialog());
        
        // 选项卡更改事件
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // 餐桌选择面板
                    tableSelectionPanel.refresh();
                    break;
                case 1: // 菜单面板
                    menuPanel.refresh();
                    break;
                case 2: // 订单面板
                    orderPanel.refresh();
                    break;
                case 3: // 账单面板
                    billPanel.refresh();
                    break;
            }
        });
    }

    private void setupWindow() {
        // 设置窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    // 生成订单ID
    private int generateOrderId() {
        // 在实际应用中，这通常会从数据库中获取下一个可用ID
        // 现在使用一个简单的计数器
        return (int) (System.currentTimeMillis() % 100000);
    }

    // 退出应用程序
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要退出餐厅点餐管理系统吗？\n所有未保存的订单数据将会丢失。",
            "确认退出",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 关闭数据库连接
            if (dbManager != null) {
                dbManager.closeConnection();
            }
            
            // 退出应用程序
            System.exit(0);
        }
    }

    // 显示关于对话框
    private void showAboutDialog() {
        String aboutText = "餐厅点餐管理系统 v1.0\n\n" +
                          "这是一个基于Java Swing的餐厅点餐管理应用。\n\n" +
                          "功能包括：\n" +
                          "- 菜单浏览和搜索\n" +
                          "- 订单管理\n" +
                          "- 账单处理和支付\n" +
                          "- 数据库存储\n\n" +
                          "开发时间: 2025年";
        
        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "关于餐厅点餐管理系统",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * 启动应用程序
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            // 尝试使用系统外观
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                } else if ("Metal".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // 在事件调度线程中启动应用程序
        SwingUtilities.invokeLater(() -> {
            try {
                RestaurantManagementSystem system = new RestaurantManagementSystem();
                system.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "应用程序启动失败: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}