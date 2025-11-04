package com.robertosodini.authify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginHistoryDto {

    private String userEmail;
    private String userIp;
    private String userAgent;
    private LocalDateTime loginTime;
    private Boolean success;
    private String failureReason;
}
