package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityCartBinding;
import com.example.xianhuashangdian.model.CartItem;
import com.example.xianhuashangdian.ui.adapter.CartAdapter;
import com.example.xianhuashangdian.util.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.Listener {
    private ActivityCartBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        cartAdapter = new CartAdapter(this);
        binding.cartRecycler.setAdapter(cartAdapter);

        binding.cartBack.setOnClickListener(view -> finish());
        binding.continueButton.setOnClickListener(view -> finish());
        binding.checkoutButton.setOnClickListener(view ->
                startActivity(new Intent(this, PaymentActivity.class)));
        loadCart();
    }

    private void loadCart() {
        List<CartItem> items = databaseHelper.getCartItems(sessionManager.getUserId());
        cartAdapter.submitList(items);
        boolean empty = items.isEmpty();
        binding.cartEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.cartRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.cartTotal.setText(String.format(Locale.CHINA, "¥%.2f",
                databaseHelper.getCartTotal(sessionManager.getUserId())));
        binding.checkoutButton.setEnabled(!empty);
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity < 1) {
            Snackbar.make(binding.getRoot(), R.string.quantity_min, Snackbar.LENGTH_SHORT).show();
            return;
        }
        boolean updated = databaseHelper.updateCartQuantity(
                sessionManager.getUserId(), item.getProduct().getId(), newQuantity);
        if (!updated) {
            Snackbar.make(binding.getRoot(), R.string.stock_not_enough, Snackbar.LENGTH_SHORT).show();
            return;
        }
        loadCart();
    }

    @Override
    public void onRemove(CartItem item) {
        databaseHelper.removeCartItem(
                sessionManager.getUserId(), item.getProduct().getId());
        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }
}
