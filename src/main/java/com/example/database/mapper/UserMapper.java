package com.example.database.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.example.database.dto.request.UserCreationRequest;
import com.example.database.dto.request.UserUpdateRequest;
import com.example.database.dto.response.UserResponse;
import com.example.database.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(UserCreationRequest request);
    UserResponse toUserResponse(UserEntity userEntity);
    void updateUserEntity(@MappingTarget UserEntity userEntity, UserUpdateRequest request);
}
