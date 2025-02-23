package com.example.foodorderingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderingapp.Adapter.CartAdapter;
import com.example.foodorderingapp.Adapter.MenuAdapter;
import com.example.foodorderingapp.PayOutActivity;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {
    private RecyclerView cartRecycle;
    private TextView txtCartTotal, txtItemTotal;
    private ConstraintLayout btnCheckOut;
    private FirebaseDatabase database;
    private List<CartItems> cartItem = new ArrayList<>();
    private CartAdapter adapter;
    private FirebaseAuth mAuth;
    private String userId;
    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartRecycle = view.findViewById(R.id.cartRecycle);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        txtCartTotal = view.findViewById(R.id.txt_cart_total);
        txtItemTotal = view.findViewById(R.id.txt_item_total);

        adapter = new CartAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        cartRecycle.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = database.getReference().child("user").child(userId).child("CartItems");
        cartRef.addValueEventListener(new ValueEventListener() {
            double totalCartPrice = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItem.clear();
                for (DataSnapshot foodsnapshot :snapshot.getChildren()){
                    CartItems  cartItems = foodsnapshot.getValue(CartItems.class);
                    cartItem.add(cartItems);
                    double price = Double.parseDouble(cartItems.getFoodPrice());
                    totalCartPrice += price * cartItems.getFoodQuanity();
                }
                adapter.setData(cartItem);
                cartRecycle.setAdapter(adapter);
                txtCartTotal.setText("$"+totalCartPrice);
                txtItemTotal.setText(cartItem.size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PayOutActivity.class);
                if (cartItem.size() != 0){
                    intent.putExtra("cart_list", (Serializable) cartItem);
                    startActivity(intent);
                }
            }
        });
    }
    public void updateTotal() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("user").child(userId).child("CartItems");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalCartPrice = 0;
                int totalItems = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItems item = itemSnapshot.getValue(CartItems.class);
                    if (item != null) {
                        double price = Double.parseDouble(item.getFoodPrice());
                        totalCartPrice += price * item.getFoodQuanity();
                        totalItems++;
                    }
                }
                txtCartTotal.setText("$" + totalCartPrice);
                txtItemTotal.setText(totalItems + "");

                Toast.makeText(getContext(), "Tổng tiền: $" + totalCartPrice, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi cập nhật giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onCartUpdated() {
        AtomicReference<Double> totalCartPrice = new AtomicReference<>(0.0);
        AtomicReference<Integer> totalItems = new AtomicReference<>(0);

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("user").child(userId).child("CartItems");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItems item = itemSnapshot.getValue(CartItems.class);
                    if (item != null) {
                        double price = Double.parseDouble(item.getFoodPrice());
                        int quantity = item.getFoodQuanity();
                        totalCartPrice.updateAndGet(v -> v + (price * quantity)); // Cập nhật giá trị
                        totalItems.updateAndGet(v -> v + 1);
                    }
                }
                txtCartTotal.setText("$" + totalCartPrice.get());
                txtItemTotal.setText(totalItems.get() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tính tổng tiền!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}