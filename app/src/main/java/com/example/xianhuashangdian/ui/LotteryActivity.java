package com.example.xianhuashangdian.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityLotteryBinding;
import com.example.xianhuashangdian.databinding.ItemCouponBinding;
import com.example.xianhuashangdian.model.Coupon;
import com.example.xianhuashangdian.model.LotteryResult;
import com.example.xianhuashangdian.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LotteryActivity extends AppCompatActivity {
    private ActivityLotteryBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private boolean spinning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLotteryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        binding.lotteryBack.setOnClickListener(view -> finish());
        binding.spinButton.setOnClickListener(view -> startSpin());
        binding.wheelCenterButton.setOnClickListener(view -> startSpin());
        refreshState();
    }

    private void startSpin() {
        if (spinning) {
            return;
        }
        int remaining = DatabaseHelper.DAILY_DRAW_LIMIT
                - databaseHelper.getTodayDrawCount(sessionManager.getUserId());
        if (remaining <= 0) {
            Snackbar.make(binding.getRoot(), R.string.lottery_draws_used,
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        LotteryResult result = databaseHelper.drawLottery(sessionManager.getUserId());
        if (!result.isSuccess()) {
            Snackbar.make(binding.getRoot(), result.getMessage(),
                    Snackbar.LENGTH_SHORT).show();
            refreshState();
            return;
        }

        spinning = true;
        binding.spinButton.setEnabled(false);
        binding.wheelCenterButton.setEnabled(false);
        binding.spinButton.setText(R.string.lottery_spinning);
        binding.luckyWheel.spinToIndex(result.getSectorIndex(), sectorIndex ->
                showResult(result));
    }

    private void showResult(LotteryResult result) {
        spinning = false;
        binding.spinButton.setText(R.string.lottery_spin);
        refreshState();
        String message = result.getCouponAmount() > 0
                ? getString(R.string.lottery_congratulations, result.getTitle())
                : getString(R.string.lottery_thanks);
        new MaterialAlertDialogBuilder(this)
                .setTitle(result.getCouponAmount() > 0
                        ? R.string.lottery_title : R.string.lottery_thanks)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void refreshState() {
        int drawCount = databaseHelper.getTodayDrawCount(sessionManager.getUserId());
        int remaining = Math.max(0, DatabaseHelper.DAILY_DRAW_LIMIT - drawCount);
        binding.drawsLeft.setText(remaining > 0
                ? getString(R.string.lottery_draws_left, remaining)
                : getString(R.string.lottery_draws_used));
        binding.spinButton.setEnabled(remaining > 0 && !spinning);
        binding.wheelCenterButton.setEnabled(remaining > 0 && !spinning);
        binding.wheelCenterButton.setAlpha(remaining > 0 ? 1f : 0.45f);
        bindCoupons();
    }

    private void bindCoupons() {
        List<Coupon> coupons = databaseHelper.getUnusedCoupons(sessionManager.getUserId());
        binding.couponsContainer.removeAllViews();
        boolean empty = coupons.isEmpty();
        binding.couponsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (empty) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Coupon coupon : coupons) {
            ItemCouponBinding itemBinding = ItemCouponBinding.inflate(
                    inflater, binding.couponsContainer, false);
            itemBinding.couponAmount.setText(getString(
                    R.string.coupon_amount_format, coupon.getAmount()));
            itemBinding.couponTitle.setText(coupon.getTitle());
            itemBinding.couponCode.setText(getString(
                    R.string.coupon_code_format, coupon.getCode()));
            binding.couponsContainer.addView(itemBinding.getRoot());
        }
    }
}
