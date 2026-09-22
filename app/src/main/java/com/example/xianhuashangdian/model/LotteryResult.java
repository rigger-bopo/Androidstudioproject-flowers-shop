package com.example.xianhuashangdian.model;

public class LotteryResult {
    private final boolean success;
    private final int sectorIndex;
    private final String title;
    private final double couponAmount;
    private final int remainingDraws;
    private final String message;

    private LotteryResult(boolean success, int sectorIndex, String title,
                          double couponAmount, int remainingDraws, String message) {
        this.success = success;
        this.sectorIndex = sectorIndex;
        this.title = title;
        this.couponAmount = couponAmount;
        this.remainingDraws = remainingDraws;
        this.message = message;
    }

    public static LotteryResult success(int sectorIndex, String title,
                                       double couponAmount, int remainingDraws) {
        return new LotteryResult(true, sectorIndex, title, couponAmount,
                remainingDraws, "");
    }

    public static LotteryResult failure(String message) {
        return new LotteryResult(false, -1, "", 0, 0, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getSectorIndex() {
        return sectorIndex;
    }

    public String getTitle() {
        return title;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public int getRemainingDraws() {
        return remainingDraws;
    }

    public String getMessage() {
        return message;
    }
}
