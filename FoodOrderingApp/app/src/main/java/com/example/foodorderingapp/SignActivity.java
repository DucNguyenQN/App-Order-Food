package com.example.foodorderingapp;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingapp.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class SignActivity extends AppCompatActivity {
    TextView txtLogin;
    EditText edtSignName, edtSignEmail, edtSignPass;
    Button btnSign, btnSignGoogle;
    String userName, email, password;
    FirebaseAuth mAuth;
    DatabaseReference database;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InnitView();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Thêm ID client của Firebase
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = edtSignName.getText().toString().trim();
                email = edtSignEmail.getText().toString().trim();
                password = edtSignPass.getText().toString().trim();
                if (userName.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }else{
                    CreateAcount(email, password);
                }
            }
        });
        btnSignGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signIntent, 9001);
            }
        });;

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignActivity.this, LoginActivity.class));
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
                        // Đăng nhập thành công, chuyển hướng người dùng
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(SignActivity.this, MainActivity.class));
                        finish();
                        // Xử lý người dùng, ví dụ: chuyển đến màn hình chính
                        Toast.makeText(this, "Thanh cong", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý lỗi đăng nhập
                    }
                });
    }
    private void CreateAcount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignActivity.this, "Account Create Successful", Toast.LENGTH_SHORT).show();
                    saveUserData();
                    startActivity(new Intent(SignActivity.this, LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignActivity.this, "Account Create Fail", Toast.LENGTH_SHORT).show();
                    Log.d("account", task.getException()+"");
                }
            }
        });
    }

    private void saveUserData() {
        userName = edtSignName.getText().toString();
        email = edtSignEmail.getText().toString().trim();
        password = edtSignPass.getText().toString().trim();
        UserModel user = new UserModel(userName,email,password);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("user").child(userId).setValue(user);
    }

    private void InnitView() {
        txtLogin = findViewById(R.id.txtLogin);
        edtSignName = findViewById(R.id.edtSignName);
        edtSignEmail = findViewById(R.id.edtSignEmail);
        edtSignPass = findViewById(R.id.edtSignPass);
        btnSign = findViewById(R.id.btnSign);
        btnSignGoogle = findViewById(R.id.btnSignGoogle);
    }
}