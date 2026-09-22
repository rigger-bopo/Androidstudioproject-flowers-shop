package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityProductDetailBinding;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.util.ProductImageResolver;
import com.example.xianhuashangdian.util.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT_ID = "product_id";

    private ActivityProductDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Product product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        long productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, -1L);
        product = databaseHelper.getProduct(productId);
        if (product == null) {
            finish();
            return;
        }

        bindProduct();
        bindActions();
    }

    private void bindProduct() {
        binding.detailName.setText(product.getName());
        binding.detailPrice.setText(String.format(
                Locale.CHINA, "¥%.2f / 枝", product.getPrice()));
        binding.detailLanguage.setText(product.getFlowerLanguage());
        binding.detailDescription.setText(product.getDescription());
        binding.detailImage.setImageResource(ProductImageResolver.resolve(
                product.getImageKey()));
        binding.detailStockBadge.setText(getString(
                R.string.stock_format, product.getStock()));
        refreshQuantity();
    }

    private void bindActions() {
        binding.backButton.setOnClickListener(view -> finish());
        binding.cartButton.setOnClickListener(view ->
                startActivity(new Intent(this, CartActivity.class)));
        binding.decreaseButton.setOnClickListener(view -> {
            if (quantity > 1) {
                quantity--;
                refreshQuantity();
            }
        });
        binding.increaseButton.setOnClickListener(view -> {
            if (quantity < product.getStock()) {
                quantity++;
                refreshQuantity();
            } else {
                Snackbar.make(binding.getRoot(), R.string.stock_not_enough, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        binding.addToCartButton.setOnClickListener(view -> {
            if (addCurrentSelectionToCart()) {
                Snackbar.make(binding.getRoot(), "已加入购物组合", Snackbar.LENGTH_SHORT)
                        .setAction("查看", clicked ->
                                startActivity(new Intent(this, CartActivity.class)))
                        .show();
                refreshCartButton();
            }
        });
        binding.buyNowButton.setOnClickListener(view -> {
            if (addCurrentSelectionToCart()) {
                startActivity(new Intent(this, CartActivity.class));
            }
        });
    }

    private boolean addCurrentSelectionToCart() {
        long result = databaseHelper.addToCart(
                sessionManager.getUserId(), product.getId(), quantity);
        if (result < 0) {
            Snackbar.make(binding.getRoot(), R.string.stock_not_enough, Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void refreshQuantity() {
        binding.quantityText.setText(String.valueOf(quantity));
        binding.decreaseButton.setEnabled(quantity > 1);
        binding.increaseButton.setEnabled(quantity < product.getStock());
        binding.decreaseButton.setAlpha(quantity > 1 ? 1f : 0.35f);
        binding.increaseButton.setAlpha(quantity < product.getStock() ? 1f : 0.35f);
        boolean available = product.getStock() > 0;
        binding.addToCartButton.setEnabled(available);
        binding.buyNowButton.setEnabled(available);
    }

    private void refreshCartButton() {
        int count = databaseHelper.getCartItemCount(sessionManager.getUserId());
        binding.cartButton.setText(count == 0
                ? getString(R.string.cart)
                : getString(R.string.cart_count, count));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (product != null) {
            product = databaseHelper.getProduct(product.getId());
            if (product == null) {
                finish();
                return;
            }
            if (quantity > product.getStock()) {
                quantity = Math.max(product.getStock(), 1);
            }
            bindProduct();
            refreshCartButton();
        }
    }
}
