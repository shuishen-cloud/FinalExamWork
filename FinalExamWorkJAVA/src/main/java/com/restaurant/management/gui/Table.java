package com.restaurant.management.gui;

import java.util.Date;

/**
 * 餐桌类
 */
public class Table {
    private int tableId;              // 餐桌ID
    private String tableName;         // 餐桌名称
    private int capacity;             // 餐桌容量
    private String status;            // 餐桌状态（空闲、占用、预定等）
    private Order currentOrder;       // 当前订单
    private Date checkInTime;         // 客户入住时间
    private Date checkOutTime;        // 客户离开时间

    // 构造函数
    public Table(int tableId, String tableName, int capacity) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.capacity = capacity;
        this.status = "空闲";  // 默认状态
        this.currentOrder = null;
        this.checkInTime = null;
        this.checkOutTime = null;
    }

    // Getter和Setter方法
    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    // 检查餐桌是否空闲
    public boolean isAvailable() {
        return "空闲".equals(status);
    }

    // 占用餐桌
    public void occupyTable(Order order) {
        if (order != null) {
            this.currentOrder = order;
            this.status = "占用";
            this.checkInTime = new Date(); // 记录客户入住时间
            this.checkOutTime = null; // 重置离开时间
        }
    }

    // 释放餐桌
    public void releaseTable() {
        this.currentOrder = null;
        this.status = "空闲";
        this.checkOutTime = new Date(); // 记录客户离开时间
    }

    @Override
    public String toString() {
        return tableName + " (ID: " + tableId + ", 容量: " + capacity + ", 状态: " + status + ")";
    }
}