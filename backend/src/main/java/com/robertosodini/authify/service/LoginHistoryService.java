package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.LoginHistoryDto;
import com.robertosodini.authify.model.LoginHistory;
import com.robertosodini.authify.repository.LoginHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    /// Creo DTO login history
    public LoginHistoryDto create(HttpServletRequest request, String email){
        String userAgent = request.getHeader("User-Agent");
        String userIp = request.getLocalAddr();

        return LoginHistoryDto.builder()
                .userEmail(email)
                .userAgent(userAgent)
                .userIp(userIp)
                .loginTime(LocalDateTime.now())
                .success(false)
                .build();
    }

    ///  Salvataggio del DTO nel db (conversione al modello)
    public void save(LoginHistoryDto loginHistoryDto){
        LoginHistory loginHistory = LoginHistory.builder()
                .userEmail(loginHistoryDto.getUserEmail())
                .userIp(loginHistoryDto.getUserIp())
                .userAgent(loginHistoryDto.getUserAgent())
                .loginTime(loginHistoryDto.getLoginTime())
                .success(loginHistoryDto.getSuccess())
                .failureReason(loginHistoryDto.getFailureReason())
                .build();

        loginHistoryRepository.save(loginHistory);
    }
}
