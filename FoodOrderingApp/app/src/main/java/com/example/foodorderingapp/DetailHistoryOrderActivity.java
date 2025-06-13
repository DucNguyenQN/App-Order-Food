package com.example.foodorderingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Adapter.BuyAgainAdapter;
import com.example.foodorderingapp.Adapter.DetailHistoryAdapter;
import com.example.foodorderingapp.databinding.ActivityDetailHistoryOrderBinding;
import com.example.foodorderingapp.model.CartItems;
import com.example.foodorderingapp.model.OrderDetails;
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

public class DetailHistoryOrderActivity extends AppCompatActivity {
    private ActivityDetailHistoryOrderBinding binding;
    private OrderDetails orderDetails;
    private BuyAgainAdapter adapter;
    private String nameOfRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailHistoryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        adapter = new BuyAgainAdapter(this);
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
            adapter.setData(cartItems, orderDetails.getResId(),nameOfRes);
            binding.recycleHis.setAdapter(adapter);
        }
    }

    private void setDataUser() {
        binding.txtName.setText(orderDetails.getUserName());
        binding.time.setText(convertTimestamp(orderDetails.getCurrentTime()));
        binding.txtPhone.setText(orderDetails.getPhoneNumber());
        binding.txtAddress.setText(orderDetails.getAddress());
        binding.txtTotal.setText(formatStringNumber(orderDetails.getTotalPrice()) +" VND");
        getNameRestaurance(orderDetails.getResId());
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
    public static String convertTimestamp(long firebaseTimestamp) {
        Date date = new Date(firebaseTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}