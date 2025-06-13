package com.example.adminfoodorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity"; // Tag để lọc log
    TextView txtSign, txtOTP;
    EditText edtLoginEmail, edtLoginPass;
    Button btnLogin;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        InnitView();
        txtSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtLoginEmail.getText().toString().trim();
                password = edtLoginPass.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password);
                }
            }
        });
        txtOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginWithPhoneActivity.class));
            }
        });
    }

    private void Login(String email, String password) {
        Log.d(TAG, "Đang cố gắng đăng nhập với email: " + email); // Log để theo dõi email

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Đăng nhập thành công!");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUi(user);
                } else {
                    Log.w(TAG, "Đăng nhập thất bại.", task.getException()); // Log lỗi đầy đủ

                    String errorMessage = "Đã xảy ra lỗi khi đăng nhập.";
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = "Tài khoản không tồn tại.";
                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Sai email hoặc mật khẩu.";
                    } else if (exception instanceof FirebaseNetworkException) {
                        errorMessage = "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối của bạn.";
                    } else if (exception != null) {
                        errorMessage = "Lỗi: " + exception.getMessage(); // Lấy thông báo lỗi cụ thể nếu có
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUi(FirebaseUser user) {
        Log.d(TAG, "Cập nhật UI sau khi đăng nhập thành công, UID: " + user.getUid());
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void InnitView() {
        txtSign = findViewById(R.id.txtSign);
        txtOTP = findViewById(R.id.txtOTP);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPass = findViewById(R.id.edtLoginPass);
        btnLogin = findViewById(R.id.btnLogin);

    }
}