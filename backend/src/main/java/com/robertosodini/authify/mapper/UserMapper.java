package com.robertosodini.authify.mapper;

import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder encoder;

    public UserModel convertToUserEntity(UserRequestDto request){
        return UserModel.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .isAccountVerify(false)
                .build();
    }

    public UserResponseDto convertToUserResponse(UserModel userModel){
        return UserResponseDto.builder()
                .userId(userModel.getUserId())
                .name(userModel.getName())
                .email(userModel.getEmail())
                .isAccountVerified(userModel.getIsAccountVerify())
                .build();
    }
}
