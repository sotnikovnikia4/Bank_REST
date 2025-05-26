package com.example.bankcards.service;

import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.dto.UpdatingUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import jakarta.validation.ValidationException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByLogin(String login);

//    Optional<User> getOptionalUserById(UUID id);

    void checkIfLoginFreeOtherwiseThrowValidationException(String login) throws ValidationException;

    User saveUser(User user);

    UserDTO convertToUserDTO(User user);

    UserDTO getUser(UUID id);

    void deleteUser(UUID id);

    UserDTO updateUser(UUID id, UpdatingUserDTO userDTO);

    PageDTO<UserDTO> getUsers(int pageNumber, int pageSize, String name, String role, String login);

    User getUserOrThrowValidationException(UUID id, String fieldName) throws ValidationException;
}
