package com.example.foodorderingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingapp.Fragment.CongratsBottomSheetFragment;
import com.example.foodorderingapp.databinding.ActivityPayOutBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PayOutActivity extends AppCompatActivity {
    private ActivityPayOutBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    String name, address, phone;
    double totalPrice;
    List<CartItems> cartList;
    CongratsBottomSheetFragment bottomSheetFragment;
    String userId;
    OrderDetails orderDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPayOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        cartList = (List<CartItems>) getIntent().getSerializableExtra("cart_list");
        Log.d("CartDebug", "CartItem size: " + cartList.size());
        for (CartItems item : cartList) {
            Log.d("CartDebug", "Item: " + item.getFoodName());
        }
        if (cartList != null) {
            totalPrice = getTotalPrice(cartList);
            binding.payoutTotal.setText("$"+totalPrice);
        }
        bottomSheetFragment = new CongratsBottomSheetFragment();
        binding.btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.payoutName.getText().toString().trim();
                address = binding.payoutAddress.getText().toString().trim();
                phone = binding.payoutPhone.getText().toString().trim();
                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()){
                    Toast.makeText(PayOutActivity.this, "Please fill all the detail", Toast.LENGTH_SHORT).show();
                }else{
                    placeOrder();
                }

            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setUserData();
    }

    private void placeOrder() {
        userId = mAuth.getCurrentUser().getUid();
        Long time = System.currentTimeMillis();
        String itemPushKey = database.child("OrderDetails").push().getKey();
        orderDetails = new OrderDetails(userId, name, address,totalPrice+"", phone, false, false,itemPushKey, time,cartList );
        DatabaseReference orderRef = database.child("OrderDetails").child(itemPushKey);
        orderRef.setValue(orderDetails).addOnSuccessListener(runnable ->{
                Toast.makeText(this, "orderSucces", Toast.LENGTH_SHORT).show();
                    bottomSheetFragment.show(getSupportFragmentManager(), "test");
                    removeItemFromCart();
                    addOrderToHistory();
                })
                .addOnFailureListener(runnable ->
                        Toast.makeText(this, "orderFail", Toast.LENGTH_SHORT).show()
                );
    }

    private void addOrderToHistory() {
        database.child("user").child(userId).child("BuyHistory").child(orderDetails.getItemPushKey())
                .setValue(orderDetails)
                .addOnSuccessListener(runnable -> {

                });
    }

    private void removeItemFromCart() {
        DatabaseReference cartItemRef = database.child("user").child(userId).child("CartItems");
        cartItemRef.removeValue();
    }

    double getTotalPrice(List<CartItems> cartList) {
        double total = 0;
        for (CartItems item : cartList) {
            double price = Double.parseDouble(item.getFoodPrice()); // Chuyển đổi giá từ String sang double
            total += price * item.getFoodQuanity();
        }
        return total;
    }
    private void setUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
             String userId = user.getUid();
            DatabaseReference userRef = database.child("user").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String names = snapshot.child("userName").getValue() == null ? "" : snapshot.child("userName").getValue(String.class);
                        String addresss = snapshot.child("address").getValue() == null ? "" : snapshot.child("address").getValue(String.class);
                        String phones = snapshot.child("phone").getValue() == null ? "" : snapshot.child("phone").getValue(String.class);
                        binding.payoutName.setText(names+"");
                        binding.payoutAddress.setText(addresss+"");
                        binding.payoutPhone.setText(phones+"");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}