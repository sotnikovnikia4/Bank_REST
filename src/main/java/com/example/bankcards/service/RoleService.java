package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import jakarta.validation.ValidationException;

public interface RoleService {
    String PREFIX_ROLE = "ROLE_";

    Role getRoleOrThrowValidationException(String role) throws ValidationException;
}
