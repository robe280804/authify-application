package com.robertosodini.authify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Benvenuto nella nostra applicazione");
            message.setText("Ciao " + name + "\n\n Grazie per esserti registrato! \n\nAuthify Team");

            javaMailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password reset OTP");
        message.setText("Il tuo OTP per resettare la password è " + otp + ". Usa questo OTP per procedere con il reset della password");

        javaMailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verifica account con OTP");
        message.setText("Il tuo OTP è " + otp + ". Usa questo OTP per verificare il tuo account");

        javaMailSender.send(message);
    }
}
