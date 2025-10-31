package com.robertosodini.authify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpDto {

    //TODO:  Validazione anche di 6 numeri
    @NotBlank(message = "L'OTP non può essere vuoto")
    private String otp;
}
