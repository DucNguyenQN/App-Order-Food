package com.example.foodorderingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingapp.model.CreateOrder;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ZaloPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zalo_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String total = getIntent().getStringExtra("total");
        double totalDouble = Double.parseDouble(total);
        int totalInt = (int) Math.round(totalDouble);
        String totalString = Integer.toString(totalInt);
        Toast.makeText(this, totalString, Toast.LENGTH_SHORT).show();
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(totalString);
            Log.d("ZaloPay Response", data.toString());
            String code = data.getString("return_code");
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(ZaloPayActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("payment_result", "success");
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("payment_result", "canceled");
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        finish();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("payment_result", "error");
                        resultIntent.putExtra("error_message", zaloPayError.toString());
                        setResult(Activity.RESULT_CANCELED, resultIntent);
                        finish();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("loi", e.getMessage());
            Toast.makeText(ZaloPayActivity.this, "vao cath", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}