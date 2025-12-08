package com.restaurant.management;

/**
 * 餐桌类 - 表示餐厅中的餐桌
 */
public class Table {
    private int tableId;
    private int capacity;
    private String status; // "AVAILABLE", "OCCUPIED", "RESERVED", "CLEANING"

    public Table(int tableId, int capacity) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.status = "AVAILABLE"; // 默认状态为可用
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
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

    @Override
    public String toString() {
        return "Table{" +
                "tableId=" + tableId +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                '}';
    }
}