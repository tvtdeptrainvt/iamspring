package com.example.database.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token; // Mã xác nhận hoặc token
    private String newPassword; // Mật khẩu mới
}
