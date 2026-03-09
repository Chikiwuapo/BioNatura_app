package com.goku1.bionatura.models;

public class PasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;

    public PasswordUpdateRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
