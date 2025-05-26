package com.example.bankcards.util;

import org.springframework.validation.BindingResult;

public interface ErrorMessageCreator {
//    MultiValueMap<String, String> createErrorMessageMap(BindingResult bindingResult);

    String createErrorMessage(BindingResult bindingResult);

    String createErrorMessage(String field, String message);
}
