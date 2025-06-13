package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.databinding.RatingItemBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.RatingData;
import com.example.foodorderingapp.model.RatingResult;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private Context context;
    private List<CartItems> cartItems;
    private List<RatingData> ratingDataList = new ArrayList<>();

    public RatingAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<CartItems> cartItems){
        this.cartItems = cartItems;
        ratingDataList.clear();
        for (int i = 0; i < cartItems.size(); i++) {
            ratingDataList.add(new RatingData(cartItems.get(i).getId(),0, ""));
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RatingItemBinding binding = RatingItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        CartItems cartItems1 = cartItems.get(position);
        holder.bind(cartItems1, position);
    }

    @Override
    public int getItemCount() {
        if (cartItems != null){
            return cartItems.size();
        }
        return 0;
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {
        private final RatingItemBinding binding;

        public RatingViewHolder(@NonNull RatingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItems cartItems1, int position) {
            Glide.with(context).load(cartItems1.getFoodImage()).into(binding.foodImage);
            binding.foodname.setText(cartItems1.getFoodName());
            binding.foodprice.setText(formatStringNumber(cartItems1.getFoodPrice()) + " VND");

            binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                ratingDataList.get(position).setRatingValue(rating);
            });

            // Lắng nghe thay đổi comment
            binding.edtComment.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    ratingDataList.get(position).setComment(s.toString());
                }
            });


        }
    }
    public List<RatingData> getRatingDataList() {
        return ratingDataList;
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
