package com.example.xianhuashangdian.model;

public class Merchant {
    private final long id;
    private final String name;
    private final String category;
    private final String specialty;
    private final String address;
    private final String phone;
    private final double rating;
    private final double minPrice;
    private final String badge;
    private final int accentColor;

    public Merchant(long id, String name, String category, String specialty, String address,
                    String phone, double rating, double minPrice, String badge, int accentColor) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.specialty = specialty;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
        this.minPrice = minPrice;
        this.badge = badge;
        this.accentColor = accentColor;
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

    public String getSpecialty() {
        return specialty;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public String getBadge() {
        return badge;
    }

    public int getAccentColor() {
        return accentColor;
    }
}
