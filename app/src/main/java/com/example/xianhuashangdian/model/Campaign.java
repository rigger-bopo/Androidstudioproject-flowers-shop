package com.example.xianhuashangdian.model;

import java.util.List;

public class Campaign {
    private final String id;
    private final int imageResId;
    private final int titleResId;
    private final int subtitleResId;
    private final int descriptionResId;
    private final int accentColor;
    private final List<String> productNames;

    public Campaign(String id, int imageResId, int titleResId, int subtitleResId,
                    int descriptionResId, int accentColor, List<String> productNames) {
        this.id = id;
        this.imageResId = imageResId;
        this.titleResId = titleResId;
        this.subtitleResId = subtitleResId;
        this.descriptionResId = descriptionResId;
        this.accentColor = accentColor;
        this.productNames = productNames;
    }

    public String getId() {
        return id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getSubtitleResId() {
        return subtitleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public List<String> getProductNames() {
        return productNames;
    }
}
