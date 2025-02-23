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

import com.example.adminfoodorderingapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {
    TextView txtLogin;
    EditText edtSignName, edtSignEmail, edtSignPass, edtnameofrestaurant;
    Button btnSign;
    String UserName, email, password, NameOfRestaurant;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference database = db.getReference();
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
        mAuth = FirebaseAuth.getInstance();
        InnitView();
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignActivity.this, LoginActivity.class));
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserName = edtSignName.getText().toString().trim();
                email = edtSignEmail.getText().toString().trim();
                password = edtSignPass.getText().toString().trim();
                NameOfRestaurant = edtnameofrestaurant.getText().toString().trim();

                if (UserName.isEmpty() || email.isEmpty() || password.isEmpty() || NameOfRestaurant.isEmpty()){
                    Toast.makeText(SignActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }else {
                    createAccount(email, password);
                }
            }
        });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignActivity.this, "Accound Create Successful", Toast.LENGTH_SHORT).show();
                    saveUserData();
                    startActivity(new Intent(SignActivity.this, LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignActivity.this, "Accound Create Fail", Toast.LENGTH_SHORT).show();
                    Log.d("createFail", task.getException()+"");
                }
            }
        });
    }

    private void saveUserData() {
        UserName = edtSignName.getText().toString().trim();
        email = edtSignEmail.getText().toString().trim();
        password = edtSignPass.getText().toString().trim();
        NameOfRestaurant = edtnameofrestaurant.getText().toString().trim();

        UserModel userModel = new UserModel(UserName, email,password, NameOfRestaurant );
        String userId = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid());
        database.child("user").child(userId).setValue(userModel);
    }


    private void InnitView() {
        txtLogin = findViewById(R.id.txtLogin);
        edtSignName = findViewById(R.id.edtSignName);
        edtSignPass = findViewById(R.id.edtSignPass);
        edtSignEmail = findViewById(R.id.edtSignEmail);
        edtnameofrestaurant = findViewById(R.id.nameofrestaurant);
        btnSign = findViewById(R.id.btnSign);
    }
}