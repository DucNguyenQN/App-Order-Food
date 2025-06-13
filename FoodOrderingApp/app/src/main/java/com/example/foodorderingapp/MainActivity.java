package com.example.foodorderingapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodorderingapp.Fragment.CartFragment;
import com.example.foodorderingapp.Fragment.HistoryFragment;
import com.example.foodorderingapp.Fragment.HomeFragment;
import com.example.foodorderingapp.Fragment.SettingFragment;
import com.example.foodorderingapp.Fragment.SearchFragment;
import com.example.foodorderingapp.Service.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private String token, userId;
    private FirebaseAuth mAuth;
    public static String tokenSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        InnitView();
        InnitAccessToken();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.homeFragment) {
                    replaceFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.cartFragment) {
                    replaceFragment(new CartFragment());
                }
                else if (item.getItemId() == R.id.searchFragment) {
                    replaceFragment(new SearchFragment());
                }
                else if (item.getItemId() == R.id.historyFragment) {
                    replaceFragment(new HistoryFragment());
                }
                else if (item.getItemId() == R.id.profileFragment) {
                    replaceFragment(new SettingFragment());
                }
                return true;
            }
        });
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
                    Log.d("DEBUG", "UserId: " + userId + " | token: " + token);
                });
    }
    private void saveToken(String token) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("token");
        tokenRef.setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Token saved successfully", Toast.LENGTH_SHORT).show();
                Log.d("Save Tokent: ",token+"");
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
    private void InnitView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}