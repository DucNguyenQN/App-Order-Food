package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;
import java.util.stream.IntStream;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private Context context;
    private List<CartItems> cartItems;
    private int[] itemQuantities;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference cartItemRef;
    private CartUpdateListener cartUpdateListener;

    public CartAdapter(Context context,CartUpdateListener listener) {
        this.context = context;
        this.cartUpdateListener = listener;
    }
    public void setData(List<CartItems> cartItems){
        this.cartItems = cartItems;
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

        cartItemRef = database.getReference().child("user").child(userId).child("CartItems");

        CartItems item = cartItems.get(position);
        holder.txtName.setText(item.getFoodName());
        holder.txtPrice.setText(item.getFoodPrice());
        holder.txtQuanity.setText(item.getFoodQuanity()+"");
        Glide.with(context).load(item.getFoodImage()).into(holder.foodImage);

        holder.imgMinus.setOnClickListener(view -> {
            if (item.getFoodQuanity() > 1) {
                int newQuantity = item.getFoodQuanity() - 1;
                updateQuantity(item, newQuantity, position);
                holder.txtQuanity.setText(String.valueOf(newQuantity)); // Cập nhật ngay UI
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
                //deleteQuanity(holder,holder.getAdapterPosition());
            }
        });
    }

    private void updateQuantity(CartItems item, int newQuantity, int position) {
        getUniqueKeyAtPosition(position, uniqueKey -> {
            if (uniqueKey != null) {
                cartItemRef.child(uniqueKey).child("foodQuanity")
                        .setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            item.setFoodQuanity(newQuantity); // 🔥 Cập nhật số lượng ngay trong danh sách
                            notifyDataSetChanged(); // 🔥 Cập nhật RecyclerView
                            if (cartUpdateListener != null) {
                                cartUpdateListener.onCartUpdated();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi cập nhật Firebase!", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void deleteQuanity(CartViewHolder holder,int position) {
        int positionRetrieve = position;
        getUniqueKeyAtPosition(positionRetrieve, uniqueKey -> {
            if (uniqueKey != null) {
                removeItem(position, uniqueKey);
            }
        });
    }

    private void removeItem(int position, String uniqueKey) {
        if (uniqueKey != null) {
            cartItemRef.child(uniqueKey).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        cartItems.remove(position);
                        itemQuantities = IntStream.range(0, itemQuantities.length)
                                .filter(i -> i != position)
                                .map(i -> itemQuantities[i])
                                .toArray();

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void getUniqueKeyAtPosition(int positionRetrieve, OnCompleteListener<String> onComplete) {
        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uniqueKey = null;
                int index = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (index == positionRetrieve) {
                        uniqueKey = dataSnapshot.getKey();
                        break;
                    }
                    index++;
                }
                onComplete.onComplete(uniqueKey); // Trả kết quả qua callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onComplete.onComplete(null);
            }
        });
        
    }

    private void increseQuanity(CartViewHolder holder, int position) {
        if (itemQuantities[position] < 10){
            itemQuantities[position]++;
            holder.txtQuanity.setText(itemQuantities[position]+"");
            getUniqueKeyAtPosition(position, uniqueKey -> {
                if (uniqueKey != null) {
                    // Cập nhật Firebase
                    cartItemRef.child(uniqueKey).child("foodQuanity")
                            .setValue(itemQuantities[position])
                            .addOnSuccessListener(aVoid -> {
                                holder.txtQuanity.setText(String.valueOf(itemQuantities[position]));
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                            );
                }
            });
        }
    }

    private void decreaseQuanity(CartViewHolder holder,int position) {
        if (itemQuantities[position] > 1){
            itemQuantities[position]--;
            holder.txtQuanity.setText(itemQuantities[position]+"");
            getUniqueKeyAtPosition(position, uniqueKey -> {
                if (uniqueKey != null) {
                    // Cập nhật Firebase
                    cartItemRef.child(uniqueKey).child("foodQuanity")
                            .setValue(itemQuantities[position])
                            .addOnSuccessListener(aVoid -> {
                                holder.txtQuanity.setText(String.valueOf(itemQuantities[position]));
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                            );
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        if (cartItems != null){
            return cartItems.size();
        }
        return 0;
    }

    public interface CartUpdateListener {
        void onCartUpdated();
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
