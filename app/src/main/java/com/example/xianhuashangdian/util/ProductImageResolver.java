package com.example.xianhuashangdian.util;

import com.example.xianhuashangdian.R;

public final class ProductImageResolver {
    private ProductImageResolver() {
    }

    public static int resolve(String imageKey) {
        if ("red_rose".equals(imageKey)) {
            return R.drawable.product_red_rose;
        }
        if ("purple_rose".equals(imageKey)) {
            return R.drawable.product_purple_rose;
        }
        if ("carnation".equals(imageKey)) {
            return R.drawable.product_carnation;
        }
        if ("chrysanthemum".equals(imageKey)) {
            return R.drawable.product_chrysanthemum;
        }
        if ("jasmine".equals(imageKey)) {
            return R.drawable.product_jasmine;
        }
        return R.drawable.ic_flower;
    }
}
