package com.example.foodorderingapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.foodorderingapp.Adapter.MenuAdapter;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.MenuItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuBottomSheetFragment extends BottomSheetDialogFragment {

    ImageButton btnMenuBack;
    RecyclerView menuRecycle;
    private FirebaseDatabase database;
    private List<MenuItem> menuItem = new ArrayList<>();
    private MenuAdapter adapter;
    public MenuBottomSheetFragment() {
        // Required empty public constructor
    }

    public static MenuBottomSheetFragment newInstance(String param1, String param2) {
        MenuBottomSheetFragment fragment = new MenuBottomSheetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnMenuBack = view.findViewById(R.id.btnMenuBack);
        menuRecycle = view.findViewById(R.id.menuRecycle);
        btnMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        adapter = new MenuAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        menuRecycle.setLayoutManager(linearLayoutManager);
        database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference().child("menu");
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuItem.clear();
                for (DataSnapshot foodsnapshot :snapshot.getChildren()){
                    MenuItem  menuItems = foodsnapshot.getValue(MenuItem.class);
                    menuItem.add(menuItems);
                }
                adapter.setData(menuItem);
                menuRecycle.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}