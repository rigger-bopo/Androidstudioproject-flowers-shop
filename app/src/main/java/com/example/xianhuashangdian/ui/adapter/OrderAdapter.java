package com.example.xianhuashangdian.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.databinding.ItemOrderBinding;
import com.example.xianhuashangdian.model.OrderRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<OrderRecord> orders = new ArrayList<>();

    public void submitList(List<OrderRecord> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderRecord order) {
            binding.orderMethod.setText(order.getPaymentMethod());
            binding.orderStatus.setText(R.string.order_status_paid);
            binding.orderItems.setText(order.getItemsSummary());
            binding.orderNo.setText(binding.getRoot().getContext().getString(
                    R.string.order_no_format, order.getOrderNo()));
            binding.orderTime.setText(order.getCreatedAt());
            binding.orderTotal.setText(String.format(
                    Locale.CHINA, "¥%.2f", order.getTotalAmount()));
            if (order.getDiscountAmount() > 0) {
                binding.orderDiscount.setVisibility(android.view.View.VISIBLE);
                binding.orderDiscount.setText(binding.getRoot().getContext().getString(
                        R.string.order_discount_format, order.getDiscountAmount()));
            } else {
                binding.orderDiscount.setVisibility(android.view.View.GONE);
            }
        }
    }
}
