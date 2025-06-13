package com.example.adminfoodorderingapp.model;
import java.io.Serializable;

public class CartItems implements Serializable {
    private String id;
    private String foodName;
    private String foodPrice;
    private String foodDescription;
    private String foodImage;
    private int foodQuanity;


    public CartItems() {
    }

    public CartItems(String id, String foodName, String foodPrice, String foodDescription, String foodImage, int foodQuanity) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
