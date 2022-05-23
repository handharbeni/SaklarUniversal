package com.mhandharbeni.saklaruniversal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mhandharbeni.saklaruniversal.FullscreenActivity;
import com.mhandharbeni.saklaruniversal.databinding.DetailDataItemBinding;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    Context context;
    List<FullscreenActivity.DataDetailScreen> listDetail;
    DetailCallback detailCallback;

    public DetailAdapter(Context context, List<FullscreenActivity.DataDetailScreen> listDetail, DetailCallback detailCallback) {
        this.context = context;
        this.listDetail = listDetail;
        this.detailCallback = detailCallback;
    }

    @NonNull
    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DetailDataItemBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.ViewHolder holder, int position) {
        FullscreenActivity.DataDetailScreen data = listDetail.get(position);
        holder.binding.btnAction.setText(data.getTextButton());
        holder.binding.textAction.setText(data.getAddressButton());
        holder.binding.btnAction.setOnClickListener(v -> detailCallback.onDetailClick(data));
    }

    @Override
    public int getItemCount() {
        return listDetail.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DetailDataItemBinding binding;
        public ViewHolder(@NonNull DetailDataItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public interface DetailCallback{
        void onDetailClick(FullscreenActivity.DataDetailScreen dataDetailScreen);
    }
}
