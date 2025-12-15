package com.restaurant.management.gui;

/**
 * 菜单项类
 */
public class MenuItem {
    private int id;           // 菜品ID
    private String name;      // 菜品名称
    private String category;  // 菜品类别
    private double price;     // 价格
    private String description; // 描述
    private String imagePath;   // 图片路径
    private boolean available;  // 是否可点

    // 构造函数
    public MenuItem(int id, String name, String category, double price, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
        this.available = true;  // 默认可点
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return name + " - ¥" + price;
    }
}