package com.example.adminfoodorderingapp.model;

import java.io.Serializable;
import java.util.List;

public class OrderDetails implements Serializable {
    private String userId;
    private String userName;
    private String address;
    private String totalPrice;
    private String phoneNumber;
    private Boolean orderAcceppted = false;
    private Boolean paymentReceived = false;
    private String itemPushKey;
    private Long currentTime = 0L;
    private List<CartItems> cartItems;

    public OrderDetails() {
    }

    public OrderDetails(String userId, String userName, String address, String totalPrice, String phoneNumber, Boolean orderAcceppted, Boolean paymentReceived, String itemPushKey, Long currentTime, List<CartItems> cartItems) {
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.totalPrice = totalPrice;
        this.phoneNumber = phoneNumber;
        this.orderAcceppted = orderAcceppted;
        this.paymentReceived = paymentReceived;
        this.itemPushKey = itemPushKey;
        this.currentTime = currentTime;
        this.cartItems = cartItems;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getOrderAcceppted() {
        return orderAcceppted;
    }

    public void setOrderAcceppted(Boolean orderAcceppted) {
        this.orderAcceppted = orderAcceppted;
    }

    public Boolean getPaymentReceived() {
        return paymentReceived;
    }

    public void setPaymentReceived(Boolean paymentReceived) {
        this.paymentReceived = paymentReceived;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    public String getItemPushKey() {
        return itemPushKey;
    }

    public void setItemPushKey(String itemPushKey) {
        this.itemPushKey = itemPushKey;
    }
}
