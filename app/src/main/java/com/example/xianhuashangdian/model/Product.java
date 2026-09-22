package com.example.xianhuashangdian.model;

public class Product {
    private final long id;
    private final String name;
    private final String category;
    private final String imageKey;
    private final int accentColor;
    private final double price;
    private final int stock;
    private final String description;
    private final String flowerLanguage;

    public Product(long id, String name, String category, String imageKey, int accentColor,
                   double price, int stock, String description, String flowerLanguage) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageKey = imageKey;
        this.accentColor = accentColor;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.flowerLanguage = flowerLanguage;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImageKey() {
        return imageKey;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public String getFlowerLanguage() {
        return flowerLanguage;
    }
}
