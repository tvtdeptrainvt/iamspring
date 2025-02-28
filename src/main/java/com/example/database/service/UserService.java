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

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    EmailService emailService;
    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> otpStorage = new HashMap<>();

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
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otp = generateOtp();
        otpStorage.put(email, otp);
        sendOtpEmail(email, otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        if (!validateOtp(email, otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStorage.remove(email);
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password OTP");
        message.setText("Your OTP to reset password is: " + otp);
        mailSender.send(message);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return otp.equals(storedOtp);
    }

    public void updatePassword(String email, String newPassword) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }
}
