package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.databinding.OrderDetailsItemBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {
    private Context context;
    private List<String> foodName, foodquantity,foodImage, foodPrice;
    public OrderDetailsAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<String> foodName,List<String> foodquantity,List<String> foodImage,List<String> foodPrice){
        this.foodName = foodName;
        this.foodquantity = foodquantity;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderDetailsItemBinding binding = OrderDetailsItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new OrderDetailsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (foodName != null){
            return foodName.size();
        }
        return 0;
    }

    public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        private final OrderDetailsItemBinding binding;
        public OrderDetailsViewHolder(OrderDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.FoodName.setText(foodName.get(position));
            binding.FoodPrice.setText(formatStringNumber(foodPrice.get(position)) + " VND");
            binding.FoodQuantity.setText(foodquantity.get(position));
            Glide.with(context).load(foodImage.get(position)).into(binding.foodImage);
        }
    }
    private String formatStringNumber(String numberString) {
        try {
            double number = Double.parseDouble(numberString);
            int roundedNumber = (int) Math.round(number); // Làm tròn và ép int
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            return formatter.format(roundedNumber);
        } catch (NumberFormatException e) {
            Log.e("Format Error", "Không thể chuyển đổi chuỗi thành số: " + numberString);
            return numberString;
        }
    }
}
