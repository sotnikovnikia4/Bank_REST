package com.example.bankcards.service;

import com.example.bankcards.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByLogin(String login);

    Optional<User> getUserById(UUID id);
}
