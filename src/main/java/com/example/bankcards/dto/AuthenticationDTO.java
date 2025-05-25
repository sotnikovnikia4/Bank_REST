package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
    @NotBlank(message = "Login should not be blank")
    @Length(min = 3, max = 255, message = "Length of login should be between 3 and 255")
    private String login;

    @NotBlank(message = "Password should not be blank")
    private String password;
}
