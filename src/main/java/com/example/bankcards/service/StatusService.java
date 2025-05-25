package com.example.bankcards.service;

import com.example.bankcards.entity.Status;
import jakarta.validation.ValidationException;

public interface StatusService {
    String ACTIVE = "ACTIVE";
    String BLOCKED = "BLOCKED";
    String EXPIRED = "EXPIRED";

    Status getStatusOrThrowValidationException(String status) throws ValidationException;
}
