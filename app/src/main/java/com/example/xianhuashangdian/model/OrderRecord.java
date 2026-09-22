package com.example.xianhuashangdian.model;

public class OrderRecord {
    private final long id;
    private final String orderNo;
    private final String paymentMethod;
    private final double totalAmount;
    private final double discountAmount;
    private final String status;
    private final String createdAt;
    private final String itemsSummary;

    public OrderRecord(long id, String orderNo, String paymentMethod, double totalAmount,
                       double discountAmount, String status, String createdAt,
                       String itemsSummary) {
        this.id = id;
        this.orderNo = orderNo;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.itemsSummary = itemsSummary;
    }

    public long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }
}
