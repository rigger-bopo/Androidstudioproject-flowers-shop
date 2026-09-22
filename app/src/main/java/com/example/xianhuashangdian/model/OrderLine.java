package com.example.xianhuashangdian.model;

public class OrderLine {
    private final String productName;
    private final double unitPrice;
    private final int quantity;
    private final double subtotal;

    public OrderLine(String productName, double unitPrice, int quantity, double subtotal) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
