package com.example.adminfoodorderingapp;

import android.os.Bundle;

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
    List<AllMenu> menuItems = new ArrayList<>();
    MenuItemAdapter adapter;
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        adapter = new MenuItemAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        Recycle.setLayoutManager(linearLayoutManager);

        retriveMenuItem();
    }



    private void retriveMenuItem() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference().child("menu");
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
    }
}