package com.example.adminfoodorderingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adminfoodorderingapp.adapter.DetailsDeliveryAdapter;
import com.example.adminfoodorderingapp.databinding.ActivityDetailsDeliveryBinding;
import com.example.adminfoodorderingapp.model.CartItems;
import com.example.adminfoodorderingapp.model.OrderDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsDeliveryActivity extends AppCompatActivity {
    private ActivityDetailsDeliveryBinding binding;
    private OrderDetails orderDetails;
    private String nameOfRes, id;
    private DetailsDeliveryAdapter adapter;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailsDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();
        adapter = new DetailsDeliveryAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recycleHis.setLayoutManager(linearLayoutManager);
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
        orderDetails = (OrderDetails) bundle.get("itemDetail");
        setDataUser();
        setDataInRycycle();
    }
    private void setDataInRycycle() {
        List<CartItems> cartItems = orderDetails.getCartItems();
        if (cartItems != null && !cartItems.isEmpty()){
            adapter.setData(cartItems);
            binding.recycleHis.setAdapter(adapter);
        }
    }
    private void setDataUser() {
        binding.txtName.setText(orderDetails.getUserName());
        binding.time.setText(convertTimestamp(orderDetails.getCurrentTime()));
        binding.txtPhone.setText(orderDetails.getPhoneNumber());
        binding.txtAddress.setText(orderDetails.getAddress());
        binding.txtTotal.setText(formatStringNumber(orderDetails.getTotalPrice()) +" VND");
        getNameRestaurance(id);
    }
    public static String convertTimestamp(long firebaseTimestamp) {
        Date date = new Date(firebaseTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
    private String formatStringNumber(String numberString) {
        try {
            double number = Double.parseDouble(numberString);
            int roundedNumber = (int) Math.round(number); // Làm tròn và ép int
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            return formatter.format(roundedNumber);
        } catch (NumberFormatException e) {
            Log.e("Format Error", "Không thể chuyển đổi chuỗi thành số: " + numberString);
            return numberString;
        }
    }
    private void getNameRestaurance(String res){
        DatabaseReference resRef = FirebaseDatabase.getInstance().getReference().child("user").child(res).child("namofresturant");
        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameOfRes = snapshot.getValue(String.class);
                    binding.nameofRes.setText(nameOfRes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };
}