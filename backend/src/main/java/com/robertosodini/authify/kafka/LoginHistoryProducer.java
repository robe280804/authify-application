package com.robertosodini.authify.kafka;

import com.robertosodini.authify.dto.LoginHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginHistoryProducer {

    private final KafkaTemplate<String, LoginHistoryDto> kafkaTemplate;

    // Invio al consumer
    public void sendLoginHistory(LoginHistoryDto loginHistory){
        kafkaTemplate.send("login-history-events", loginHistory)
                .whenComplete((res, ex) -> {
                    if (ex == null){
                        log.info("[LOGIN HISTORY PRODUCER] Evento login {} inviato con successo a {}",
                                loginHistory, res.getRecordMetadata().topic());
                    } else {
                        log.error("[LOGIN HISTORY PRODUCER] Errore durante l'invio ", ex);
                    }
                });
    }
}
