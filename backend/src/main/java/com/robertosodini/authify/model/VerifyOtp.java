package com.robertosodini.authify.model;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "verify_otp_tbl")
public class VerifyOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "otp")
    private String verifyAccountOtp;  // hashato
    private LocalDateTime expiryOtp;
    private Boolean revoked;
}
