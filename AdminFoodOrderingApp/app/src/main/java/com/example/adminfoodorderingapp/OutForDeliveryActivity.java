package com.example.adminfoodorderingapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adminfoodorderingapp.adapter.DeliveryAdapter;
import com.example.adminfoodorderingapp.databinding.ActivityOutForDeliveryBinding;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutForDeliveryActivity extends AppCompatActivity {
    private ActivityOutForDeliveryBinding binding;
    private DeliveryAdapter adapter;
    private FirebaseDatabase database;
    private List<OrderDetails> listOfComleteOrder = new ArrayList<>();
    private String resId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOutForDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        adapter = new DeliveryAdapter(this);
        mAuth = FirebaseAuth.getInstance();
        resId = mAuth.getCurrentUser().getUid();

        retriveCompleteOrderDetails();

        binding.deliveryRecycle.setLayoutManager(new LinearLayoutManager(this));
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void retriveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance();
        Query completeOrderRef = database.getReference().child("CompleteOrder").child(resId).orderByChild("currentTime");

        completeOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfComleteOrder.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()){
                    OrderDetails orderComplete = orderSnapshot.getValue(OrderDetails.class);
                    if (orderComplete != null){
                        listOfComleteOrder.add(orderComplete);
                    }
                }
                Collections.reverse(listOfComleteOrder);
                setDataInToRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataInToRecycleView() {
        adapter.setData(listOfComleteOrder);
        binding.deliveryRecycle.setAdapter(adapter);
    }
}