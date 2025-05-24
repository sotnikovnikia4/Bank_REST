package com.example.bankcards.utl;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;

@Component
public class ErrorMessageCreator {
    public MultiValueMap<String, String> createErrorMessageMap(BindingResult bindingResult) {
        MultiValueMap<String, String> errorMessageMap = new LinkedMultiValueMap<>();

        for(var error : bindingResult.getFieldErrors()) {
            errorMessageMap.add(error.getField(), error.getDefaultMessage());
        }

        return errorMessageMap;
    }

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

    public String createErrorMessage(String field, String message){
        return String.format("%s: %s", field, message);
    }
}
