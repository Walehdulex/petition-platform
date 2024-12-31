package com.example.petitionplatform.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullname;

    @NotNull
    private String dateOfBirth;

    @NotBlank
    @Size(min = 6)
    private String password;

}
