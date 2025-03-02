package com.example.database.service;

import com.example.database.entity.UserActivityLog;
import com.example.database.entity.UserEntity;
import com.example.database.repository.UserActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserActivityLogService {
    private final UserActivityLogRepository logRepository;

    public void logActivity(UserEntity userEntity, String activity, HttpServletRequest request) {
        UserActivityLog log = UserActivityLog.builder()
                .userId(userEntity.getId())
                .activity(activity)
                .createdAt(LocalDateTime.now())
                .build();

        logRepository.save(log);
    }
}
