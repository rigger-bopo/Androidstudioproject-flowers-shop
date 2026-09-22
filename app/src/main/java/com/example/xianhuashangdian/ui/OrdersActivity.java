package com.example.xianhuashangdian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.data.DatabaseHelper;
import com.example.xianhuashangdian.databinding.ActivityOrdersBinding;
import com.example.xianhuashangdian.model.OrderRecord;
import com.example.xianhuashangdian.ui.adapter.OrderAdapter;
import com.example.xianhuashangdian.util.SessionManager;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    private ActivityOrdersBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        orderAdapter = new OrderAdapter();
        binding.ordersRecycler.setAdapter(orderAdapter);

        binding.ordersBack.setOnClickListener(view -> finish());
        binding.ordersContinue.setOnClickListener(view -> openHome());
        binding.logoutButton.setOnClickListener(view -> logout());
        loadOrders();
    }

    private void loadOrders() {
        List<OrderRecord> orders = databaseHelper.getOrders(sessionManager.getUserId());
        orderAdapter.submitList(orders);
        boolean empty = orders.isEmpty();
        binding.ordersEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.ordersRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
