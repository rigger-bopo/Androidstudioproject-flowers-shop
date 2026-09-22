package com.example.xianhuashangdian.model;

import android.graphics.Color;

import com.example.xianhuashangdian.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CampaignRepository {
    private static final List<Campaign> CAMPAIGNS = Arrays.asList(
            new Campaign(
                    "floral_store",
                    R.drawable.campaign_floral_store,
                    R.string.campaign_store_title,
                    R.string.campaign_store_subtitle,
                    R.string.campaign_store_description,
                    Color.rgb(192, 66, 104),
                    Arrays.asList("红玫瑰", "紫玫瑰", "茉莉花")),
            new Campaign(
                    "love_520",
                    R.drawable.campaign_520,
                    R.string.campaign_520_title,
                    R.string.campaign_520_subtitle,
                    R.string.campaign_520_description,
                    Color.rgb(215, 45, 101),
                    Arrays.asList("紫玫瑰", "红玫瑰")),
            new Campaign(
                    "teacher_day",
                    R.drawable.campaign_teacher,
                    R.string.campaign_teacher_title,
                    R.string.campaign_teacher_subtitle,
                    R.string.campaign_teacher_description,
                    Color.rgb(53, 61, 70),
                    Arrays.asList("菊花", "康乃馨", "茉莉花")));

    private CampaignRepository() {
    }

    public static List<Campaign> getCampaigns() {
        return Collections.unmodifiableList(CAMPAIGNS);
    }

    public static Campaign findById(String id) {
        for (Campaign campaign : CAMPAIGNS) {
            if (campaign.getId().equals(id)) {
                return campaign;
            }
        }
        return null;
    }
}
