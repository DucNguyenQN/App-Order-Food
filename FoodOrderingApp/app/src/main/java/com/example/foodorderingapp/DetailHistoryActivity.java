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

import com.example.foodorderingapp.Adapter.BuyAgainAdapter;
import com.example.foodorderingapp.Adapter.DetailHistoryAdapter;
import com.example.foodorderingapp.Adapter.MenuAdapter;
import com.example.foodorderingapp.Adapter.PopularAdapter;
import com.example.foodorderingapp.databinding.ActivityDetailHistoryBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailHistoryActivity extends AppCompatActivity {
    private ActivityDetailHistoryBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userId;
    private List<String> resId = new ArrayList<>();
    private List<OrderDetails> listOfOrderitem = new ArrayList<>();
    private DetailHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        adapter = new DetailHistoryAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recycleHisDetail.setLayoutManager(linearLayoutManager);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getBuyHistory();
    }

    private void getBuyHistory() {
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference buyHistoryRef = database.getReference().child("user").child(userId).child("BuyHistory");

        buyHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : restaurantSnapshot.getChildren()) {
                        OrderDetails order = orderSnapshot.getValue(OrderDetails.class);
                        listOfOrderitem.add(order);
                    }
                }

                // Sắp xếp giảm dần theo currentTime
                Collections.sort(listOfOrderitem, new Comparator<OrderDetails>() {
                    @Override
                    public int compare(OrderDetails o1, OrderDetails o2) {
                        return Long.compare(o2.getCurrentTime(), o1.getCurrentTime()); // Giảm dần
                    }
                });
                setUpRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    private void setUpRecycleView() {
        adapter.setData(listOfOrderitem);
        binding.recycleHisDetail.setAdapter(adapter);
    }
}