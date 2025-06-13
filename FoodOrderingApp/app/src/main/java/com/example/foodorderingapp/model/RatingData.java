package com.example.foodorderingapp.model;

import androidx.annotation.NonNull;

public class RatingData {
    private String itemId;
    private float ratingValue;
    private String comment;

    public RatingData(String itemId, float ratingValue, String comment) {
        this.itemId = itemId;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public String toString() {
        return "RatingData{" +
                "itemId='" + itemId + '\'' +
                ", ratingValue=" + ratingValue +
                ", comment='" + comment + '\'' +
                '}';
    }
}
