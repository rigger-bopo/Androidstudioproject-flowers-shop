package com.example.xianhuashangdian.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.databinding.ItemProductBinding;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.util.ProductImageResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface Listener {
        void onProductClick(Product product);

        void onQuickAdd(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final Listener listener;

    public ProductAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            Context context = binding.getRoot().getContext();
            binding.productName.setText(product.getName());
            binding.productDescription.setText(product.getDescription());
            binding.productPrice.setText(String.format(
                    Locale.CHINA, "¥%.2f/枝", product.getPrice()));
            binding.productStock.setText(context.getString(
                    R.string.stock_format, product.getStock()));
            binding.productImage.setImageResource(ProductImageResolver.resolve(
                    product.getImageKey()));

            binding.getRoot().setOnClickListener(view -> listener.onProductClick(product));
            binding.quickAddButton.setOnClickListener(view -> listener.onQuickAdd(product));
        }
    }
}
