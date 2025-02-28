package com.example.database.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP to Reset Your Password");
        message.setText("Your OTP to reset your password is: " + otp);
        mailSender.send(message);
    }
    public void sendMailAlert(String toEmail, String type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        switch (type) {
            case "signin":
                message.setSubject("Chúc mừng bạn đăng ký thành công");
                message.setText("Chúc mừng bạn đăng ký thành công. Tài khoản của bạn đã có thể đăng nhập vào hệ thống");
                break;
            case "change_info":
                message.setSubject("Thay đổi thông tin thành công");
                message.setText("Chúc mừng bạn đã thay đổi thông tin thành công");
                break;
            case "change_password":
                message.setSubject("Thay đổi mật khẩu thành công");
                message.setText("Chúc mừng bạn đã thay đổi mật khẩu thành công");
                break;
            default:
                message.setSubject("Alert Iam");
                message.setText("Tài khoản của bạn đã thực hiện thay đổi thành công");
        }
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
