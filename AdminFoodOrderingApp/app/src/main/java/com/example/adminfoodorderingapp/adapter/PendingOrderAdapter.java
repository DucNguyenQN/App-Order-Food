package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.databinding.PendingOrderItemBinding;
import com.example.adminfoodorderingapp.inter.OnItemClickListener;
import com.example.adminfoodorderingapp.model.Message;
import com.example.adminfoodorderingapp.model.Notification;
import com.example.adminfoodorderingapp.model.OrderDetails;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import kotlin.ranges.OpenEndRange;

public class PendingOrderAdapter  extends RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>{
    private Context context;
    private List<String> customerName, quantity;
    private List<String>  foodImage;
    private OnItemClickListener listener;
    public PendingOrderAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    public void setData(List<String> customerName,List<String> quantity,List<String>  foodImage){
        this.customerName = customerName;
        this.quantity = quantity;
        this.foodImage = foodImage;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PendingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PendingOrderItemBinding binding = PendingOrderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PendingOrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (customerName != null){
            return customerName.size();
        }
        return 0;
    }

    public class PendingOrderViewHolder extends RecyclerView.ViewHolder {
        private final PendingOrderItemBinding binding;
        private boolean isAccept = false;
        public PendingOrderViewHolder(PendingOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.customerName.setText(customerName.get(position));
            binding.txtPrice.setText(formatStringNumber(quantity.get(position)) + " VND");
            Glide.with(context).load(foodImage.get(position)).into(binding.foodImage);
             if (!isAccept){
                 binding.accept.setText("Chấp nhận");
             }else {
                 binding.accept.setText("Giao hàng");
             }
             binding.accept.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if (!isAccept){
                         binding.accept.setText("Giao hàng");
                         isAccept = true;
                         listener.onItemAcceptClick(position);
                     }else {
                         binding.accept.setText("Giao hàng");
                         customerName.remove(getAdapterPosition());
                         notifyItemRemoved(getAdapterPosition());
                         listener.onItemDispatchClick(position);
                     }
                 }
             });
             binding.cardView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     listener.onItemClick(getAdapterPosition());
                 }
             });
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
