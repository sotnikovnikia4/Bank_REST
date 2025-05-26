package com.example.bankcards.util.validation;

import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreationCardValidator  implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreationCardDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreationCardDTO creationCardDTO = (CreationCardDTO) target;

        User user = userService.getUserOrThrowValidationException(creationCardDTO.getOwnerId(), "ownerId");

        LocalDate now = LocalDate.now();
        if(now.isAfter(creationCardDTO.getExpiresAt())){
            errors.rejectValue("expiresAt", "", "Value is less than current date");
        }
    }
}
