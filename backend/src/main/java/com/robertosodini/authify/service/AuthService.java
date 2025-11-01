package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.dto.OtpDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    String sendOtp(String email);


    void verifyOtp(OtpDto otp, String email);

}