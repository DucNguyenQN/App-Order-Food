package com.example.foodorderingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.example.foodorderingapp.ResActivity;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {
    private RecyclerView cartRecycle;
    private TextView txtCartTotal, txtItemTotal, nameRes, viewshop;
    private ConstraintLayout btnCheckOut;
    private FirebaseDatabase database;
    private List<CartItems> cartItem = new ArrayList<>();
    private CartAdapter adapter;
    private FirebaseAuth mAuth;
    private String userId;
    private String resId;

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
        nameRes = view.findViewById(R.id.nameRes);
        viewshop = view.findViewById(R.id.viewShop);

        adapter = new CartAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        cartRecycle.setLayoutManager(linearLayoutManager);
        cartRecycle.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadCartData();
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PayOutActivity.class);
                if (cartItem.size() != 0) {
                    intent.putExtra("cart_list", (Serializable) cartItem);
                    Bundle bundle = new Bundle();
                    bundle.putString("resId", resId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Vui lòng chọn món ăn trước khi thanh toán!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (cartItem.size() == 0) {
            nameRes.setVisibility(View.GONE);
            viewshop.setVisibility(View.GONE);
        }
        viewshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResActivity.class);
                intent.putExtra("resId", resId);
                startActivity(intent);
            }
        });
    }
    private void loadCartData() {
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = database.getReference().child("user").child(userId).child("CartItems");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItem.clear();
                double totalCartPrice = 0;
                String restaurantName = ""; // Khai báo tên nhà hàng

                // Duyệt qua các nhà hàng trong giỏ hàng (mỗi nhà hàng là một resId)
                for (DataSnapshot resSnapshot : snapshot.getChildren()) {
                    resId = resSnapshot.getKey();
                    if (resSnapshot.child("restaurantName").exists()) {
                        // Lấy tên nhà hàng
                        restaurantName = resSnapshot.child("restaurantName").getValue(String.class);
                        nameRes.setVisibility(View.VISIBLE);
                        viewshop.setVisibility(View.VISIBLE);
                        nameRes.setText(restaurantName); // Hiển thị tên nhà hàng
                    }

                    // Duyệt qua các món ăn của nhà hàng đó
                    for (DataSnapshot itemSnapshot : resSnapshot.getChildren()) {
                        // Bỏ qua "restaurantName"
                        if (itemSnapshot.getKey().equals("restaurantName")) continue;

                        CartItems cartItems = itemSnapshot.getValue(CartItems.class);
                        if (cartItems != null) {
                            cartItem.add(cartItems);
                            double price = Double.parseDouble(cartItems.getFoodPrice());
                            totalCartPrice += price * cartItems.getFoodQuanity(); // Cập nhật tổng tiền
                        }
                    }
                }

                // Cập nhật dữ liệu lên UI
                adapter.setData(cartItem,resId);
                adapter.notifyDataSetChanged();
                txtCartTotal.setText(formatStringNumber(String.valueOf(totalCartPrice))+" VND"); // Hiển thị tổng tiền
                txtItemTotal.setText(String.valueOf(cartItem.size())); // Hiển thị tổng số món
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String formatStringNumber(String numberString) {
        try {
            double number = Double.parseDouble(numberString);
            int roundedNumber = (int) Math.round(number); // Làm tròn và ép int
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            return formatter.format(roundedNumber);
        } catch (NumberFormatException e) {
            Log.e("Format Error", "Không thể chuyển đổi chuỗi thành số: " + numberString);
            return numberString;
        }
    }
    @Override
    public void onCartUpdatedLocally(List<CartItems> updatedList) {
        this.cartItem = updatedList;
        adapter.setData(cartItem, resId);
        adapter.notifyDataSetChanged();

        double totalCartPrice = 0;
        for (CartItems item : cartItem) {
            double price = Double.parseDouble(item.getFoodPrice());
            totalCartPrice += price * item.getFoodQuanity();
        }

        txtCartTotal.setText(formatStringNumber(String.valueOf(totalCartPrice))+" VND");
        txtItemTotal.setText(String.valueOf(cartItem.size()));
        if (cartItem.isEmpty()) {
            nameRes.setVisibility(View.GONE);
            viewshop.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
    }
}