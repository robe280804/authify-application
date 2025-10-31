package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.PasswordRequestDto;
import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;

public interface UserService {

    UserResponseDto createUser(UserRequestDto request);

    UserResponseDto getUserInfo(String email);

    void sendResetOtp(String email);

    void resetPassword(PasswordRequestDto request);


}
