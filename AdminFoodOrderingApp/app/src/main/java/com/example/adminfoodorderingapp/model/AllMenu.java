package com.example.adminfoodorderingapp.model;

import java.io.Serializable;

public class AllMenu implements Serializable {
    String id;
    String foodName;
    String foodPrice;
    String foodDescription;
    String foodIngredients;
    String foodImage;
    String resId;
    String nameOfRestaurance;

    public AllMenu() {
    }

    public AllMenu(String id, String foodName, String foodPrice, String foodDescription, String foodIngredients, String foodImage, String resId, String nameOfRestaurance) {
        this.id = id;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.foodIngredients = foodIngredients;
        this.foodImage = foodImage;
        this.resId = resId;
        this.nameOfRestaurance = nameOfRestaurance;
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

    public String getFoodIngredients() {
        return foodIngredients;
    }

    public void setFoodIngredients(String foodIngredients) {
        this.foodIngredients = foodIngredients;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getNameOfRestaurance() {
        return nameOfRestaurance;
    }

    public void setNameOfRestaurance(String nameOfRestaurance) {
        this.nameOfRestaurance = nameOfRestaurance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
