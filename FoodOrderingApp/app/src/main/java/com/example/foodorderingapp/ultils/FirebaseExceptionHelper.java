package com.example.foodorderingapp.ultils;

import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseExceptionHelper {

    public static String getErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) e;
            return getAuthErrorMessage(authException.getErrorCode());
        }
        return "Đã có lỗi xảy ra. Vui lòng thử lại.";
    }

    private static String getAuthErrorMessage(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                return "Invalid email";
            case "ERROR_WRONG_PASSWORD":
                return "Password is incorrect";
            case "ERROR_USER_NOT_FOUND":
                return "User does not exist.";
            case "ERROR_USER_DISABLED":
                return "This account has been disabled.";
            case "ERROR_EMAIL_ALREADY_IN_USE":
                return "Email already in use by another account";
            case "ERROR_WEAK_PASSWORD":
                return "Password is too weak. Choose a stronger password.";
            case "ERROR_TOO_MANY_REQUESTS":
                return "Too many requests. Please try again later..";
            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                return "Email has been registered using another method.";
            case "ERROR_INVALID_CREDENTIAL":
                return "The credentials are invalid or have expired.";
            default:
                return "Unknown error: " + errorCode;
        }
    }
}
