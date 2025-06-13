package com.example.adminfoodorderingapp.model;

public class NotificationModel {
    private String title;
    private String body;
    private String userId;
    private long timestamp;

    public NotificationModel() {
    }
    public NotificationModel(String title, String body, String userId, long timestamp) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
