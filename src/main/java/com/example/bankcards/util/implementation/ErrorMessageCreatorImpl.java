package com.example.bankcards.util.implementation;

import com.example.bankcards.util.ErrorMessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ErrorMessageCreatorImpl implements ErrorMessageCreator {
    @Override
    public String createErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        for(var error : bindingResult.getFieldErrors()) {
            errorMessage.append(error.getField());
            errorMessage.append(": ");
            errorMessage.append(error.getDefaultMessage());
            errorMessage.append(";");
        }
        errorMessage.setLength(errorMessage.length() - 1);
        return errorMessage.toString();
    }

    @Override
    public String createErrorMessage(String field, String message){
        return String.format("%s: %s", field, message);
    }
}
