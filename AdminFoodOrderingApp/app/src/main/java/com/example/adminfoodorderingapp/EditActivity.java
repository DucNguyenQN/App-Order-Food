package com.example.adminfoodorderingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adminfoodorderingapp.databinding.ActivityEditBinding;
import com.example.adminfoodorderingapp.model.AllMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    private AllMenu menutem;
    private ActivityEditBinding binding;
    private String userId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding  = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        menutem = (AllMenu) bundle.get("itemDetail");
        binding.enterfoodname.setText(menutem.getFoodName());
        binding.enterfoodprice.setText(menutem.getFoodPrice());
        binding.description.setText(menutem.getFoodDescription());
        binding.editText.setText(menutem.getFoodIngredients());
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditActivity.this, "id: "+menutem.getId(), Toast.LENGTH_SHORT).show();
                updateFoodToFirebase(menutem.getId());
                finish();
            }
        });
    }
    private void updateFoodToFirebase(String foodId) {
        String name = binding.enterfoodname.getText().toString().trim();
        String price = binding.enterfoodprice.getText().toString().trim();
        String description = binding.description.getText().toString().trim();
        String ingredients = binding.editText.getText().toString().trim();


        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menu").child(foodId);
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("user").child(userId).child("menu").child(foodId);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("foodName", name);
        updatedData.put("foodPrice", price);
        updatedData.put("foodDescription", description);
        updatedData.put("foodIngredients", ingredients);


        ref.updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        restaurantRef.updateChildren(updatedData).addOnSuccessListener(aVoid -> {
            // Cập nhật thành công trong danh sách menu của nhà hàng
            Toast.makeText(this, "Cập nhật thành công trong danh sách menu của nhà hàng", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Xử lý lỗi nếu có
            Toast.makeText(this, "Lỗi khi cập nhật trong danh sách menu của nhà hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }
}