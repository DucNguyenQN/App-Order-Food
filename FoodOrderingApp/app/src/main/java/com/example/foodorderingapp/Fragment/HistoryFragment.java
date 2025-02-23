package com.example.foodorderingapp.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Adapter.BuyAgainAdapter;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.FragmentHistoryBinding;
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
import java.util.List;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private BuyAgainAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String userId;
    private List<OrderDetails> listOfOrderitem = new ArrayList<>();
    
    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        adapter = new BuyAgainAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerecent.setLayoutManager(linearLayoutManager);
        retriveBuyHistory();
        binding.buttonReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus();
            }
        });
    }

    private void updateOrderStatus() {
        String itemPushKey = listOfOrderitem.get(0).getItemPushKey();
        DatabaseReference completeOrderRef = database.getReference().child("CompleteOrder").child(itemPushKey);
        completeOrderRef.child("paymentReceived").setValue(true);
        binding.buttonReceived.setVisibility(View.INVISIBLE);
    }

    private void retriveBuyHistory() {
        binding.recentbuyitem.setVisibility(View.INVISIBLE);
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference buyItemRef = database.getReference().child("user").child(userId).child("BuyHistory");
        Query sortingQuery = buyItemRef.orderByChild("currentTime");
        sortingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot buysanpshot : snapshot.getChildren()){
                    OrderDetails buyHistoryItem = buysanpshot.getValue(OrderDetails.class);
                    listOfOrderitem.add(buyHistoryItem);
                }
                Collections.reverse(listOfOrderitem);
                if (!listOfOrderitem.isEmpty()){
                    setDataInRecentbuy();
                    if (!listOfOrderitem.isEmpty()){
                        setUpRecycleView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpRecycleView() {
        OrderDetails recentOrderItem = null;
        if (listOfOrderitem != null && !listOfOrderitem.isEmpty()) {
            recentOrderItem = listOfOrderitem.get(0);
        }
        if (recentOrderItem != null) {
            if (binding != null) {
                OrderDetails item = recentOrderItem;
                List<CartItems> ListCartItem = item.getCartItems();
                adapter.setData(ListCartItem);
                binding.recyclerecent.setAdapter(adapter);
            }
        }
    }

    private void setDataInRecentbuy() {
        binding.recentbuyitem.setVisibility(View.VISIBLE);
        OrderDetails recentOrderItem = null;
        if (listOfOrderitem != null && !listOfOrderitem.isEmpty()) {
            recentOrderItem = listOfOrderitem.get(0);
        }
        if (recentOrderItem != null) {
            if (binding != null) {
                OrderDetails item = recentOrderItem;
                List<CartItems> ListCartItem = item.getCartItems();
                if (ListCartItem != null && !ListCartItem.isEmpty()) {
                    CartItems cartItems = ListCartItem.get(0);
                    binding.foodname.setText(cartItems.getFoodName());
                    binding.foodprice.setText("$"+cartItems.getFoodPrice());
                    Glide.with(getContext()).load(cartItems.getFoodImage()).into(binding.foodImage);

                    Boolean isOrderIsAccepted = listOfOrderitem.get(0).getOrderAcceppted();
                    if (isOrderIsAccepted){
                        binding.cardView5.setCardBackgroundColor(Color.GREEN);
                        binding.buttonReceived.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}