package com.example.adminfoodorderingapp.model;

public class ChatMessage {
    private String senderId;
    private String message;
    private long timestamp;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String message, long timestamp, String type) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
