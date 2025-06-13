package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.databinding.ItemDeliveryBinding;
import com.example.adminfoodorderingapp.model.CartItems;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailsDeliveryAdapter extends RecyclerView.Adapter<DetailsDeliveryAdapter.DetailsDeliveryViewHolder>{
    private Context context;
    private List<CartItems> cartItems;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String resId, nameRes;

    @NonNull
    @Override
    public DetailsDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeliveryBinding binding = ItemDeliveryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DetailsDeliveryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsDeliveryViewHolder holder, int position) {
        CartItems cartItem = cartItems.get(position);
        holder.bind(cartItem, resId, nameRes);
    }

    public void setData(List<CartItems> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public DetailsDeliveryAdapter(Context context) {
        this.context = context;
    }
    

    @Override
    public int getItemCount() {
        if (cartItems != null){
            return cartItems.size();
        }
        return 0;
    }

    public class DetailsDeliveryViewHolder extends RecyclerView.ViewHolder {
        private final ItemDeliveryBinding binding;
        public DetailsDeliveryViewHolder(ItemDeliveryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItems cartItem, String resId, String nameRes) {
            binding.txtName.setText(cartItem.getFoodName());
            binding.txtPrice.setText(formatStringNumber(cartItem.getFoodPrice()) +" VND");
            Glide.with(context).load(cartItem.getFoodImage()).into(binding.foodImage);
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
