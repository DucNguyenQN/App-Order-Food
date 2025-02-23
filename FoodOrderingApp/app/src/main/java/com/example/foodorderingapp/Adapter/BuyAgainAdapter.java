package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.databinding.BuyAgainItemBinding;
import com.example.foodorderingapp.model.CartItems;

import java.util.List;

public class BuyAgainAdapter extends RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>{
    private Context context;
    private List<CartItems> cartItems;

    @NonNull
    @Override
    public BuyAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BuyAgainItemBinding binding = BuyAgainItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BuyAgainViewHolder(binding);
    }

    public BuyAgainAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CartItems> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull BuyAgainViewHolder holder, int position) {
        CartItems cartItems1 = cartItems.get(position);
        holder.bind(cartItems1);
    }

    @Override
    public int getItemCount() {
        if (cartItems != null){
            return cartItems.size();
        }
        return 0;
    }

    public class BuyAgainViewHolder extends RecyclerView.ViewHolder {
        private final BuyAgainItemBinding binding;
        public BuyAgainViewHolder(BuyAgainItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItems cartItems1) {
            binding.foodname.setText(cartItems1.getFoodName());
            binding.foodprice.setText("$"+cartItems1.getFoodPrice());
            Glide.with(context).load(cartItems1.getFoodImage()).into(binding.foodImage);
        }
    }
}
