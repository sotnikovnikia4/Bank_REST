package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorMessageCreator errorMessageCreator;

    public UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .role(user.getRole().getRole().substring(RoleService.PREFIX_ROLE.length()))
                .build();
    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public void checkIfLoginFreeOtherwiseThrowValidationException(String login) throws ValidationException{
        Optional<User> userWithSameLogin = getUserByLogin(login);

        if(userWithSameLogin.isPresent()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("login", "Login already exists"));
        }
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }
}