package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemResBinding;
import com.example.foodorderingapp.databinding.PayItemBinding;
import com.example.foodorderingapp.model.CartItems;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayoutAdapter extends  RecyclerView.Adapter<PayoutAdapter.PayoutViewHolder> {
    private Context context;
    private List<CartItems> cartItems;

    public PayoutAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CartItems> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PayItemBinding binding = PayItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PayoutViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PayoutViewHolder holder, int position) {
        CartItems cartItem = cartItems.get(position);
       holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        if (cartItems != null) {
            return cartItems.size();
        }
        return 0;
    }

    public class PayoutViewHolder extends RecyclerView.ViewHolder {
        private final PayItemBinding binding;

        public PayoutViewHolder(PayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItems cartItem) {
            Glide.with(context).load(cartItem.getFoodImage()).into(binding.foodImage);
            binding.txtName.setText(cartItem.getFoodName());
            binding.txtPrice.setText(formatStringNumber(cartItem.getFoodPrice())+ " VND");
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
