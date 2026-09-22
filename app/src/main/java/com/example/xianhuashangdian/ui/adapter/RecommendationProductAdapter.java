package com.example.xianhuashangdian.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.databinding.ItemRecommendationProductBinding;
import com.example.xianhuashangdian.model.Product;
import com.example.xianhuashangdian.util.ProductImageResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecommendationProductAdapter
        extends RecyclerView.Adapter<RecommendationProductAdapter.ProductViewHolder> {

    public interface Listener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final Listener listener;

    public RecommendationProductAdapter(Listener listener) {
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
        ItemRecommendationProductBinding binding = ItemRecommendationProductBinding.inflate(
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
        private final ItemRecommendationProductBinding binding;

        ProductViewHolder(ItemRecommendationProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.recommendProductImage.setImageResource(
                    ProductImageResolver.resolve(product.getImageKey()));
            binding.recommendProductName.setText(product.getName());
            binding.recommendProductPrice.setText(String.format(
                    Locale.CHINA, "¥%.2f/枝", product.getPrice()));
            binding.getRoot().setOnClickListener(view -> listener.onProductClick(product));
        }
    }
}
