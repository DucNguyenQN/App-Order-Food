package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminfoodorderingapp.databinding.DeliveryItemBinding;
import com.example.adminfoodorderingapp.model.AllMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>{
    private List<String> customerNames;
    private List<Boolean> moneyStatus;
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

    public void setData(List<String> customerNames, List<Boolean> moneyStatus){
        this.customerNames = customerNames;
        this.moneyStatus = moneyStatus;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (customerNames != null){
            return customerNames.size();
        }
        return 0;
    }

    public class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private final DeliveryItemBinding binding;
        public DeliveryViewHolder(DeliveryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.customerName.setText(customerNames.get(position));
            if (moneyStatus.get(position) == true){
                binding.moneyStatus.setText("Received");
            }else{
                binding.moneyStatus.setText("Not Received");
            }

            Map<Boolean, Integer> colorMap = new HashMap<>();
            colorMap.put(true, Color.GREEN);
            colorMap.put(false, Color.RED);

//          binding.moneyStatus.setTextColor(colorMap.get(moneyStatus.get(position)));
            binding.moneyStatus.setTextColor(colorMap.getOrDefault(moneyStatus.get(position), Color.BLACK));
            binding.statusColor.setBackgroundTintList(ColorStateList.valueOf(colorMap.getOrDefault(moneyStatus.get(position), Color.BLACK)));
        }
    }
}
