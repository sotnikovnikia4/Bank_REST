package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.dto.UpdatingUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.dto.UserFilterDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Role_;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.User_;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorMessageCreator errorMessageCreator;
    private final RoleService roleService;
    private final UserDetailsHolder userDetailsHolder;
    private final PasswordEncoder passwordEncoder;
    private final Converter<User, UserDTO> converterUserDTO;
    private final Converter<Page<User>, PageDTO<UserDTO>> converterPageDTO;

    @Override
    public UserDTO getUser(UUID id) {
        return converterUserDTO.convert(getUserOrThrowValidationException(id, "userId"));
    }

    @Override
    public void deleteUser(UUID id) {
        User user =  getUserOrThrowValidationException(id, "userId");

        userRepository.delete(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UpdatingUserDTO userDTO) {
        User user = getUserOrThrowValidationException(id, "userId");
        Role role = roleService.getRoleOrThrowValidationException(userDTO.getRole());

        user.setName(userDTO.getName());
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(role);

        user = userRepository.save(user);

        return converterUserDTO.convert(user);
    }

    @Override
    public PageDTO<UserDTO> getUsers(int pageNumber, int pageSize, UserFilterDTO userFilterDTO) throws ValidationException{
        if(pageSize <= 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("pageSize", "Page's size should be greater than 0"));
        }
        if(pageNumber < 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("pageNumber", "Page's number should be non negative"));
        }

        Page<User> page = userRepository.findAll(
                findUsersSpecification(userFilterDTO),
                PageRequest.of(pageNumber, pageSize)
        );

        return converterPageDTO.convert(page);
    }

    private Specification<User> findUsersSpecification(UserFilterDTO userFilterDTO) {
        if(userFilterDTO.getRole() != null && !userFilterDTO.getRole().isBlank()){
            userFilterDTO.setRole(RoleService.PREFIX_ROLE + userFilterDTO.getRole());
        }

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(userFilterDTO.getName() != null && !userFilterDTO.getName().isBlank()){
                predicates.add(builder.like(root.get(User_.name), "%" + userFilterDTO.getName() + "%"));
            }
            if(userFilterDTO.getLogin() != null && !userFilterDTO.getLogin().isBlank()){
                predicates.add(builder.like(root.get(User_.login), "%" + userFilterDTO.getLogin() + "%"));
            }
            if(userFilterDTO.getRole() != null && !userFilterDTO.getRole().isBlank()){
                Join<User, Role> roleJoin = root.join(User_.role);
                predicates.add(builder.equal(roleJoin.get(Role_.role), userFilterDTO.getRole()));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLoginIgnoreCase(login);
    }

    @Override
    public void checkIfLoginFreeOtherwiseThrowValidationException(String login) throws ValidationException{
        Optional<User> userWithSameLogin = getUserByLogin(login);

        if(userWithSameLogin.isPresent()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("login", "Login already exists"));
        }
    }

    public UserDTO saveUser(User user){
        return converterUserDTO.convert(userRepository.save(user));
    }

    @Override
    public User getUserOrThrowValidationException(UUID id, String fieldName) throws ValidationException {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(fieldName, "User not found"));
        }

        return user.get();
    }

    @Override
    public UserDTO getCurrentUserInfo() {
        return converterUserDTO.convert(userDetailsHolder.getUserFromSecurityContext());
    }
}