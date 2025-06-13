package com.example.foodorderingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.API.api;
import com.example.foodorderingapp.Adapter.PayoutAdapter;
import com.example.foodorderingapp.Fragment.CongratsBottomSheetFragment;
import com.example.foodorderingapp.databinding.ActivityPayOutBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.CreateOrder;
import com.example.foodorderingapp.model.Message;
import com.example.foodorderingapp.model.Notification;
import com.example.foodorderingapp.model.OrderDetails;
import com.example.foodorderingapp.model.SendMessageResponse;
import com.example.foodorderingapp.model.dataMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PayOutActivity extends AppCompatActivity {
    private ActivityPayOutBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    String name, address, phone;
    double totalPrice;
    List<CartItems> cartList;
    CongratsBottomSheetFragment bottomSheetFragment;
    String userId, tthai_ttoan;
    String resId;
    OrderDetails orderDetails;
    ArrayList<String> arrayThanhToan;
    private PayoutAdapter payoutAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPayOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        arrayThanhToan = new ArrayList<String>();
        arrayThanhToan.add("Thanh toán khi nhận hàng");
        arrayThanhToan.add("Thanh toán bằng ZaloPay");

        payoutAdapter = new PayoutAdapter(this);
        binding.recycleProduct.setHasFixedSize(true);
        //Định nghĩa recycleView thành listview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recycleProduct.setLayoutManager(linearLayoutManager);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,arrayThanhToan);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.orderPayment.setAdapter(arrayAdapter);
        cartList = (List<CartItems>) getIntent().getSerializableExtra("cart_list");

        payoutAdapter.setData(cartList);
        binding.recycleProduct.setAdapter(payoutAdapter);

        resId = getIntent().getExtras().getString("resId");
        if (cartList != null) {
            totalPrice = getTotalPrice(cartList);
            binding.payoutTotal.setText(formatStringNumber(String.valueOf(totalPrice)) + "VND");
        }
        bottomSheetFragment = new CongratsBottomSheetFragment();

        binding.btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.payoutName.getText().toString().trim();
                address = binding.payoutAddress.getText().toString().trim();
                phone = binding.payoutPhone.getText().toString().trim();
                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()){
                    Toast.makeText(PayOutActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }else{
                    if (tthai_ttoan.equals("Thanh toán khi nhận hàng")) {
                        sendNotification();
                    }else{
//                        Intent intent = new Intent(PayOutActivity.this, ZaloPayActivity.class);
//                        intent.putExtra("total", String.valueOf(totalPrice));
////                        startActivity(intent);
//                        paymentLauncher.launch(intent);
                        int totalInt = (int) Math.round(totalPrice);
                        String totalString = Integer.toString(totalInt);
                        CreateOrder orderApi = new CreateOrder();
                        try {
                            JSONObject data = orderApi.createOrder(totalString);
                            Log.d("ZaloPay Response", data.toString());
                            String code = data.getString("return_code");
                            Log.d("code", code);
                            if (code.equals("1")) {
                                String token = data.getString("zp_trans_token");
                                Log.d("tokenzalo", token);
                                ZaloPaySDK.getInstance().payOrder(PayOutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String s, String s1, String s2) {
                                        Log.d("ZALO_PAY", "Thanh toán thành công");
                                        runOnUiThread(() -> {
                                            Toast.makeText(PayOutActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                            sendNotification();
                                        });
                                    }

                                    @Override
                                    public void onPaymentCanceled(String s, String s1) {
                                        Log.d("ZALO_PAY", "Thanh toán bị hủy");
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                        Log.e("ZALO_PAY", "Lỗi thanh toán: " + zaloPayError.toString());
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("loi", e.getMessage());
                            Toast.makeText(PayOutActivity.this, "vao cath", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
        binding.orderPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tthai_ttoan =  arrayThanhToan.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setUserData();
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
    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            String paymentResult = result.getData().getStringExtra("payment_result");
            if ("success".equals(paymentResult)) {
                Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED && result.getData() != null) {
            String paymentResult = result.getData().getStringExtra("payment_result");
            if ("canceled".equals(paymentResult)) {
                Toast.makeText(this, "Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
            } else if ("error".equals(paymentResult)) {
                String errorMessage = result.getData().getStringExtra("error_message");
                Toast.makeText(this, "Lỗi thanh toán: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    });
    private void placeOrder() {
        userId = mAuth.getCurrentUser().getUid();
        Long time = System.currentTimeMillis();
        String itemPushKey = database.child("OrderDetails").push().getKey();
        orderDetails = new OrderDetails(userId, name, address,totalPrice+"", phone, false, false,itemPushKey, time,cartList, resId );
        DatabaseReference orderRef = database.child("OrderDetails").child(resId).child(itemPushKey);
        orderRef.setValue(orderDetails).addOnSuccessListener(runnable ->{
                Toast.makeText(this, "orderSucces", Toast.LENGTH_SHORT).show();
                    bottomSheetFragment.show(getSupportFragmentManager(), "test");
                    removeItemFromCart();
                    addOrderToHistory();
                })
                .addOnFailureListener(runnable ->
                        Toast.makeText(this, "orderFail", Toast.LENGTH_SHORT).show()
                );
    }

    private void sendNotification() {
        if (resId != null) {
            DatabaseReference sendRef = FirebaseDatabase.getInstance().getReference().child("user").child(resId).child("token");
            sendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String token = snapshot.getValue(String.class);
                    Notification notification = new Notification("Bạn có đơn hàng mới!", "Thông báo");
                    Message message = new Message(token, notification);
                    dataMessage dataMessage = new dataMessage(message);
                    Log.d("Tokents", token);
                    if (token != null){
                        if (MainActivity.tokenSend != null){
                            Log.d("Messagetoken",token);
                            String bear = "Bearer " + MainActivity.tokenSend;
                            Log.d("bear", bear);
                            api.apiSendMessage.sendNotification(bear, dataMessage).enqueue(new Callback<SendMessageResponse>() {
                                @Override
                                public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("Notification", "Sent successfully");
                                        placeOrder();
                                    } else {
                                        Log.d("Notification", "Failed to send"+response);
                                    }
                                }
                                @Override
                                public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                                    Log.d("Notification", "Error: " + t.getMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void addOrderToHistory() {
        database.child("user").child(userId).child("BuyHistory").child(resId).child(orderDetails.getItemPushKey())
                .setValue(orderDetails)
                .addOnSuccessListener(runnable -> {

                });
    }

    private void removeItemFromCart() {
        DatabaseReference cartItemRef = database.child("user").child(userId).child("CartItems");
        cartItemRef.removeValue();
    }

    double getTotalPrice(List<CartItems> cartList) {
        double total = 0;
        for (CartItems item : cartList) {
            double price = Double.parseDouble(item.getFoodPrice());
            total += price * item.getFoodQuanity();
        }
        return total;
    }
    private void setUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
             String userId = user.getUid();
            DatabaseReference userRef = database.child("user").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String names = snapshot.child("userName").getValue() == null ? "" : snapshot.child("userName").getValue(String.class);
                        String addresss = snapshot.child("address").getValue() == null ? "" : snapshot.child("address").getValue(String.class);
                        String phones = snapshot.child("phone").getValue() == null ? "" : snapshot.child("phone").getValue(String.class);
                        binding.payoutName.setText(names+"");
                        binding.payoutAddress.setText(addresss+"");
                        binding.payoutPhone.setText(phones+"");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}