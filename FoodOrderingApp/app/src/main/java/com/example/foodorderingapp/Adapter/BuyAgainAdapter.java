package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.DetailsActivity;
import com.example.foodorderingapp.databinding.BuyAgainItemBinding;
import com.example.foodorderingapp.model.CartItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BuyAgainAdapter extends RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>{
    private Context context;
    private List<CartItems> cartItems;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String resId, nameRes;

    @NonNull
    @Override
    public BuyAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BuyAgainItemBinding binding = BuyAgainItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new BuyAgainViewHolder(binding);
    }

    public BuyAgainAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CartItems> cartItems, String resId, String nameRes){
        this.cartItems = cartItems;
        this.resId = resId;
        this.nameRes = nameRes;
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
            binding.foodprice.setText(formatStringNumber(cartItems1.getFoodPrice()) +" VND");
            Glide.with(context).load(cartItems1.getFoodImage()).into(binding.foodImage);

            binding.btnBuyAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference cartRef = database.child("user").child(userId).child("CartItems");

                    cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                // Giỏ hàng trống → tạo node theo nhà hàng mới
                                addItemToCart(cartRef.child(resId), cartItems1);
                                return;
                            }

                            // Lấy resId hiện tại trong giỏ hàng
                            String currentResId = dataSnapshot.getChildren().iterator().next().getKey();

                            if (!currentResId.equals(resId)) {
                                // Khác nhà hàng → hiển thị cảnh báo
                                new AlertDialog.Builder(context)
                                        .setTitle("Giỏ hàng khác nhà hàng")
                                        .setMessage("Bạn chỉ có thể đặt món từ một nhà hàng mỗi lần. Xóa giỏ hàng hiện tại để thêm món mới?")
                                        .setPositiveButton("Xóa và thêm mới", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Xóa giỏ hàng cũ
                                                cartRef.removeValue().addOnSuccessListener(unused -> {
                                                    // Thêm món mới sau khi xóa
                                                    addItemToCart(cartRef.child(resId), cartItems1);
                                                });
                                            }
                                        })
                                        .setNegativeButton("Huỷ", null)
                                        .show();
                            } else {
                                // Cùng nhà hàng → kiểm tra trùng món
                                DatabaseReference currentResCart = cartRef.child(resId);
                                currentResCart.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean itemExists = false;
                                        String itemKey = null;
                                        int currentQuantity = 0;

                                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                                            if (itemSnap.getKey().equals("restaurantName")) continue;
                                            CartItems existingItem = itemSnap.getValue(CartItems.class);
                                            if (existingItem.getFoodName().equals(cartItems1.getFoodName())) {
                                                itemExists = true;
                                                itemKey = itemSnap.getKey();
                                                currentQuantity = existingItem.getFoodQuanity();
                                                break;
                                            }
                                        }

                                        if (itemExists) {
                                            int newQuantity = currentQuantity + 1;
                                            currentResCart.child(itemKey).child("foodQuanity").setValue(newQuantity)
                                                    .addOnSuccessListener(runnable -> {
                                                        Toast.makeText(context, "Đã cập nhật số lượng sản phẩm", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(runnable -> {
                                                        Toast.makeText(context, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            addItemToCart(currentResCart, cartItems1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
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
        private void addItemToCart(DatabaseReference cartRef, CartItems cartItems1) {
            cartRef.child("restaurantName").setValue(nameRes);
//            CartItems cartItems = new CartItems(
//                    menuItem.getFoodName(),
//                    menuItem.getFoodPrice(),
//                    menuItem.getFoodDescription(),
//                    menuItem.getFoodImage(),
//                    1
//            );

            cartRef.push().setValue(cartItems1)
                    .addOnSuccessListener(runnable -> {
                        Toast.makeText(context, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(runnable -> {
                        Toast.makeText(context, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
