package com.example.foodorderingapp.model;

public class RatingResult {
    private String itemId;
    private float rating;
    private String comment;

    public RatingResult(String itemId, float rating, String comment) {
        this.itemId = itemId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getItemId() { return itemId; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
}
