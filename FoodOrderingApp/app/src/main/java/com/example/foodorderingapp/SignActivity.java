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
import com.example.foodorderingapp.ultils.FirebaseExceptionHelper;
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
    Button btnSign;
    String userName, email, password;
    FirebaseAuth mAuth;
    DatabaseReference database;
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
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = edtSignName.getText().toString().trim();
                email = edtSignEmail.getText().toString().trim();
                password = edtSignPass.getText().toString().trim();
                if (userName.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }else{
                    CreateAcount(email, password);
                }
            }
        });


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignActivity.this, LoginActivity.class));
            }
        });
    }

    private void CreateAcount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                    saveUserData();
                    startActivity(new Intent(SignActivity.this, LoginActivity.class));
                    finish();
                }else {
                    String errorMessage = FirebaseExceptionHelper.getErrorMessage(task.getException());
                    Toast.makeText(SignActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
    }
}