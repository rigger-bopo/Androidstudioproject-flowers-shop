package com.example.xianhuashangdian.model;

import java.util.List;

public class RecommendationResult {
    private final String title;
    private final String reason;
    private final List<Product> products;
    private final List<Merchant> merchants;

    public RecommendationResult(String title, String reason,
                                List<Product> products, List<Merchant> merchants) {
        this.title = title;
        this.reason = reason;
        this.products = products;
        this.merchants = merchants;
    }

    public String getTitle() {
        return title;
    }

    public String getReason() {
        return reason;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }
}
