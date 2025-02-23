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
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.foodorderingapp.Adapter.MenuAdapter;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView popularRecycleView;
    private TextView txtViewMenu;
    private FirebaseDatabase database;
    private List<MenuItem> menuItem= new ArrayList<>();
    private MenuAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        banner(view);
        txtViewMenu = view.findViewById(R.id.txtViewMenu);
        popularRecycleView = view.findViewById(R.id.popularRecycleView);
        adapter = new MenuAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        popularRecycleView.setLayoutManager(linearLayoutManager);

        MenuBottomSheetFragment bottomSheetDialog = new MenuBottomSheetFragment();
        txtViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show(getParentFragmentManager(),"Test");
            }
        });
        diplayPorpularItem();
    }

    private void diplayPorpularItem() {
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
                randomPopularItem();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void randomPopularItem() {
        List<Integer> index = new ArrayList<>();
        for (int i=0; i<menuItem.size(); i++){
            index.add(i);
        }
        Collections.shuffle(index);
        int numItemToShow = 6; // Edit product display on screen

        List<MenuItem> subsetMenuItems = new ArrayList<>();
        for (int i = 0; i < numItemToShow; i++) {
            subsetMenuItems.add(menuItem.get(index.get(i)));
        }
        adapter.setData(subsetMenuItems);
        popularRecycleView.setAdapter(adapter);
    }

    private void banner(View view){
        ArrayList<SlideModel> imagelist = new ArrayList<>();
        imagelist.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        imagelist.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));

        ImageSlider imageSlider;
        imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imagelist);
        imageSlider.setImageList(imagelist, ScaleTypes.FIT);
    }
}