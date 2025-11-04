package com.robertosodini.authify.kafka;

import com.robertosodini.authify.dto.LoginHistoryDto;
import com.robertosodini.authify.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryConsumer {

    private final LoginHistoryService loginHistoryService;

    @KafkaListener(topics = "login-history-events", groupId = "login-group")
    public void loginHistoryConsumer(LoginHistoryDto loginHistoryDto){
        try {
            loginHistoryService.save(loginHistoryDto);
            log.info("[LOGIN HISTORY CONSUMER] Attività ricevuta e salvata con successo");

        } catch (Exception e){
            log.error("[LOGIN HISTORY CONSUMER] Errore elaborando l'evento {}", loginHistoryDto, e);
        }

    }
}
