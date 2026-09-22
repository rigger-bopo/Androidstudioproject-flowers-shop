package com.example.xianhuashangdian.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.databinding.ItemCartBinding;
import com.example.xianhuashangdian.model.CartItem;
import com.example.xianhuashangdian.util.ProductImageResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface Listener {
        void onQuantityChanged(CartItem item, int newQuantity);

        void onRemove(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final Listener listener;

    public CartAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<CartItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item) {
            binding.cartName.setText(item.getProduct().getName());
            binding.cartUnitPrice.setText(String.format(
                    Locale.CHINA, "¥%.2f / 枝 · 库存 %d",
                    item.getProduct().getPrice(), item.getProduct().getStock()));
            binding.cartQuantity.setText(String.valueOf(item.getQuantity()));
            binding.cartSubtotal.setText(String.format(
                    Locale.CHINA, "¥%.2f", item.getSubtotal()));
            binding.cartImage.setImageResource(ProductImageResolver.resolve(
                    item.getProduct().getImageKey()));

            boolean canDecrease = item.getQuantity() > 1;
            boolean canIncrease = item.getQuantity() < item.getProduct().getStock();
            binding.cartDecrease.setEnabled(canDecrease);
            binding.cartDecrease.setAlpha(canDecrease ? 1f : 0.3f);
            binding.cartIncrease.setEnabled(canIncrease);
            binding.cartIncrease.setAlpha(canIncrease ? 1f : 0.3f);

            binding.cartDecrease.setOnClickListener(view ->
                    listener.onQuantityChanged(item, item.getQuantity() - 1));
            binding.cartIncrease.setOnClickListener(view ->
                    listener.onQuantityChanged(item, item.getQuantity() + 1));
            binding.removeButton.setOnClickListener(view -> listener.onRemove(item));
        }
    }
}
