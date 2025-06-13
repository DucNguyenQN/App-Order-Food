package com.example.adminfoodorderingapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adminfoodorderingapp.adapter.PendingOrderAdapter;
import com.example.adminfoodorderingapp.api.api;
import com.example.adminfoodorderingapp.databinding.ActivityPendingOrderBinding;
import com.example.adminfoodorderingapp.inter.OnItemClickListener;
import com.example.adminfoodorderingapp.model.Message;
import com.example.adminfoodorderingapp.model.Notification;
import com.example.adminfoodorderingapp.model.NotificationModel;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.example.adminfoodorderingapp.model.SendMessageResponse;
import com.example.adminfoodorderingapp.model.dataMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingOrderActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityPendingOrderBinding binding;
    private PendingOrderAdapter adapter;
    private FirebaseAuth mAuth;
    private String resId;
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
        mAuth = FirebaseAuth.getInstance();
        resId = mAuth.getCurrentUser().getUid();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        database = FirebaseDatabase.getInstance();
        databaseOrderDetails = database.getReference().child("OrderDetails").child(resId);
        getOrderDetails();

    }

    private void getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ordersnapshot : snapshot.getChildren()) {
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
        for (OrderDetails order : listOfOrderItem) {
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
        intent.putExtra("userOrderDetail", userOrderDetail);
        startActivity(intent);
    }

    @Override
    public void onItemAcceptClick(int position) {
        String childItemPushKey = listOfOrderItem.get(position).getItemPushKey();
        if (childItemPushKey != null) {
            DatabaseReference clickItemOrderRef = database.getReference().child("OrderDetails").child(resId).child(childItemPushKey);
            if (clickItemOrderRef != null) {
                clickItemOrderRef.child("orderAcceppted").setValue(true);
                UpdateOrderAcceptStatus(position);
            }
        }
    }

    @Override
    public void onItemDispatchClick(int positon) {
        String dispatchItemPushKey = listOfOrderItem.get(positon).getItemPushKey();
        DatabaseReference dispatchItemOrderRef = database.getReference().child("CompleteOrder").child(resId).child(dispatchItemPushKey);
        dispatchItemOrderRef.setValue(listOfOrderItem.get(positon)).addOnSuccessListener(runnable -> {
            getUserId(dispatchItemPushKey);
            updateOrderAcceptStatus(dispatchItemPushKey);
        });
    }

    private void updateOrderAcceptStatus(String dispatchItemPushKey) {
        DatabaseReference orderRef = database.getReference().child("CompleteOrder").child(resId).child(dispatchItemPushKey);
        if(orderRef != null){
            orderRef.child("orderAcceppted").setValue(true);
        }
    }
    private void updateOrderAcceptUser(String userId, String dispatchItemPushKey) {
        DatabaseReference orderRef = database.getReference().child("user").child(userId).child("BuyHistory").child(resId).child(dispatchItemPushKey);
        if(orderRef != null){
            orderRef.child("orderAcceppted").setValue(true);
        }
    }

    private void deleteThisItemFromOrderDetails(String dispatchItemPushKey) {
        DatabaseReference orderDetailsItemsRef = database.getReference().child("OrderDetails").child(resId).child(dispatchItemPushKey);
        orderDetailsItemsRef.removeValue().addOnSuccessListener(runnable -> {
                    Toast.makeText(this, "Đơn hàng đã được giao", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(runnable -> {
                    Toast.makeText(this, "Đơn hàng chưa được giao", Toast.LENGTH_SHORT).show();
                });
    }


    private void UpdateOrderAcceptStatus(int position) {
        String userIdOfClickItem = listOfOrderItem.get(position).getUserId();
        String pushKeyOfClickItem = listOfOrderItem.get(position).getItemPushKey();
        DatabaseReference buyHistoryRef = database.getReference().child("user").child(userIdOfClickItem).child("BuyHistory").child(resId).child(pushKeyOfClickItem);
        buyHistoryRef.child("orderAcceppted").setValue(true);
        databaseOrderDetails.child(pushKeyOfClickItem).child("orderAcceppted").setValue(true);
    }

    private void getUserId(String dispatchItemPushKey) {
        DatabaseReference userRef = database.getReference().child("OrderDetails").child(resId).child(dispatchItemPushKey).child("userId");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.getValue(String.class);
                //Toast.makeText(PendingOrderActivity.this, userId, Toast.LENGTH_SHORT).show();
                if (userId != null) {
                    DatabaseReference userRef = database.getReference().child("user").child(userId).child("token");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String token = snapshot.getValue(String.class);
                            //Toast.makeText(PendingOrderActivity.this, token, Toast.LENGTH_SHORT).show();
                            if (token != null) {
                                //Toast.makeText(PendingOrderActivity.this, token, Toast.LENGTH_SHORT).show();
                                if (MainActivity.tokenSend == null || MainActivity.tokenSend.isEmpty()) {
                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            MainActivity.tokenSend = task.getResult();
                                            //Toast.makeText(PendingOrderActivity.this, "Gui sau", Toast.LENGTH_SHORT).show();
                                            // Sau khi có token thì gửi thông báo
                                            sendNotification(token, userId, dispatchItemPushKey);
                                        } else {
                                            //Toast.makeText(PendingOrderActivity.this, "Không thể lấy token để gửi thông báo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    //Toast.makeText(PendingOrderActivity.this, "Gui luon", Toast.LENGTH_SHORT).show();
                                    sendNotification(token, userId, dispatchItemPushKey);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(PendingOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendNotification(String token, String userId, String dispatchItemPushKey) {
        Notification notification = new Notification("Đơn hàng của bạn đã được gửi, chú ý điện thoại để nhận hàng", "Thông báo");
        Message message = new Message(token, notification);
        dataMessage dataMessage = new dataMessage(message);
        api.apiSendMessage.sendNotification("Bearer " + MainActivity.tokenSend, dataMessage).enqueue(new Callback<SendMessageResponse>() {
            @Override
            public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(PendingOrderActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    NotificationModel noti = new NotificationModel(
                            notification.getTitle(),
                            notification.getBody(),
                            userId,
                            System.currentTimeMillis()
                    );

                    db.collection("notifications")
                            .add(noti)
                            .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification saved"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving notification", e));
                    Toast.makeText(PendingOrderActivity.this, dispatchItemPushKey, Toast.LENGTH_SHORT).show();
                    deleteThisItemFromOrderDetails(dispatchItemPushKey);
                    updateOrderAcceptUser(userId,dispatchItemPushKey);
                } else {
                    Log.d("loi", "onResponse: " + response.message() + response.code());
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                //Toast.makeText(PendingOrderActivity.this, "Co loi", Toast.LENGTH_SHORT).show();
                Log.d("loigui", "onFailure: " + t.getMessage());
            }
        });
    }
}