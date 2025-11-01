package com.robertosodini.authify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "verify_otp_tbl")
public class VerifyOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Evito di caricare l'utente
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false, name = "otp")
    private String verifyAccountOtp;  // hashato
    private LocalDateTime expiryOtp;
    private Boolean revoked;
}
