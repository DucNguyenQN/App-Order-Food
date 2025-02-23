package com.example.foodorderingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CartItems implements Serializable {
    private String foodName;
    private String foodPrice;
    private String foodDescription;
    private String foodImage;
    private int foodQuanity;

    public CartItems() {
    }

    public CartItems(String foodName, String foodPrice, String foodDescription, String foodImage, int foodQuanity) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.foodImage = foodImage;
        this.foodQuanity = foodQuanity;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public int getFoodQuanity() {
        return foodQuanity;
    }

    public void setFoodQuanity(int foodQuanity) {
        this.foodQuanity = foodQuanity;
    }
}
