package com.example.database.service;


import com.example.database.dto.request.*;
import com.example.database.dto.response.UserResponse;
import com.example.database.entity.UserEntity;
import com.example.database.enums.Role;
import com.example.database.exception.AppException;
import com.example.database.exception.ErrorCode;
import com.example.database.mapper.UserMapper;
import com.example.database.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public UserEntity createUser(UserCreationRequest request) {

        // khi tao user neu user da ton tai thi no se tra ve " user have "
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        UserEntity userEntity = userMapper.toUserEntity(request);     // ma hoa mat khau Bcrypt
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        userEntity.setRoles(roles);

        return userRepository.save(userEntity);
    }

    public UserResponse updateUserEntity(int userId, UserUpdateRequest request) {


        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserEntity not fond"));

        userMapper.updateUserEntity(userEntity, request);

        return userMapper.toUserResponse(userRepository.save(userEntity));

    }

    public void deleteUserEntity(int userId) {
        userRepository.deleteById(userId);
    }

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUserEntity(int id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserEntity not fond")));
    }
    public void changePassword(int userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD); // Ném lỗi nếu mật khẩu cũ không khớp
        }

        // Mã hóa mật khẩu mới và lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public void forgotPassword(ForgotPasswordRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String resetToken = UUID.randomUUID().toString();
        userEntity.setResetToken(resetToken);
        userRepository.save(userEntity);

        sendResetPasswordEmail(userEntity.getEmail(), resetToken);
    }

    private void sendResetPasswordEmail(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("To reset your password, click the link below:\n"
                + "http://localhost:8080/database/auth/reset-password?token=" + resetToken);
        mailSender.send(message);
    }
    public void resetPassword(ResetPasswordRequest request) {
        UserEntity userEntity = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));

        userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userEntity.setResetToken(null);
        userRepository.save(userEntity);
    }
}
