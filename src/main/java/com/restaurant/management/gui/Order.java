package com.restaurant.management.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单类
 */
public class Order {
    private int orderId;              // 订单ID
    private List<OrderItem> items;    // 订单项列表
    private Date orderTime;           // 下单时间
    private String status;            // 订单状态（如"进行中"、"已完成"）
    private double subtotal;          // 小计
    private double tax;               // 税费
    private double total;             // 总计
    private static final double TAX_RATE = 0.1;  // 税率10%

    // 构造函数
    public Order(int orderId) {
        this.orderId = orderId;
        this.items = new ArrayList<>();
        this.orderTime = new Date();
        this.status = "进行中";  // 默认状态
        this.subtotal = 0.0;
        this.tax = 0.0;
        this.total = 0.0;
    }

    // Getter和Setter方法
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    // 添加订单项
    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotals();
    }

    // 移除订单项
    public void removeItem(OrderItem item) {
        items.remove(item);
        calculateTotals();
    }

    // 计算订单总额的方法
    public void calculateTotals() {
        // 计算小计
        this.subtotal = 0.0;
        for (OrderItem item : items) {
            this.subtotal += item.getSubtotal();
        }
        
        // 计算税费
        this.tax = this.subtotal * TAX_RATE;
        
        // 计算总计
        this.total = this.subtotal + this.tax;
    }

    @Override
    public String toString() {
        return "订单 #" + orderId + " - 总计: ¥" + String.format("%.2f", total);
    }
}