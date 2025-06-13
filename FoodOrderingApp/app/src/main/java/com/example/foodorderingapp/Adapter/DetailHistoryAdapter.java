package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.DetailHistoryOrderActivity;
import com.example.foodorderingapp.RatingActivity;
import com.example.foodorderingapp.databinding.DetailHistoryOrderItemBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailHistoryAdapter extends  RecyclerView.Adapter<DetailHistoryAdapter.DetailHistoryViewHolder>{
    private Context context;
    private List<OrderDetails> lstOrderDetail;
    private String nameOfRes,resId,userId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DetailHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DetailHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DetailHistoryOrderItemBinding binding = DetailHistoryOrderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DetailHistoryViewHolder(binding);
    }
    public void setData(List<OrderDetails> lstOrderDetail){
        this.lstOrderDetail = lstOrderDetail;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull DetailHistoryViewHolder holder, int position) {
        OrderDetails orderDetails = lstOrderDetail.get(position);
        holder.bind(orderDetails);
    }

    @Override
    public int getItemCount() {
        if (lstOrderDetail != null){
            return lstOrderDetail.size();
        }
        return 0;
    }
    private void getNameRestaurance(String res, final DetailHistoryViewHolder holder){
        DatabaseReference resRef = FirebaseDatabase.getInstance().getReference().child("user").child(res).child("namofresturant");
        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameOfRes = snapshot.getValue(String.class);
                    holder.binding.nameRestaurance.setText(nameOfRes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };
    public class DetailHistoryViewHolder extends RecyclerView.ViewHolder {
        private final DetailHistoryOrderItemBinding binding;
        public DetailHistoryViewHolder(DetailHistoryOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderDetails orderDetails) {
            userId = orderDetails.getUserId();
            resId = orderDetails.getResId();
            binding.btnReceived.setVisibility(View.INVISIBLE);
            binding.btnrating.setVisibility(View.INVISIBLE);
            checkIsRating(orderDetails, this);
            CartItems cartItems = orderDetails.getCartItems().get(0);
            Boolean isReceived = orderDetails.getPaymentReceived();
            Boolean isAccepted = orderDetails.getOrderAcceppted();
            String itemPushKey = orderDetails.getItemPushKey();
            //Toast.makeText(context, itemPushKey, Toast.LENGTH_SHORT).show();
            getNameRestaurance(orderDetails.getResId(), this);
            Glide.with(context).load(cartItems.getFoodImage()).into(binding.menuImage);
            binding.timeOrder.setText(convertTimestamp(orderDetails.getCurrentTime()));
            binding.txtMenuFoodName.setText(cartItems.getFoodName());
            binding.txtMenuPrice.setText(formatStringNumber(orderDetails.getTotalPrice())+" VND");
            if (isAccepted) {
                if (isReceived == false) {
                    binding.btnReceived.setVisibility(View.VISIBLE);
                    binding.btnReceived.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            binding.btnReceived.setVisibility(View.INVISIBLE);
                            updateOrderStatus(itemPushKey);
                        }
                    });
                }
            }
            binding.itemHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailHistoryOrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("itemDetail", orderDetails);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            binding.btnrating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RatingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("itemDetail", orderDetails);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void checkIsRating(OrderDetails orderDetails,  DetailHistoryViewHolder holder) {
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("OrderRatings");
        ratingRef.child(orderDetails.getItemPushKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.binding.btnrating.setVisibility(View.INVISIBLE); // Đã đánh giá
                } else {
                    holder.binding.btnrating.setVisibility(View.VISIBLE); // Chưa đánh giá
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void updateOrderStatus(String itemPushKey) {
        Toast.makeText(context, "itemPushKey: " + itemPushKey, Toast.LENGTH_SHORT).show();
        DatabaseReference completeOrderRef = database.getReference().child("CompleteOrder").child(resId).child(itemPushKey);
        completeOrderRef.child("paymentReceived").setValue(true);

        DatabaseReference historyOrderRef = database.getReference().child("user").child(userId).child("BuyHistory").child(resId).child(itemPushKey);
        historyOrderRef.child("paymentReceived").setValue(true);

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
    public static String convertTimestamp(long firebaseTimestamp) {
        Date date = new Date(firebaseTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

}
