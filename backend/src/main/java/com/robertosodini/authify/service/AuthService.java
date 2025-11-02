package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.dto.OtpDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    String sendOtp(String email);


    void verifyOtp(OtpDto otp, String email);

    String logout(String email, HttpServletResponse response);
}