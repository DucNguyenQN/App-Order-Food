package com.example.adminfoodorderingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminfoodorderingapp.adapter.MenuItemAdapter;
import com.example.adminfoodorderingapp.model.AllMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllItemActivity extends AppCompatActivity {
    RecyclerView Recycle;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    List<AllMenu> menuItems = new ArrayList<>();
    String userId;
    MenuItemAdapter adapter;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InnitView();
        mAuth  = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        adapter = new MenuItemAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        Recycle.setLayoutManager(linearLayoutManager);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        retriveMenuItem();
    }



    private void retriveMenuItem() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference().child("user").child(userId).child("menu");
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuItems.clear();
                for (DataSnapshot foodsnapshot :snapshot.getChildren()){
                    AllMenu  menuItem = foodsnapshot.getValue(AllMenu.class);
                    menuItems.add(menuItem);
                }
                adapter.setData(menuItems);
                Recycle.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void InnitView() {
        Recycle = findViewById(R.id.RecycleMenu);
        backButton = findViewById(R.id.backButton);
    }
}