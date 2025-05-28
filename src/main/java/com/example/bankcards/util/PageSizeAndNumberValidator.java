package com.example.bankcards.util;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageSizeAndNumberValidator {
    private final ErrorMessageCreator errorMessageCreator;

    public void validateOrThrowValidationException(int pageNumber, int pageSize){
        if(pageSize <= 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("pageSize", "Page's size should be greater than 0"));
        }
        if(pageNumber < 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("pageNumber", "Page's number should be non negative"));
        }
    }
}
