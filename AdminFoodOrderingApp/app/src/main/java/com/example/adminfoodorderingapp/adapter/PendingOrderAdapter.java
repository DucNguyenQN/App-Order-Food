package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.databinding.PendingOrderItemBinding;
import com.example.adminfoodorderingapp.inter.OnItemClickListener;
import com.example.adminfoodorderingapp.model.OrderDetails;

import java.util.List;

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
            binding.txtPrice.setText("$"+quantity);
            Glide.with(context).load(foodImage.get(position)).into(binding.foodImage);
             if (!isAccept){
                 binding.accept.setText("Accept");
             }else {
                 binding.accept.setText("Dispatch");
             }
             binding.accept.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if (!isAccept){
                         binding.accept.setText("Dispatch");
                         isAccept = true;
                         listener.onItemAcceptClick(position);
                     }else {
                         binding.accept.setText("Dispatch");
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
}
