package com.example.foodorderingapp.model;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private String foodName;
    private String foodPrice;
    private String foodDescription;
    private String foodImage;
    private String foodIngredients;

    public MenuItem() {
    }

    public MenuItem(String foodName, String foodPrice, String foodDescription, String foodImageUrl, String foodIngredients) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.foodImage = foodImageUrl;
        this.foodIngredients = foodIngredients;
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

    public String getFoodIngredients() {
        return foodIngredients;
    }

    public void setFoodIngredients(String foodIngredients) {
        this.foodIngredients = foodIngredients;
    }
}
