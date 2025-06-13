package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Inter.OnCompleteListener;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.CartItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private Context context;
    private List<CartItems> cartItems;
    private int[] itemQuantities;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference cartItemRef;
    private CartUpdateListener cartUpdateListener;
    private String resId;

    public CartAdapter(Context context,CartUpdateListener listener) {
        this.context = context;
        this.cartUpdateListener = listener;
    }
    public void setData(List<CartItems> cartItems,  String resId){
        this.cartItems = cartItems;
        this.resId = resId;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        cartItemRef = database.getReference().child("user").child(userId).child("CartItems").child(resId);

        CartItems item = cartItems.get(position);
        holder.txtName.setText(item.getFoodName());
        holder.txtPrice.setText(formatStringNumber(item.getFoodPrice())+" VND");
        holder.txtQuanity.setText(item.getFoodQuanity()+"");
        Glide.with(context).load(item.getFoodImage()).into(holder.foodImage);

        holder.imgMinus.setOnClickListener(view -> {
            if (item.getFoodQuanity() > 1) {
                int newQuantity = item.getFoodQuanity() - 1;
                updateQuantity(item, newQuantity, position);
                holder.txtQuanity.setText(String.valueOf(newQuantity));
            }
        });
        holder.imgplus.setOnClickListener(view -> {
            int newQuantity = item.getFoodQuanity() + 1;
            updateQuantity(item, newQuantity, position);
            holder.txtQuanity.setText(String.valueOf(newQuantity));
        });
        holder.imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                deleteQuanity(currentPosition);
            }
        });
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
    private void updateQuantity(CartItems item, int newQuantity, int position) {
        getUniqueKeyAtPosition(position, uniqueKey -> {
            if (uniqueKey != null) {
                cartItemRef.child(uniqueKey).child("foodQuanity")
                        .setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            item.setFoodQuanity(newQuantity);
                            notifyDataSetChanged();
                            if (cartUpdateListener != null) {
                                cartUpdateListener.onCartUpdatedLocally(cartItems);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi cập nhật Firebase!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void deleteQuanity(int position) {
        int positionRetrieve = position;
        getUniqueKeyAtPosition(positionRetrieve, uniqueKey -> {
            if (uniqueKey != null) {
                removeItem(position, uniqueKey);
            }
        });
    }

    private void removeItem(int position, String uniqueKey) {
        if (uniqueKey != null && cartItems != null) {
            if (position >= 0 && position < cartItems.size()) {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());

                cartItemRef.child(uniqueKey).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (cartItems.isEmpty()) {
                                // Nếu giỏ trống thì xóa luôn node cartItemRef (CartItems/resId)
                                cartItemRef.removeValue()
                                        .addOnSuccessListener(aVoid2 -> {
                                            if (cartUpdateListener != null) {
                                                cartUpdateListener.onCartUpdatedLocally(cartItems);
                                            }
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Failed to Delete Cart", Toast.LENGTH_SHORT).show()
                                        );
                            } else {
                                if (cartUpdateListener != null) {
                                    cartUpdateListener.onCartUpdatedLocally(cartItems);
                                }
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to Delete Item", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }

    private void getUniqueKeyAtPosition(int positionRetrieve, OnCompleteListener<String> onComplete) {
        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().equals("restaurantName")) {
                        keys.add(dataSnapshot.getKey()); // Lấy key của CartItem
                    }
                }

                if (positionRetrieve >= 0 && positionRetrieve < keys.size()) {
                    onComplete.onComplete(keys.get(positionRetrieve)); // Lấy đúng key
                } else {
                    onComplete.onComplete(null);
                } // Trả kết quả qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onComplete.onComplete(null);
            }
        });

    }
    @Override
    public int getItemCount() {
        if (cartItems != null){
            return cartItems.size();
        }
        return 0;
    }

    public interface CartUpdateListener {
        void onCartUpdatedLocally(List<CartItems> updatedList);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtPrice, txtQuanity;
        private ImageView foodImage;
        private ImageButton imgMinus, imgplus, imgTrash;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuanity = itemView.findViewById(R.id.txtQuanity);
            foodImage = itemView.findViewById(R.id.foodImage);
            imgMinus = itemView.findViewById(R.id.imgMinus);
            imgplus = itemView.findViewById(R.id.imgplus);
            imgTrash = itemView.findViewById(R.id.imgTrash);
        }
    }
}
