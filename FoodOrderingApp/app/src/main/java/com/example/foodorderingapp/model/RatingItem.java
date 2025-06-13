package com.example.foodorderingapp.model;

public class RatingItem {
    private float ratingValue;
    private String comment;
    private String userId;
    private Long timestamp;

    public RatingItem() {
    }

    public RatingItem(float ratingValue, String comment, String userId, Long timestamp) {
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public float getRatingValue() { return ratingValue; }
    public void setRatingValue(float ratingValue) { this.ratingValue = ratingValue; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
