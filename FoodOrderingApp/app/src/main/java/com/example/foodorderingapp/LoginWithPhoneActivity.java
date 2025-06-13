package com.example.foodorderingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingapp.databinding.ActivityLoginWithPhoneBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginWithPhoneActivity extends AppCompatActivity {
    private ActivityLoginWithPhoneBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId;
    private Dialog dialog;
    private Button btnCancel, btnVerify;
    private EditText edtOTP;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginWithPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ///Dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_otp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);
        ///Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Thêm ID client của Firebase
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnVerify = dialog.findViewById(R.id.btnVerify);
        edtOTP = dialog.findViewById(R.id.edtOTP);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        binding.btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginWithPhoneActivity.this, LoginActivity.class));
            }
        });
        binding.btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = binding.edtphone.getText().toString().trim();
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(LoginWithPhoneActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!phoneNumber.startsWith("+84")) {
                    phoneNumber = "+84" + phoneNumber.substring(1); // Chuyển 0xxx => +84xxx
                }

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                                .setActivity(LoginWithPhoneActivity.this)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                        signInWithPhoneAuthCredential(credential);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(LoginWithPhoneActivity.this, "Lỗi gửi mã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                        super.onCodeSent(id, token);
                                        verificationId = id;
                                        Toast.makeText(LoginWithPhoneActivity.this, "Mã OTP đã gửi!", Toast.LENGTH_SHORT).show();
                                        dialog.show();
                                    }
                                })
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
        ///LoginWithGoogle
        binding.btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signIntent, 9001);
            }
        });
        //Dialog
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = edtOTP.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(LoginWithPhoneActivity.this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (verificationId != null) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Xử lý lỗi
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveGoogleUser(user);
                        Toast.makeText(this, "email"+user.getEmail(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Đăng nhập thành công với tài khoản Google: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý lỗi đăng nhập
                    }
                });
    }
    private void saveGoogleUser(FirebaseUser user){
        if (user == null) return;

        String uid = user.getUid();
        String email = user.getEmail();
        if (email == null) {
            Log.e("FirebaseUser", "Email null, không thể lưu.");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Người dùng chưa tồn tại -> lưu mới
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("email", email);
                    userRef.setValue(data).addOnSuccessListener(aVoid -> {
                        Log.d("DB", "Đã lưu user mới vào Realtime Database.");
                        startActivity(new Intent(LoginWithPhoneActivity.this, MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Log.e("DB", "Lỗi khi lưu user: " + e.getMessage());
                    });
                } else {
                    Log.d("DB", "User đã tồn tại, không lưu lại.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB", "Lỗi Database: " + error.getMessage());
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(LoginWithPhoneActivity.this, "Đăng nhập thành công với số: " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                        updateUi(user);
                    } else {
                        Toast.makeText(LoginWithPhoneActivity.this, "Xác minh OTP thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUi(FirebaseUser user) {
        if (user == null) return;

        String uid = user.getUid();
        String phone = convertToVietnamesePhoneNumber(user.getPhoneNumber());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Người dùng chưa tồn tại -> lưu mới
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("phone", phone);
                    userRef.setValue(data).addOnSuccessListener(aVoid -> {
                        Log.d("DB", "Đã lưu user mới vào Realtime Database.");
                    }).addOnFailureListener(e -> {
                        Log.e("DB", "Lỗi khi lưu user: " + e.getMessage());
                    });
                } else {
                    Log.d("DB", "User đã tồn tại, không lưu lại.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB", "Lỗi Database: " + error.getMessage());
            }
        });


        startActivity(new Intent(LoginWithPhoneActivity.this, MainActivity.class));
        finish();
    }
    public static String convertToVietnamesePhoneNumber(String internationalNumber) {
        if (internationalNumber == null || internationalNumber.isEmpty()) {
            return ""; // Trả về chuỗi rỗng hoặc xử lý theo ý bạn
        }

        String cleanedNumber = internationalNumber.replaceAll("[\\s-()_.]", "");

        if (cleanedNumber.startsWith("+84")) {
            return "0" + cleanedNumber.substring(3);
        } else if (cleanedNumber.startsWith("84")) {
            return "0" + cleanedNumber.substring(2);
        } else if (cleanedNumber.startsWith("0")) {
            return cleanedNumber;
        } else {
            return internationalNumber; // Hoặc trả về chuỗi rỗng nếu không khớp
        }
    }
}