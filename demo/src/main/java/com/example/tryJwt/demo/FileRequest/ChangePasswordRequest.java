package com.example.tryJwt.demo.FileRequest;

public record ChangePasswordRequest(String oldPassword, String newPassword, String repeatNewPassword) {
}
