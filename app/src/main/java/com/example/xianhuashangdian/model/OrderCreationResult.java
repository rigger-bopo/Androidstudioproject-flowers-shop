package com.example.xianhuashangdian.model;

public class OrderCreationResult {
    private final boolean success;
    private final long orderId;
    private final String orderNo;
    private final String message;

    private OrderCreationResult(boolean success, long orderId, String orderNo, String message) {
        this.success = success;
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.message = message;
    }

    public static OrderCreationResult success(long orderId, String orderNo) {
        return new OrderCreationResult(true, orderId, orderNo, "");
    }

    public static OrderCreationResult failure(String message) {
        return new OrderCreationResult(false, -1, "", message);
    }

    public boolean isSuccess() {
        return success;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getMessage() {
        return message;
    }
}
