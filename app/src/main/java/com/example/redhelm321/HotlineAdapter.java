package com.example.redhelm321;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.HotlineViewHolder> {
    private final List<HotlineItem> hotlineList;
    private final OnHotlineClickListener listener;

    public interface OnHotlineClickListener {
        void onHotlineClick(HotlineItem hotline);
        void onDeleteClick(HotlineItem hotline);
        void onDeleteConfirmed(HotlineItem hotline);
    }

    public HotlineAdapter(List<HotlineItem> hotlineList, OnHotlineClickListener listener) {
        this.hotlineList = hotlineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotline, parent, false);
        return new HotlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotlineViewHolder holder, int position) {
        HotlineItem hotline = hotlineList.get(position);
        holder.nameTextView.setText(hotline.getName());
        holder.numberTextView.setText(hotline.getNumber());
        holder.descriptionTextView.setText(hotline.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHotlineClick(hotline);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(hotline);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotlineList.size();
    }

    static class HotlineViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView numberTextView;
        TextView descriptionTextView;
        ImageButton deleteButton;

        public HotlineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
