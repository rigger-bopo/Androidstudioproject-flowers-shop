package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityCampaignDetailBinding;
import com.example.xianhuashangdian.model.Campaign;
import com.example.xianhuashangdian.model.CampaignRepository;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.ui.adapter.RecommendationProductAdapter;

public class CampaignDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CAMPAIGN_ID = "campaign_id";

    private ActivityCampaignDetailBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCampaignDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        String campaignId = getIntent().getStringExtra(EXTRA_CAMPAIGN_ID);
        Campaign campaign = CampaignRepository.findById(campaignId);
        if (campaign == null) {
            finish();
            return;
        }

        binding.campaignImage.setImageResource(campaign.getImageResId());
        binding.campaignTitle.setText(campaign.getTitleResId());
        binding.campaignSubtitle.setText(campaign.getSubtitleResId());
        binding.campaignDescription.setText(campaign.getDescriptionResId());
        RecommendationProductAdapter productAdapter =
                new RecommendationProductAdapter(this::openProduct);
        binding.campaignProductsRecycler.setAdapter(productAdapter);
        productAdapter.submitList(databaseHelper.getProductsByNames(campaign.getProductNames()));

        binding.campaignBack.setOnClickListener(view -> finish());
        binding.campaignCart.setOnClickListener(view ->
                startActivity(new Intent(this, CartActivity.class)));
    }

    private void openProduct(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
