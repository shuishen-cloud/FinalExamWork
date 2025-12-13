package com.restaurant.management.gui;

/**
 * 订单项类
 */
public class OrderItem {
    private MenuItem menuItem;  // 关联的菜单项
    private int quantity;       // 数量
    private String notes;       // 备注

    // 构造函数
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.notes = "";
    }

    public OrderItem(MenuItem menuItem, int quantity, String notes) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.notes = notes;
    }

    // Getter和Setter方法
    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // 计算小计
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity + " = ¥" + String.format("%.2f", getSubtotal());
    }
}