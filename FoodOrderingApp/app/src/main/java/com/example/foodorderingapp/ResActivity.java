package com.example.foodorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodorderingapp.Adapter.ChatListAdapter;
import com.example.foodorderingapp.Adapter.ResAdapter;
import com.example.foodorderingapp.databinding.ActivityResBinding;
import com.example.foodorderingapp.model.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResActivity extends AppCompatActivity {
    private ActivityResBinding binding;
    private String resId;
    private ResAdapter adapter;
    private List<MenuItem> menuItem = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        adapter = new ResAdapter(this);
        binding.recycleRes.setHasFixedSize(true);
        //Định nghĩa recycleView thành listview
        binding.recycleRes.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        resId  = getIntent().getStringExtra("resId");
        if (resId == null) {
            return;
        }
        getProduct();
        getNameRes();
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResActivity.this, ChatActivity.class);
                intent.putExtra("receiverId",resId);
                startActivity(intent);
            }
        });
    }
    private void getProduct() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference foodRef = database.getReference().child("user").child(resId).child("menu");
            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    menuItem.clear();
                    for (DataSnapshot foodsnapshot :snapshot.getChildren()){
                        MenuItem menuItems = foodsnapshot.getValue(MenuItem.class);
                        menuItem.add(menuItems);
                    }
                    adapter.setData(menuItem);
                    binding.recycleRes.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    private void getNameRes(){
        DatabaseReference resName = FirebaseDatabase.getInstance().getReference().child("user").child(resId).child("namofresturant");
        resName.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getValue(String.class);
                if (name != null) {
                    binding.nameRes.setText(name);
                }
            }
        });
    }
}