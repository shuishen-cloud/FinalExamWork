package com.restaurant.management;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单类 - 表示客户的一份订单
 */
public class Order {
    private int orderId;
    private List<OrderItem> orderItems;
    private LocalDateTime orderTime;
    private String customerName;
    private String status; // "PENDING", "PREPARING", "READY", "COMPLETED", "CANCELLED"

    public Order(int orderId, String customerName) {
        this.orderId = orderId;
        this.orderItems = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.customerName = customerName;
        this.status = "PENDING"; // 默认状态为待处理
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public void removeItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + getTotalAmount() +
                ", orderTime=" + orderTime +
                ", items=" + orderItems.size() +
                '}';
    }
}