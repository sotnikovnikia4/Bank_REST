package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import jakarta.validation.ValidationException;

public interface RoleService {
    String PREFIX_ROLE = "ROLE_";

    String ADMIN_ROLE = "ADMIN";
    String USER_ROLE = "USER";

    Role getRoleOrThrowValidationException(String role) throws ValidationException;
}
