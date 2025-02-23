package com.example.foodorderingapp;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private TextView detailsFoodName, txtDecription, txtIngredients;
    private ImageView detailFoodImage;
    private Button addItemButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InnitView();
        mAuth = FirebaseAuth.getInstance();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        MenuItem menuItem = (MenuItem) bundle.get("menuitem");
        detailsFoodName.setText(menuItem.getFoodName());
        Glide.with(this).load(menuItem.getFoodImage()).into(detailFoodImage);
        Log.d("anhdetail", menuItem.getFoodImage());
        txtDecription.setText(menuItem.getFoodDescription());
        txtIngredients.setText(menuItem.getFoodIngredients());
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference cartRef = database.child("user").child(userId).child("CartItems");

                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean itemExists = false;
                        String itemKey = null;
                        int currentQuantity = 0;

                        // Duyệt qua từng món trong giỏ hàng
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItems existingItem = snapshot.getValue(CartItems.class);

                            if (existingItem.getFoodName().equals(menuItem.getFoodName())) { // So sánh foodName
                                itemExists = true;
                                itemKey = snapshot.getKey(); // Lấy key của món ăn
                                currentQuantity = existingItem.getFoodQuanity(); // Lấy số lượng hiện tại
                                break; // Thoát vòng lặp ngay khi tìm thấy
                            }
                        }

                        if (itemExists) {
                            // Nếu món ăn đã tồn tại, cập nhật số lượng mới
                            int newQuantity = currentQuantity + 1;
                            cartRef.child(itemKey).child("foodQuanity").setValue(newQuantity)
                                    .addOnSuccessListener(runnable -> {
                                        Toast.makeText(DetailsActivity.this, "Đã cập nhật số lượng sản phẩm", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(runnable -> {
                                        Toast.makeText(DetailsActivity.this, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                                    });
                        }else{
                            // Nếu chưa có trong giỏ, thêm mới
                            CartItems cartItems = new CartItems(menuItem.getFoodName(), menuItem.getFoodPrice(), menuItem.getFoodDescription(), menuItem.getFoodImage(), 1);
                            cartRef.push().setValue(cartItems)
                                    .addOnSuccessListener(runnable -> {
                                        Toast.makeText(DetailsActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(runnable -> {
                                        Toast.makeText(DetailsActivity.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private void InnitView() {
        imageButton = findViewById(R.id.imageButton);
        detailsFoodName = findViewById(R.id.detailsFoodName);
        txtDecription = findViewById(R.id.txtDecription);
        txtIngredients = findViewById(R.id.txtIngredients);
        detailFoodImage = findViewById(R.id.detailFoodImage);
        addItemButton = findViewById(R.id.addItemButton);
    }
}