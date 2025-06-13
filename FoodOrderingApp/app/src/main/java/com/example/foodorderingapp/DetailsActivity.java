package com.example.foodorderingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Adapter.RatingDetailsAdapter;
import com.example.foodorderingapp.databinding.ActivityDetailsBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.MenuItem;
import com.example.foodorderingapp.model.RatingItem;
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

public class DetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityDetailsBinding binding;
    private MenuItem menuItem;
    private RatingDetailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        adapter = new RatingDetailsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recyclerRating.setLayoutManager(linearLayoutManager);
        binding.recyclerRating.setAdapter(adapter);
        menuItem = (MenuItem) bundle.get("menuitem");
        binding.detailsFoodName.setText(menuItem.getFoodName());
        Glide.with(this).load(menuItem.getFoodImage()).into(binding.detailFoodImage);
        binding.txtDecription.setText(menuItem.getFoodDescription());
        binding.txtIngredients.setText(menuItem.getFoodIngredients());
        binding.nameRes.setText(menuItem.getNameOfRestaurance());
        binding.price.setText("Giá: "+formatStringNumber(menuItem.getFoodPrice()) +" VND");
        //Toast.makeText(this, menuItem.getId(), Toast.LENGTH_SHORT).show();
        getTotalProduct();
        getRating();
        binding.btnViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, ResActivity.class);
                intent.putExtra("resId", menuItem.getResId());
                startActivity(intent);
            }
        });
        binding.addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String userId = mAuth.getCurrentUser().getUid();
                String newResId = menuItem.getResId();
                DatabaseReference cartRef = database.child("user").child(userId).child("CartItems");

                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Giỏ hàng trống → tạo node theo nhà hàng mới
                            addItemToCart(cartRef.child(newResId), menuItem);
                            return;
                        }

                        // Lấy resId hiện tại trong giỏ hàng
                        String currentResId = dataSnapshot.getChildren().iterator().next().getKey();

                        if (!currentResId.equals(newResId)) {
                            // Khác nhà hàng → hiển thị cảnh báo
                            new AlertDialog.Builder(DetailsActivity.this)
                                    .setTitle("Giỏ hàng khác nhà hàng")
                                    .setMessage("Bạn chỉ có thể đặt món từ một nhà hàng mỗi lần. Xóa giỏ hàng hiện tại để thêm món mới?")
                                    .setPositiveButton("Xóa và thêm mới", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Xóa giỏ hàng cũ
                                            cartRef.removeValue().addOnSuccessListener(unused -> {
                                                // Thêm món mới sau khi xóa
                                                addItemToCart(cartRef.child(newResId), menuItem);
                                            });
                                        }
                                    })
                                    .setNegativeButton("Huỷ", null)
                                    .show();
                        } else {
                            // Cùng nhà hàng → kiểm tra trùng món
                            DatabaseReference currentResCart = cartRef.child(newResId);
                            currentResCart.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean itemExists = false;
                                    String itemKey = null;
                                    int currentQuantity = 0;

                                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                                        if (itemSnap.getKey().equals("restaurantName")) continue;
                                        CartItems existingItem = itemSnap.getValue(CartItems.class);
                                        if (existingItem.getFoodName().equals(menuItem.getFoodName())) {
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
                                                    Toast.makeText(DetailsActivity.this, "Đã cập nhật số lượng sản phẩm", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(runnable -> {
                                                    Toast.makeText(DetailsActivity.this, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        addItemToCart(currentResCart, menuItem);
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
    private void addItemToCart(DatabaseReference cartRef, MenuItem menuItem) {
        cartRef.child("restaurantName").setValue(menuItem.getNameOfRestaurance());
        CartItems cartItems = new CartItems(
                menuItem.getId(),
                menuItem.getFoodName(),
                menuItem.getFoodPrice(),
                menuItem.getFoodDescription(),
                menuItem.getFoodImage(),
                1
        );

        cartRef.push().setValue(cartItems)
                .addOnSuccessListener(runnable -> {
                    Toast.makeText(DetailsActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(runnable -> {
                    Toast.makeText(DetailsActivity.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                });
    }
    private void getTotalProduct(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference item = db.child("user").child(menuItem.getResId()).child("menu");
        item.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = snapshot.getChildrenCount();
                binding.totalProduct.setText(total+" products");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRating(){
        //Toast.makeText(this, "menuID"+menuItem.getId(), Toast.LENGTH_SHORT).show();
        List<RatingItem> ratingList = new ArrayList<>();
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(menuItem.getId());
        ratingRef.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ratingList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    RatingItem rating = child.getValue(RatingItem.class);
                    if (rating != null) {
                        ratingList.add(rating);
                    }
                }
                adapter.setData(ratingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailsActivity.this, "Lỗi khi lấy đánh giá", Toast.LENGTH_SHORT).show();
            }
        });
    };

}