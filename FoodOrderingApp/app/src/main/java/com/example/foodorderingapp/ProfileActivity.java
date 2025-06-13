package com.example.foodorderingapp;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodorderingapp.databinding.ActivityProfileBinding;
import com.example.foodorderingapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUserData();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.profileName.getText().toString();
                String address = binding.profileAddress.getText().toString();
                String email = binding.profileEmail.getText().toString();
                String phone = binding.profilePhone.getText().toString();
                UpdateProfile(userName, address, email, phone);
            }
        });
    }

    private void UpdateProfile(String userName, String address, String email, String phone) {
        String userId = mAuth.getCurrentUser().getUid();
        if (userId != null){
            DatabaseReference userRef = database.getReference("user").child(userId);
            UserModel userModel = new UserModel(userName, address, email, phone);
            userRef.setValue(userModel).addOnSuccessListener(runnable -> {
                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(runnable -> {
                Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        if (userId != null){
            DatabaseReference userRef = database.getReference("user").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        UserModel userprofile = snapshot.getValue(UserModel.class);
                        if (userprofile != null){
                            binding.profileName.setText(userprofile.getUserName());
                            binding.profileAddress.setText(userprofile.getAddress());
                            binding.profileEmail.setText(userprofile.getEmail());
                            binding.profilePhone.setText(userprofile.getPhone());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}