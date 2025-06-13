package com.example.adminfoodorderingapp.model;

public class ChatPreview {
    private String chatId;
    private String otherUserId;
    private String lastMessage;
    private long lastTimestamp;

    public ChatPreview(String chatId, String otherUserId, String lastMessage, long lastTimestamp) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }
}
