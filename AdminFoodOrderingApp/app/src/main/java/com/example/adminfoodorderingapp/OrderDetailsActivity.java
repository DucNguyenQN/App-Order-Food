package com.example.adminfoodorderingapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adminfoodorderingapp.adapter.OrderDetailsAdapter;
import com.example.adminfoodorderingapp.databinding.ActivityOrderDetailsBinding;
import com.example.adminfoodorderingapp.model.CartItems;
import com.example.adminfoodorderingapp.model.OrderDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;
    private String userName, address, phoneNumber, total;
    List<String> foodNames = new ArrayList<>(), foodImages = new ArrayList<>(), foodQuantity = new ArrayList<>(), foodPrices = new ArrayList<>();
    private OrderDetailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        adapter = new OrderDetailsAdapter(this);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        OrderDetails receivedOrderDetail = (OrderDetails) getIntent().getSerializableExtra("userOrderDetail");
        if (receivedOrderDetail != null){
            for (CartItems item : receivedOrderDetail.getCartItems()){
                foodNames.add(item.getFoodName());
                foodImages.add(item.getFoodImage());
                foodPrices.add(item.getFoodPrice());
                foodQuantity.add(item.getFoodQuanity()+"");
            }
            userName  = receivedOrderDetail.getUserName();
            address = receivedOrderDetail.getAddress();
            phoneNumber = receivedOrderDetail.getPhoneNumber();
            total = receivedOrderDetail.getTotalPrice();
            setUserDetails(receivedOrderDetail);
            setAdapter();
        }
    }

    private void setUserDetails(OrderDetails receivedOrderDetail) {
        binding.Name.setText(userName);
        binding.Address.setText(address);
        binding.Phone.setText(phoneNumber);
        binding.total.setText(total);
    }

    private void setAdapter() {
        binding.orderDetailsRecycle.setLayoutManager(new LinearLayoutManager(this));
        adapter.setData(foodNames, foodQuantity, foodImages, foodPrices);
        binding.orderDetailsRecycle.setAdapter(adapter);
    }
}