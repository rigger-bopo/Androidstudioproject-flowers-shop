package com.example.xianhuashangdian.model;

public class Coupon {
    private final long id;
    private final String code;
    private final String title;
    private final double amount;
    private final String status;
    private final String createdAt;

    public Coupon(long id, String code, String title, double amount,
                  String status, String createdAt) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
