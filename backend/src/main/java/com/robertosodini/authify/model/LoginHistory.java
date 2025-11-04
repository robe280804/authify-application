package com.robertosodini.authify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "login_history_tbl")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(nullable = false, name = "user_ip")
    public String userIp;

    @Column(nullable = false, name = "user_agent")
    private String userAgent;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(nullable = false)
    private Boolean success;

    private String failureReason;

}
