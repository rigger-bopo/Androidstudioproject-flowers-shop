package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityHomeBinding;
import com.example.xianhuashangdian.model.Campaign;
import com.example.xianhuashangdian.model.CampaignRepository;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.model.RecommendationResult;
import com.example.xianhuashangdian.ui.adapter.BannerAdapter;
import com.example.xianhuashangdian.ui.adapter.MerchantAdapter;
import com.example.xianhuashangdian.ui.adapter.ProductAdapter;
import com.example.xianhuashangdian.ui.adapter.RecommendationProductAdapter;
import com.example.xianhuashangdian.util.FlowerRecommendationEngine;
import com.example.xianhuashangdian.util.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements ProductAdapter.Listener, BannerAdapter.Listener {
    private static final long BANNER_DELAY_MS = 3200L;

    private ActivityHomeBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ProductAdapter productAdapter;
    private RecommendationProductAdapter recommendationProductAdapter;
    private MerchantAdapter merchantAdapter;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private String activeQuery = "";
    private int bannerPosition = 0;

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding == null || binding.bannerPager.getAdapter() == null) {
                return;
            }
            int count = binding.bannerPager.getAdapter().getItemCount();
            if (count > 0) {
                bannerPosition = (bannerPosition + 1) % count;
                binding.bannerPager.setCurrentItem(bannerPosition, true);
            }
            bannerHandler.postDelayed(this, BANNER_DELAY_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        binding.greetingText.setText(getString(
                R.string.welcome_user, sessionManager.getUsername()));

        setupBanner();
        setupRecommendations();
        setupCategories();
        setupProducts();
        setupActions();
        loadProducts("");
    }

    private void setupBanner() {
        List<Campaign> campaigns = CampaignRepository.getCampaigns();
        BannerAdapter adapter = new BannerAdapter(campaigns, this);
        binding.bannerPager.setAdapter(adapter);
        binding.bannerPager.registerOnPageChangeCallback(
                new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        bannerPosition = position;
                        updateBannerIndicator(position, campaigns.size());
                    }
                });
        updateBannerIndicator(0, campaigns.size());
    }

    private void updateBannerIndicator(int selected, int count) {
        binding.bannerIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (getResources().getDisplayMetrics().density * (i == selected ? 18 : 6)),
                    (int) (getResources().getDisplayMetrics().density * 6));
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == selected
                    ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            binding.bannerIndicator.addView(dot);
        }
    }

    private void setupCategories() {
        addCategoryChip(getString(R.string.flower_categories), "", true);
        addCategoryChip("玫瑰", "玫瑰", false);
        addCategoryChip("康乃馨", "康乃馨", false);
        addCategoryChip("菊花", "菊花", false);
        addCategoryChip("茉莉", "茉莉", false);
        binding.categoryGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            Chip chip = group.findViewById(checkedIds.get(0));
            if (chip != null) {
                activeQuery = chip.getTag() == null ? "" : chip.getTag().toString();
                loadProducts(activeQuery);
            }
        });
    }

    private void addCategoryChip(String label, String query, boolean selected) {
        Chip chip = new Chip(this);
        chip.setText(label);
        chip.setTag(query);
        chip.setCheckable(true);
        chip.setChecked(selected);
        chip.setChipBackgroundColor(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.rose_primary),
                        ContextCompat.getColor(this, R.color.surface)
                }));
        chip.setTextColor(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.white),
                        ContextCompat.getColor(this, R.color.ink)
                }));
        chip.setChipStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.line)));
        chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
        chip.setEnsureMinTouchTargetSize(false);
        binding.categoryGroup.addView(chip);
    }

    private void setupProducts() {
        productAdapter = new ProductAdapter(this);
        binding.productsRecycler.setAdapter(productAdapter);
    }

    private void setupRecommendations() {
        recommendationProductAdapter = new RecommendationProductAdapter(this::onProductClick);
        binding.recommendProductsRecycler.setAdapter(recommendationProductAdapter);
        merchantAdapter = new MerchantAdapter();
        binding.merchantsRecycler.setAdapter(merchantAdapter);

        addRecommendationChip(R.string.advice_chip_girlfriend, "想买花送给女朋友");
        addRecommendationChip(R.string.advice_chip_parents, "想买花送给父母");
        addRecommendationChip(R.string.advice_chip_teacher, "想买花送给老师");
        addRecommendationChip(R.string.advice_chip_birthday, "朋友生日想送花");
        addRecommendationChip(R.string.advice_chip_visit, "想买花看望慰问");

        binding.recommendButton.setOnClickListener(view -> runRecommendation());
        binding.recommendationInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                runRecommendation();
                return true;
            }
            return false;
        });
    }

    private void addRecommendationChip(int textRes, String query) {
        Chip chip = new Chip(this);
        chip.setText(textRes);
        chip.setCheckable(false);
        chip.setChipBackgroundColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.rose_soft)));
        chip.setTextColor(ContextCompat.getColor(this, R.color.rose_primary));
        chip.setChipStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.line)));
        chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setOnClickListener(view -> {
            binding.recommendationInput.setText(query);
            binding.recommendationInput.setSelection(query.length());
            runRecommendation();
        });
        binding.recommendationChips.addView(chip);
    }

    private void runRecommendation() {
        String input = binding.recommendationInput.getText() == null
                ? "" : binding.recommendationInput.getText().toString().trim();
        if (input.isEmpty()) {
            binding.recommendationInputLayout.setError("请输入送花对象或使用场景");
            return;
        }
        binding.recommendationInputLayout.setError(null);
        RecommendationResult result = FlowerRecommendationEngine.recommend(
                databaseHelper, input);
        binding.recommendationResult.setVisibility(View.VISIBLE);
        binding.recommendationTitle.setText(result.getTitle());
        binding.recommendationReason.setText(result.getReason());
        recommendationProductAdapter.submitList(result.getProducts());
        merchantAdapter.submitList(result.getMerchants());
        binding.recommendedProductsLabel.setVisibility(View.VISIBLE);
        binding.recommendProductsRecycler.setVisibility(View.VISIBLE);
        binding.recommendedMerchantsLabel.setVisibility(View.VISIBLE);
        boolean hasMerchants = !result.getMerchants().isEmpty();
        binding.merchantsRecycler.setVisibility(hasMerchants ? View.VISIBLE : View.GONE);
        binding.merchantsEmpty.setVisibility(hasMerchants ? View.GONE : View.VISIBLE);
    }

    private void setupActions() {
        binding.searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                String query = text == null ? "" : text.toString().trim();
                activeQuery = query;
                binding.categoryGroup.clearCheck();
                if (query.isEmpty()) {
                    chipAllChecked();
                }
                loadProducts(query);
            }

            @Override
            public void afterTextChanged(android.text.Editable text) {
            }
        });
        binding.searchInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        binding.ordersButton.setOnClickListener(view ->
                startActivity(new Intent(this, OrdersActivity.class)));
        binding.activityCard.setOnClickListener(view ->
                startActivity(new Intent(this, LotteryActivity.class)));
        binding.logoutCard.setOnClickListener(view -> logout());
        binding.cartFab.setOnClickListener(view ->
                startActivity(new Intent(this, CartActivity.class)));
    }

    private void performSearch() {
        String query = binding.searchInput.getText() == null
                ? "" : binding.searchInput.getText().toString().trim();
        activeQuery = query;
        loadProducts(query);
        hideKeyboardAndFocus();
        binding.homeScroll.post(() -> binding.homeScroll.smoothScrollTo(
                0, Math.max(0, binding.productsHeader.getTop())));
    }

    private void hideKeyboardAndFocus() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (inputMethodManager != null && focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(), 0);
        }
        binding.searchInput.clearFocus();
        binding.homeScroll.requestFocus();
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void chipAllChecked() {
        if (binding.categoryGroup.getChildCount() > 0) {
            ((Chip) binding.categoryGroup.getChildAt(0)).setChecked(true);
        }
    }

    private void loadProducts(String query) {
        List<Product> products = databaseHelper.searchProducts(query);
        productAdapter.submitList(products);
        boolean empty = products.isEmpty();
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.productsRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.resultCount.setText(getString(R.string.result_count_format, products.size()));
    }

    private void refreshCartFab() {
        int count = databaseHelper.getCartItemCount(sessionManager.getUserId());
        binding.cartFab.setText(count == 0
                ? getString(R.string.cart)
                : getString(R.string.cart_count, count));
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onBannerClick(Campaign campaign) {
        Intent intent = new Intent(this, CampaignDetailActivity.class);
        intent.putExtra(CampaignDetailActivity.EXTRA_CAMPAIGN_ID, campaign.getId());
        startActivity(intent);
    }

    @Override
    public void onQuickAdd(Product product) {
        long result = databaseHelper.addToCart(
                sessionManager.getUserId(), product.getId(), 1);
        if (result < 0) {
            Snackbar.make(binding.getRoot(), R.string.stock_not_enough, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.cartFab)
                    .show();
            return;
        }
        refreshCartFab();
        Snackbar.make(binding.getRoot(), product.getName() + " 已加入组合", Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.cartFab)
                .setAction("查看", view -> startActivity(new Intent(this, CartActivity.class)))
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartFab();
        if (!activeQuery.equals(binding.searchInput.getText() == null
                ? "" : binding.searchInput.getText().toString().trim())) {
            loadProducts(activeQuery);
        }
        bannerHandler.removeCallbacks(bannerRunnable);
        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
    }

    @Override
    protected void onPause() {
        bannerHandler.removeCallbacks(bannerRunnable);
        super.onPause();
    }
}
