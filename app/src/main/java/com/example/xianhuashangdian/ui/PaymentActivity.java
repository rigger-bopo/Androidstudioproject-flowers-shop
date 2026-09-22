package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityPaymentBinding;
import com.example.xianhuashangdian.databinding.DialogPaymentSuccessBinding;
import com.example.xianhuashangdian.model.CartItem;
import com.example.xianhuashangdian.model.Coupon;
import com.example.xianhuashangdian.model.OrderCreationResult;
import com.example.xianhuashangdian.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String selectedPaymentMethod;
    private Coupon selectedCoupon;
    private double cartSubtotal;
    private boolean paying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        selectedPaymentMethod = getString(R.string.wechat_pay);
        bindCartSummary();
        selectPayment(true);
        bindActions();
    }

    private void bindCartSummary() {
        List<CartItem> items = databaseHelper.getCartItems(sessionManager.getUserId());
        if (items.isEmpty()) {
            finish();
            return;
        }

        binding.paymentSummaryContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartItem item : items) {
            TextView line = (TextView) inflater.inflate(
                    android.R.layout.simple_list_item_1, binding.paymentSummaryContainer, false);
            line.setText(String.format(Locale.CHINA, "%s　×%d　¥%.2f",
                    item.getProduct().getName(), item.getQuantity(), item.getSubtotal()));
            line.setTextColor(getColor(R.color.ink));
            line.setTextSize(14);
            line.setPadding(0, 7, 0, 7);
            binding.paymentSummaryContainer.addView(line);
        }

        cartSubtotal = databaseHelper.getCartTotal(sessionManager.getUserId());
        bindCoupons();
        updateTotals();
    }

    private void bindActions() {
        binding.paymentBack.setOnClickListener(view -> finish());
        binding.wechatOption.setOnClickListener(view -> selectPayment(true));
        binding.wechatRadio.setOnClickListener(view -> selectPayment(true));
        binding.alipayOption.setOnClickListener(view -> selectPayment(false));
        binding.alipayRadio.setOnClickListener(view -> selectPayment(false));
        binding.payButton.setOnClickListener(view -> simulatePayment());
        binding.couponGotoButton.setOnClickListener(view ->
                startActivity(new Intent(this, LotteryActivity.class)));
    }

    private void bindCoupons() {
        List<Coupon> coupons = databaseHelper.getUnusedCoupons(sessionManager.getUserId());
        binding.paymentCouponGroup.removeAllViews();
        selectedCoupon = null;

        RadioButton noCoupon = createCouponOption(
                getString(R.string.payment_use_no_coupon), -1L);
        noCoupon.setChecked(true);
        binding.paymentCouponGroup.addView(noCoupon);

        boolean empty = coupons.isEmpty();
        binding.paymentCouponsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        for (Coupon coupon : coupons) {
            RadioButton option = createCouponOption(
                    getString(R.string.payment_coupon_option,
                            coupon.getTitle(), coupon.getAmount()),
                    coupon.getId());
            binding.paymentCouponGroup.addView(option);
        }

        binding.paymentCouponGroup.setOnCheckedChangeListener((group, checkedId) -> {
            View checked = group.findViewById(checkedId);
            long couponId = checked == null || !(checked.getTag() instanceof Long)
                    ? -1L : (Long) checked.getTag();
            selectedCoupon = null;
            if (couponId > 0) {
                for (Coupon coupon : coupons) {
                    if (coupon.getId() == couponId) {
                        selectedCoupon = coupon;
                        break;
                    }
                }
            }
            updateTotals();
        });
    }

    private RadioButton createCouponOption(String text, long couponId) {
        RadioButton button = new RadioButton(this);
        button.setText(text);
        button.setTag(couponId);
        button.setTextSize(14);
        button.setTextColor(getColor(R.color.ink));
        button.setButtonTintList(android.content.res.ColorStateList.valueOf(
                getColor(R.color.rose_primary)));
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(0, 4, 0, 4);
        return button;
    }

    private void updateTotals() {
        double discount = selectedCoupon == null
                ? 0 : Math.min(selectedCoupon.getAmount(), cartSubtotal);
        double payable = Math.max(0, cartSubtotal - discount);
        binding.paymentTotal.setText(String.format(Locale.CHINA, "¥%.2f", payable));
        binding.paymentBottomTotal.setText(String.format(Locale.CHINA, "¥%.2f", payable));
        binding.paymentDiscountRow.setVisibility(discount > 0 ? View.VISIBLE : View.GONE);
        binding.paymentDiscountAmount.setText(getString(
                R.string.payment_discount_format, discount));
    }

    private void selectPayment(boolean wechat) {
        binding.wechatRadio.setChecked(wechat);
        binding.alipayRadio.setChecked(!wechat);
        selectedPaymentMethod = getString(wechat ? R.string.wechat_pay : R.string.alipay);
        int activeStroke = getColor(R.color.rose_primary);
        int normalStroke = getColor(R.color.line);
        binding.wechatOption.setStrokeColor(wechat ? activeStroke : normalStroke);
        binding.alipayOption.setStrokeColor(wechat ? normalStroke : activeStroke);
    }

    private void simulatePayment() {
        if (paying) {
            return;
        }
        paying = true;
        binding.paymentProgress.setVisibility(View.VISIBLE);
        binding.payButton.setEnabled(false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            OrderCreationResult result = databaseHelper.createOrder(
                    sessionManager.getUserId(),
                    selectedPaymentMethod,
                    selectedCoupon == null ? -1L : selectedCoupon.getId());
            binding.paymentProgress.setVisibility(View.GONE);
            paying = false;
            if (result.isSuccess()) {
                showPaymentSuccess(result);
            } else {
                binding.payButton.setEnabled(true);
                Snackbar.make(binding.getRoot(), result.getMessage(), Snackbar.LENGTH_LONG).show();
                bindCartSummary();
            }
        }, 1300L);
    }

    private void showPaymentSuccess(OrderCreationResult result) {
        DialogPaymentSuccessBinding dialogBinding = DialogPaymentSuccessBinding.inflate(
                getLayoutInflater());
        dialogBinding.successOrderNo.setText(getString(
                R.string.success_order_no_format, result.getOrderNo()));

        new MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .setPositiveButton(R.string.view_order, (dialog, which) -> openOrders())
                .setNegativeButton(R.string.back_home, (dialog, which) -> openHome())
                .show();
    }

    private void openOrders() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
        startActivity(new Intent(this, OrdersActivity.class));
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!paying && sessionManager.getUserId() > 0) {
            bindCartSummary();
        }
    }
}
