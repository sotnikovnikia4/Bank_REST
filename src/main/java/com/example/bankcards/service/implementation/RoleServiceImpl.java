package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final ErrorMessageCreator errorMessageCreator;

    @Override
    public Role getRoleOrThrowValidationException(String roleName) throws ValidationException {
        Optional<Role> role = roleRepository.findByRole(RoleService.PREFIX_ROLE + roleName);

        if(role.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("role", "This role does not exists"));
        }

        return role.get();
    }
}
