package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import jakarta.validation.ValidationException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByLogin(String login);

    Optional<User> getUserById(UUID id);

    void checkIfLoginFreeOtherwiseThrowValidationException(String login) throws ValidationException;

    User saveUser(User user);
}
