package com.example.foodorderingapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.FragmentProfileBinding;
import com.example.foodorderingapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUserData();
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
                Toast.makeText(getContext(), "Profile Update Successful", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(runnable -> {
                Toast.makeText(getContext(), "Profile Update Failed", Toast.LENGTH_SHORT).show();
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