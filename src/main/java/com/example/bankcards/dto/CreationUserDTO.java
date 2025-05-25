package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreationUserDTO {
    @NotBlank(message = "Role should not be blank")
    private String role;

    @NotBlank(message = "Login should not be blank")
    @Length(min = 3, max = 255, message = "Length of login should be between 3 and 255")
    private String login;

    @NotBlank(message = "Password should not be blank")
    private String password;

    @NotBlank(message = "Name should not be blank")
    @Length(min = 2, max = 255, message = "Length of name should be between 2 and 255")
    private String name;
}
