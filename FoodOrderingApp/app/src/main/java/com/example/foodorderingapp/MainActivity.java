package com.example.foodorderingapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.example.foodorderingapp.Fragment.CartFragment;
import com.example.foodorderingapp.Fragment.HistoryFragment;
import com.example.foodorderingapp.Fragment.HomeFragment;
import com.example.foodorderingapp.Fragment.ProfileFragment;
import com.example.foodorderingapp.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
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
        InnitView();
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
                    replaceFragment(new ProfileFragment());
                }
                return true;
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