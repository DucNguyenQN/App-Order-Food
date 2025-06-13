package com.example.adminfoodorderingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adminfoodorderingapp.databinding.ActivityStatisticBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class StatisticActivity extends AppCompatActivity {
    private String id;
    private FirebaseAuth mAuth;
    private ActivityStatisticBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStatisticBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();
        //fetchRevenueAndShowChart("dd/MM/yyyy");
        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Theo ngày", "Theo tháng"));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    fetchRevenueAndShowChart("dd/MM/yyyy"); // Theo ngày
                } else {
                    fetchRevenueAndShowChart("MM/yyyy"); // Theo tháng
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //fetchRevenueAndShowChart("MM/yyyy");
    }
    private void fetchRevenueAndShowChart(String pattern) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("CompleteOrder").child(id);

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> revenueMap = new TreeMap<>();

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    // 1. Lấy totalPrice dạng String, parse sang double
                    String totalPriceStr = orderSnap.child("totalPrice").getValue(String.class);
                    if (totalPriceStr == null) continue;

                    double totalAmount;
                    try {
                        totalAmount = Double.parseDouble(totalPriceStr);
                    } catch (NumberFormatException e) {
                        Log.e("ParseError", "Không thể parse totalPrice: " + totalPriceStr);
                        continue;
                    }

                    // 2. Lấy currentTime dạng Long
                    Long currentTimeLong = orderSnap.child("currentTime").getValue(Long.class);
                    if (currentTimeLong == null) continue;

                    long timestamp = currentTimeLong;
                    String dateKey = getFormattedDate(timestamp, pattern);
                    Log.d("DateKeyCheck", "timestamp=" + timestamp + ", formatted=" + dateKey);

                    // 3. Cộng dồn doanh thu
                    int currentTotal = revenueMap.getOrDefault(dateKey, 0);
                    revenueMap.put(dateKey, currentTotal + (int) totalAmount);
                    Log.d("OrderSnap", orderSnap.getKey() + " - currentTime: " + orderSnap.child("currentTime").getValue());
                }

                Log.d("DoanhThu", revenueMap.toString());
                showBarChart(revenueMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DoanhThu", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }


    private long parseCurrentTimeToTimestamp(String currentTimeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(currentTimeStr);
            Log.d("ParseTime", "currentTimeStr = " + currentTimeStr);
            return date != null ? date.getTime() : 0;

        } catch (Exception e) {
            Log.e("ParseError", "Lỗi parse thời gian: " + e.getMessage());
            return 0;
        }

    }

    private void showBarChart(Map<String, Integer> revenueMap) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : revenueMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColor(Color.parseColor("#FFA726"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);


        binding.barChart.setData(barData);
        binding.barChart.setFitBars(true);
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }
    public static String getFormattedDate(long timestamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}