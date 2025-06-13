package com.example.adminfoodorderingapp;

import static android.content.ContentValues.TAG;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adminfoodorderingapp.Service.AccessToken;
import com.example.adminfoodorderingapp.databinding.ActivityMainBinding;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formattable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String resId;
    public static String token;
    public static String tokenSend;
    private DatabaseReference completeOrderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            resId = mAuth.getCurrentUser().getUid();
        } else {
            Log.e(TAG, "No user is currently signed in.");
            resId = null; // Or handle this case appropriately
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
        InnitControl();
        InnitAccessToken();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        if (exception instanceof java.util.concurrent.ExecutionException &&
                                exception.getCause() instanceof java.io.IOException &&
                                "SERVICE_NOT_AVAILABLE".equals(exception.getCause().getMessage())) {
                            Log.w(TAG, "FCM service not available. Retrying...");
                            // Retry logic can be implemented here if needed
                        } else {
                            Log.w(TAG, "Fetching FCM registration token failed", exception);
                        }
                        return;
                    }

                    // Token retrieved successfully
                    token = task.getResult();
                    Log.d(TAG, "Token: " + token);
                    if (token != null) {
                        saveToken(token);
                    } else {
                        //Toast.makeText(this, "Token: null", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("DEBUG", "resId: " + resId + " | token: " + token);
                });
    }
    private void saveToken(String token) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("user").child(resId).child("token");
        tokenRef.setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(MainActivity.this, "Token saved successfully", Toast.LENGTH_SHORT).show();
                Log.d("Save Token: ",token+"");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failed to save token: ", e.getMessage());
            }
        });
    }
    private void InnitAccessToken() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AccessToken accessToken = new AccessToken();
                tokenSend = accessToken.getAccessToken();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("tokent: ", tokenSend);
                    }
                });

            }
        });
    }

    private void InnitControl() {
        binding.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddItemsActivity.class));
            }
        });
        binding.cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AllItemActivity.class));
            }
        });
        binding.outFrDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OutForDeliveryActivity.class));
            }
        });
        binding.cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdminProfileActivity.class));
            }
        });
        binding.cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateUserActivity.class));
            }
        });
        binding.pendingOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PendingOrderActivity.class));
            }
        });
        binding.cardView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FirebaseMessaging.getInstance().deleteToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Logout", "Token deleted successfully");
                            } else {
                                Log.e("Logout", "Failed to delete token");
                            }
                        });
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        binding.cardView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatListActivity.class));
            }
        });
        binding.cardView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StatisticActivity.class));
            }
        });
        pendingOrders();
        completeOrders();
        wholeTimeEarning();
    }

    private void wholeTimeEarning() {
        List<Integer> listOfTotalPay = new ArrayList<>();
        completeOrderRef = FirebaseDatabase.getInstance().getReference().child("CompleteOrder").child(resId);;
        completeOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ordersnapshot : snapshot.getChildren()){
                    OrderDetails completeOrder  = ordersnapshot.getValue(OrderDetails.class);
                    if (completeOrder != null){
                        if (completeOrder.getTotalPrice() != null){
                            double doubleNumber = Double.parseDouble(completeOrder.getTotalPrice());
                            listOfTotalPay.add((int) doubleNumber);
                        }
                    }
                }
                int sum = 0;
                for (int num : listOfTotalPay) {
                    sum += num;
                }
                binding.whole.setText(formatStringNumber(String.valueOf(sum)) + " VND");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    private void completeOrders() {
        database   = FirebaseDatabase.getInstance();
        DatabaseReference completeOrderRefe = database.getReference().child("CompleteOrder").child(resId);;
        completeOrderRefe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.completeOrder.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void pendingOrders() {
        database   = FirebaseDatabase.getInstance();
        DatabaseReference pendingOrderRef = database.getReference().child("OrderDetails").child(resId);
        pendingOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.pendingOrderCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pendingOrders();
        completeOrders();
        wholeTimeEarning();
    }
}