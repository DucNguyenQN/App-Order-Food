package com.example.adminfoodorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adminfoodorderingapp.databinding.ActivityMainBinding;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Formattable;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference completeOrderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InnitControl();
    }
    private void InnitControl() {
        binding.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddItemsActivity.class));
            }
        });
        binding.cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AllItemActivity.class));
            }
        });
        binding.outFrDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OutForDeliveryActivity.class));
            }
        });
        binding.cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdminProfileActivity.class));
            }
        });
        binding.cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateUserActivity.class));
            }
        });
        binding.pendingOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PendingOrderActivity.class));
            }
        });
        pendingOrders();
        completeOrders();
        wholeTimeEarning();
    }

    private void wholeTimeEarning() {
        List<Integer> listOfTotalPay = new ArrayList<>();
        completeOrderRef = FirebaseDatabase.getInstance().getReference().child("CompleteOrder");
        completeOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ordersnapshot : snapshot.getChildren()){
                    OrderDetails completeOrder  = ordersnapshot.getValue(OrderDetails.class);
                    if (completeOrder != null){
                        if (completeOrder.getTotalPrice() != null){
                            double doubleNumber = Double.parseDouble(completeOrder.getTotalPrice());
                            listOfTotalPay.add((int) doubleNumber);
                        }
                    }
                }
                int sum = 0;
                for (int num : listOfTotalPay) {
                    sum += num;
                }
                binding.whole.setText(sum +"$");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void completeOrders() {
        database   = FirebaseDatabase.getInstance();
        DatabaseReference completeOrderRefe = database.getReference().child("CompleteOrder");
        completeOrderRefe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.completeOrder.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pendingOrders() {
        database   = FirebaseDatabase.getInstance();
        DatabaseReference pendingOrderRef = database.getReference().child("OrderDetails");
        pendingOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.pendingOrderCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}