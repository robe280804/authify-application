package com.robertosodini.authify.model;

import jakarta.persistence.*;

@Entity
@Table(name = "login_history_tbl")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false, name = "user_ip")
    public String userIp;

    @Column(nullable = false, name = "user_agent")
    private String userAgent;

    @Column(nullable = false)
    private Boolean success;

    private String failureReason;

}
