package com.example.bankcards.service;

import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.dto.UpdatingUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.dto.UserFilterDTO;
import com.example.bankcards.entity.User;
import jakarta.validation.ValidationException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserByLogin(String login);

    void checkIfLoginFreeOtherwiseThrowValidationException(String login) throws ValidationException;

    UserDTO saveUser(User user);

    UserDTO getUser(UUID id);

    void deleteUser(UUID id);

    UserDTO updateUser(UUID id, UpdatingUserDTO userDTO);

    PageDTO<UserDTO> getUsers(int pageNumber, int pageSize, UserFilterDTO userFilterDTO) throws ValidationException;

    User getUserOrThrowValidationException(UUID id, String fieldName) throws ValidationException;

    UserDTO getCurrentUserInfo();
}
