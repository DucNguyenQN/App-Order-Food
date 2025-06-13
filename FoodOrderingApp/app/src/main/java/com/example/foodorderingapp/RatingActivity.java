package com.example.foodorderingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Adapter.RatingAdapter;
import com.example.foodorderingapp.databinding.ActivityRatingBinding;
import com.example.foodorderingapp.databinding.RatingItemBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.OrderDetails;
import com.example.foodorderingapp.model.RatingData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.logging.LogFactory;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(RatingActivity.class);
    private OrderDetails orderDetails;
    private RatingAdapter adapter;
    private ActivityRatingBinding binding;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        //binding.btnRating.setVisibility(View.GONE);
        adapter = new RatingAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recycleRating.setLayoutManager(linearLayoutManager);
        orderDetails = (OrderDetails) bundle.get("itemDetail");
        if (orderDetails == null || orderDetails.getItemPushKey() == null) {
            Toast.makeText(this, "Thiếu thông tin đơn hàng để đánh giá!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        List<CartItems> cartItems = orderDetails.getCartItems();
        //checkIsRating();
        if (cartItems != null && !cartItems.isEmpty()){
            adapter.setData(cartItems);
            binding.recycleRating.setAdapter(adapter);
            for (CartItems item : cartItems) {
                Log.d("CartItem", "ID: " + item.getId() + ", Name: " + item.getFoodName() + ", Price: " + item.getFoodPrice());
            }
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllItemsRated()) {
                    //Toast.makeText(RatingActivity.this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();

                    DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("Ratings");
                    DatabaseReference orderRatingRef = FirebaseDatabase.getInstance().getReference("OrderRatings");

                    for (RatingData data : adapter.getRatingDataList()) {
//                        // Tạo key tự động cho mỗi đánh giá (cho phép lưu nhiều đánh giá khác nhau)
//                        Toast.makeText(RatingActivity.this, "DataID: "+data.getItemId(), Toast.LENGTH_SHORT).show();
                        String key = ratingRef.child(data.getItemId()).push().getKey();
                        Toast.makeText(RatingActivity.this, "Key: "+key, Toast.LENGTH_SHORT).show();

                        if (key == null) {
                            Log.e("Rating", "Không thể tạo key cho đánh giá.");
                            continue;
                        }

                        Map<String, Object> ratingMap = new HashMap<>();
                        ratingMap.put("userId", userId);
                        ratingMap.put("ratingValue", data.getRatingValue());
                        ratingMap.put("comment", data.getComment());
                        ratingMap.put("timestamp", ServerValue.TIMESTAMP);
                        /// rating for menu item
                        ratingRef.child(data.getItemId()).child(key).setValue(ratingMap)
                                .addOnSuccessListener(aVoid -> Log.d("Rating", "Đánh giá lưu thành công cho item: " + data.getItemId()))
                                .addOnFailureListener(e -> Log.e("Rating", "Lỗi khi lưu đánh giá: " + e.getMessage()));
                    }

                    Map<String, Object> itemsMap = new HashMap<>();
                    for (RatingData data : adapter.getRatingDataList()) {
                        Map<String, Object> itemRating = new HashMap<>();
                        itemRating.put("ratingValue", data.getRatingValue());
                        itemRating.put("comment", data.getComment());
                        itemsMap.put(data.getItemId(), itemRating);
                    }

                    Map<String, Object> orderRatingMap = new HashMap<>();
                    orderRatingMap.put("userId", userId);
                    orderRatingMap.put("timestamp", ServerValue.TIMESTAMP);
                    orderRatingMap.put("items", itemsMap);


                    orderRatingRef.child(orderDetails.getItemPushKey()).setValue(orderRatingMap);
                    finish();
                } else {
                    Toast.makeText(RatingActivity.this, "Vui lòng đánh giá và điền đầy đủ mô tả!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void checkIsRating() {
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("OrderRatings");
        ratingRef.child(orderDetails.getItemPushKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.btnRating.setVisibility(View.GONE); // Đã đánh giá
                } else {
                    binding.btnRating.setVisibility(View.VISIBLE); // Chưa đánh giá
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private boolean isAllItemsRated() {
        for (RatingData data : adapter.getRatingDataList()) {
            if (data.getRatingValue() == 0 || data.getComment().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}