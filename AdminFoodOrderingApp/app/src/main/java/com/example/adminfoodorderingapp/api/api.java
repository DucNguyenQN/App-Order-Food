package com.example.adminfoodorderingapp.api;

import com.example.adminfoodorderingapp.model.SendMessageResponse;
import com.example.adminfoodorderingapp.model.dataMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface api {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    api apiService = new Retrofit.Builder()
            .baseUrl("https://api-cloud-y1oi.onrender.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(api.class);

    api apiSendMessage  = new Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/v1/projects/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api.class);

    @Multipart
    @POST("api/upload/image")
    Call<String> uploadImage(@Part MultipartBody.Part file);

    @POST("foodorderingapp-b8079/messages:send")
    Call<SendMessageResponse> sendNotification(
            @Header("Authorization") String header,
            @Body dataMessage message);
}
