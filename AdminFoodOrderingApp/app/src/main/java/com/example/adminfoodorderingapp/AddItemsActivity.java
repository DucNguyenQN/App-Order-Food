 package com.example.adminfoodorderingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adminfoodorderingapp.api.api;
import com.example.adminfoodorderingapp.model.AllMenu;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

 public class AddItemsActivity extends AppCompatActivity {
    EditText enterFoodName, enterFoodPrice, description, Ingredients;
    TextView selectImage;
    ImageView selectedImage;
    ImageButton backButton;
    Button btnAddItem;
    Uri mUri;
    private ActivityResultLauncher<String> imagePickerLauncher;
     FirebaseAuth mAuth;
     FirebaseDatabase db = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        InnitView();
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImage.setImageURI(uri);
                        mUri = uri;
                    }
                }
        );
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerLauncher.launch("image/*");
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUri == null) {
                    Toast.makeText(AddItemsActivity.this, "Please select photo", Toast.LENGTH_SHORT).show();
                }else {
                    uploadImage();
                }
            }
        });
    }
    private void uploadImage(){
        String strRealPath = RealPathUtil.getRealPath(this, mUri);
        File file = new File(strRealPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part multipartbody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        api.apiService.uploadImage(multipartbody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    String foodImage = response.body();
                    String foodName = enterFoodName.getText().toString().trim();
                    String foodPrice = enterFoodPrice.getText().toString().trim();
                    String foodDescription = description.getText().toString().trim() ;
                    String foodIngredients = Ingredients.getText().toString().trim();
                    if (foodName.isEmpty() || foodPrice.isEmpty() || foodDescription.isEmpty() || foodIngredients.isEmpty() || foodImage.isEmpty()){
                        Toast.makeText(AddItemsActivity.this, "Fill all the details", Toast.LENGTH_SHORT).show();
                    }else{
                        uploadData(foodName,foodPrice,foodDescription,foodIngredients,foodImage);
                        //Toast.makeText(AddItemsActivity.this, "Upload Data Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(AddItemsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }
     private void uploadData(String foodName,String foodPrice,String foodDescription, String foodIngredients, String foodImage) {
         DatabaseReference menuRef = db.getReference("menu");
         String newItemKey = menuRef.push().getKey();
         AllMenu newItem = new AllMenu(foodName,foodPrice,foodDescription,foodIngredients,foodImage);
         menuRef.child(newItemKey).setValue(newItem).addOnSuccessListener(unused ->
                 Toast.makeText(this, "Upload Data Successful", Toast.LENGTH_SHORT).show()
         ).addOnFailureListener(e -> Toast.makeText(this, "Upload Data Fail: " + e.getMessage(), Toast.LENGTH_SHORT).show());
     }

     private void InnitView() {
        enterFoodName = findViewById(R.id.enterfoodname);
        enterFoodPrice = findViewById(R.id.enterfoodprice);
        selectImage = findViewById(R.id.selectImage);
        selectedImage = findViewById(R.id.selectedImage);
        backButton = findViewById(R.id.backButton);
        btnAddItem = findViewById(R.id.addItemButton);
        description = findViewById(R.id.description);
        Ingredients = findViewById(R.id.editText);
    }
}