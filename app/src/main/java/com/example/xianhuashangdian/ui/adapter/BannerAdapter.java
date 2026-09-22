package com.example.xianhuashangdian.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xianhuashangdian.databinding.ItemBannerBinding;
import com.example.xianhuashangdian.model.Campaign;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    public interface Listener {
        void onBannerClick(Campaign campaign);
    }

    private final List<Campaign> campaigns;
    private final Listener listener;

    public BannerAdapter(List<Campaign> campaigns, Listener listener) {
        this.campaigns = campaigns;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(campaigns.get(position));
    }

    @Override
    public int getItemCount() {
        return campaigns.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemBannerBinding binding;

        BannerViewHolder(ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Campaign campaign) {
            binding.bannerImage.setImageResource(campaign.getImageResId());
            binding.bannerTitle.setText(campaign.getTitleResId());
            binding.bannerSubtitle.setText(campaign.getSubtitleResId());
            binding.getRoot().setOnClickListener(view -> listener.onBannerClick(campaign));
        }
    }
}
