package com.example.tryJwt.demo.FileRequest.Request;

public record ChangePasswordRequest(String oldPassword, String newPassword, String repeatNewPassword) {
}
