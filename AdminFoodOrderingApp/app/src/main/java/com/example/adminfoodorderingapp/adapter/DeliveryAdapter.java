package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminfoodorderingapp.DetailsDeliveryActivity;
import com.example.adminfoodorderingapp.databinding.DeliveryItemBinding;
import com.example.adminfoodorderingapp.model.AllMenu;
import com.example.adminfoodorderingapp.model.OrderDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>{
    List<OrderDetails> orderDetailsList;
    private Context context;
    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeliveryItemBinding binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DeliveryViewHolder(binding);
    }

    public DeliveryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<OrderDetails> orderDetailsList){
        this.orderDetailsList = orderDetailsList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        OrderDetails orderDetails = orderDetailsList.get(position);
        holder.bind(orderDetails);
    }

    @Override
    public int getItemCount() {
        if (orderDetailsList != null){
            return orderDetailsList.size();
        }
        return 0;
    }

    public class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private final DeliveryItemBinding binding;
        public DeliveryViewHolder(DeliveryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderDetails orderDetails) {
            binding.customerName.setText(orderDetails.getUserName());
            if (orderDetails.getPaymentReceived() == true){
                binding.moneyStatus.setText("Đã nhận");
            }else{
                binding.moneyStatus.setText("Chưa nhận");
            }

            Map<Boolean, Integer> colorMap = new HashMap<>();
            colorMap.put(true, Color.GREEN);
            colorMap.put(false, Color.RED);

//          binding.moneyStatus.setTextColor(colorMap.get(moneyStatus.get(position)));
            binding.moneyStatus.setTextColor(colorMap.getOrDefault(orderDetails.getPaymentReceived(), Color.BLACK));
            binding.statusColor.setBackgroundTintList(ColorStateList.valueOf(colorMap.getOrDefault(orderDetails.getPaymentReceived(), Color.BLACK)));
            binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsDeliveryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("itemDetail", orderDetails);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
}
