package com.example.bankcards.utl;

import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationValidator implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreationUserDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreationUserDTO userToCheck = (CreationUserDTO) target;
        Optional<User> userWithSameLogin = userService.getUserByLogin(userToCheck.getLogin());

        if(userWithSameLogin.isPresent()) {
            errors.rejectValue("login", "", "Login already exists");
        }
    }
}
