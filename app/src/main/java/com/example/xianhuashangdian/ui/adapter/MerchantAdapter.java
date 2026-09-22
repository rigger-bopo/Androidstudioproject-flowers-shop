package com.example.xianhuashangdian.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.R;
import com.example.xianhuashangdian.databinding.ItemMerchantBinding;
import com.example.xianhuashangdian.model.Merchant;

import java.util.ArrayList;
import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {
    private final List<Merchant> merchants = new ArrayList<>();

    public void submitList(List<Merchant> newMerchants) {
        merchants.clear();
        merchants.addAll(newMerchants);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMerchantBinding binding = ItemMerchantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MerchantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        holder.bind(merchants.get(position));
    }

    @Override
    public int getItemCount() {
        return merchants.size();
    }

    static class MerchantViewHolder extends RecyclerView.ViewHolder {
        private final ItemMerchantBinding binding;

        MerchantViewHolder(ItemMerchantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Merchant merchant) {
            binding.merchantBadge.setText(merchant.getBadge());
            binding.merchantRating.setText(binding.getRoot().getContext().getString(
                    R.string.merchant_rating_format, merchant.getRating()));
            binding.merchantName.setText(merchant.getName());
            binding.merchantSpecialty.setText(binding.getRoot().getContext().getString(
                    R.string.merchant_specialty_format, merchant.getSpecialty()));
            binding.merchantPrice.setText(binding.getRoot().getContext().getString(
                    R.string.merchant_min_price_format, merchant.getMinPrice()));
        }
    }
}
