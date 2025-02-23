package com.example.adminfoodorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adminfoodorderingapp.adapter.DeliveryAdapter;
import com.example.adminfoodorderingapp.adapter.PendingOrderAdapter;
import com.example.adminfoodorderingapp.databinding.ActivityPendingOrderBinding;
import com.example.adminfoodorderingapp.inter.OnItemClickListener;
import com.example.adminfoodorderingapp.model.CartItems;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PendingOrderActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityPendingOrderBinding binding;
    private PendingOrderAdapter adapter;

    private List<String> listOfName = new ArrayList<>();
    private List<String> listOfTotalPrice = new ArrayList<>();
    private List<String> listOfImageFirstFoodOrder = new ArrayList<>();
    private List<OrderDetails> listOfOrderItem = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference databaseOrderDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPendingOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        database = FirebaseDatabase.getInstance();
        databaseOrderDetails = database.getReference().child("OrderDetails");
        getOrderDetails();

    }

    private void getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ordersnapshot : snapshot.getChildren()){
                    OrderDetails orderDetails = ordersnapshot.getValue(OrderDetails.class);
                    listOfOrderItem.add(orderDetails);
                }
                addDataToListForRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addDataToListForRecycleView() {
        for (OrderDetails order : listOfOrderItem){
            listOfName.add(order.getUserName());
            listOfTotalPrice.add(order.getTotalPrice());
            if (order.getCartItems() != null && !order.getCartItems().isEmpty()) {
                listOfImageFirstFoodOrder.add(order.getCartItems().get(0).getFoodImage());
            } else {
                listOfImageFirstFoodOrder.add(null);
            }
        }
        setAdapter();
    }

    private void setAdapter() {
        binding.pendingOrderRecycle.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PendingOrderAdapter(this, this);
        adapter.setData(listOfName, listOfTotalPrice, listOfImageFirstFoodOrder);
        binding.pendingOrderRecycle.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        OrderDetails userOrderDetail = listOfOrderItem.get(position);
        Intent intent = new Intent(PendingOrderActivity.this, OrderDetailsActivity.class);
        intent.putExtra("userOrderDetail",userOrderDetail );
        startActivity(intent);
    }

    @Override
    public void onItemAcceptClick(int position) {
        String childItemPushKey = listOfOrderItem.get(position).getItemPushKey();
        if (childItemPushKey != null) {
            DatabaseReference clickItemOrderRef = database.getReference().child("OrderDetails").child(childItemPushKey);
            if (clickItemOrderRef != null) {
                clickItemOrderRef.child("orderAcceppted").setValue(true);
                UpdateOrderAcceptStatus(position);
            }
        }
    }

    @Override
    public void onItemDispatchClick(int positon) {
        String dispatchItemPushKey = listOfOrderItem.get(positon).getItemPushKey();
        DatabaseReference dispatchItemOrderRef = database.getReference().child("CompleteOrder").child(dispatchItemPushKey);
        dispatchItemOrderRef.setValue(listOfOrderItem.get(positon)).addOnSuccessListener(runnable -> {
            deleteThisItemFromOrderDetails(dispatchItemPushKey);
        });
    }

    private void deleteThisItemFromOrderDetails(String dispatchItemPushKey) {
        DatabaseReference orderDetailsItemsRef = database.getReference().child("OrderDetails").child(dispatchItemPushKey);
        orderDetailsItemsRef.removeValue().addOnSuccessListener(runnable -> {
            Toast.makeText(this, "Order Is Dispatched", Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(runnable -> {
            Toast.makeText(this, "Order Is Not Dispatched", Toast.LENGTH_SHORT).show();
        });
    }


    private void UpdateOrderAcceptStatus(int position) {
        //Update order accept in history and orderdetails
        String userIdOfClickItem = listOfOrderItem.get(position).getUserId();
        String pushKeyOfClickItem = listOfOrderItem.get(position).getItemPushKey();
        DatabaseReference buyHistoryRef = database.getReference().child("user").child(userIdOfClickItem).child("BuyHistory").child(pushKeyOfClickItem);
        buyHistoryRef.child("orderAcceppted").setValue(true);
        databaseOrderDetails.child(pushKeyOfClickItem).child("orderAcceppted").setValue(true);
    }

}