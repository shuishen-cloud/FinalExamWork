package com.restaurant.management;

import java.math.BigDecimal;

/**
 * 订单项类 - 表示订单中的单个菜品及其数量
 */
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

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

    public BigDecimal getSubtotal() {
        return menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItem=" + menuItem.getName() +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}