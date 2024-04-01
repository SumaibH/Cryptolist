package com.tradetrack.cryptolist.rvAdapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataBase.DefinedColors;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final List<DefinedColors.ColorItem> colorList;
    private final OnColorItemClickListener listener;

    public interface OnColorItemClickListener {
        void onColorItemClick(String colorCode);
    }

    public ColorAdapter(List<DefinedColors.ColorItem> colorList, OnColorItemClickListener listener) {
        this.colorList = colorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        DefinedColors.ColorItem colorItem = colorList.get(position);
        holder.bind(colorItem);
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout colorLayout;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorLayout = itemView.findViewById(R.id.colorLayout);

            colorLayout.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onColorItemClick(colorList.get(position).code);
                }
            });
        }

        public void bind(DefinedColors.ColorItem colorItem) {
            int color = Color.parseColor(colorItem.code);
            colorLayout.setBackgroundColor(color);
        }
    }
}
