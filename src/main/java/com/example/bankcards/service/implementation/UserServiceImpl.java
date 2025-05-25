package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.UpdatingUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
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
    private final RoleService roleService;

    public UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .role(user.getRole().getRole().substring(RoleService.PREFIX_ROLE.length()))
                .build();
    }

    @Override
    public UserDTO getUser(UUID id) {
        return convertToUserDTO(getUserOrThrowValidationException(id));
    }

    @Override
    public void deleteUser(UUID id) {
        User user =  getUserOrThrowValidationException(id);

        userRepository.delete(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UpdatingUserDTO userDTO) {
        User user = getUserOrThrowValidationException(id);
        Role role = roleService.getRoleOrThrowValidationException(userDTO.getRole());

        user.setName(userDTO.getName());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        user.setRole(role);

        user = userRepository.save(user);

        return convertToUserDTO(user);
    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public Optional<User> getOptionalUserById(UUID id) {
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

    private User getUserOrThrowValidationException(UUID id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "User not found"));
        }

        return user.get();
    }
}