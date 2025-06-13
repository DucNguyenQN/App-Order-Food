package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.DetailsActivity;
import com.example.foodorderingapp.databinding.ItemResBinding;
import com.example.foodorderingapp.model.MenuItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResAdapter extends RecyclerView.Adapter<ResAdapter.ResViewHolder> {
    private List<MenuItem> menuItems;
    private Context context;

    public ResAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<com.example.foodorderingapp.model.MenuItem> menuItems){
        this.menuItems = new ArrayList<>(menuItems);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResBinding binding = ItemResBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.binding(menuItem);

    }

    @Override
    public int getItemCount() {
        if (menuItems != null) {
            return menuItems.size();
        }
        return 0;
    }

    public class ResViewHolder extends RecyclerView.ViewHolder {
        private final ItemResBinding binding;
        public ResViewHolder(ItemResBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(MenuItem menuItem) {
            binding.txtTensp.setText(menuItem.getFoodName());
            binding.txtGiasp.setText(formatStringNumber(menuItem.getFoodPrice()) +" VND");
            Glide.with(context).load(menuItem.getFoodImage()).into(binding.imgSanpham);
            binding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("menuitem", menuItem);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
    private String formatStringNumber(String numberString) {
        try {
            String cleanedString = numberString.replaceAll("[^\\d]", "");
            int number = Integer.parseInt(cleanedString);
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            return formatter.format(number);
        } catch (NumberFormatException e) {
            Log.e("Format Error", "Không thể chuyển đổi chuỗi thành số: " + numberString);
            return numberString; // Trả về chuỗi gốc nếu có lỗi
        }
    }
}
