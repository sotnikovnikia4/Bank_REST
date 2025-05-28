package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ErrorMessageCreator errorMessageCreator;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testGetRoleOrThrowValidationException_ShouldThrowValidationException() {
        doReturn(Optional.empty()).when(roleRepository).findByRole(any());
        doReturn("message").when(errorMessageCreator).createErrorMessage(anyString(), anyString());

        assertThrows(ValidationException.class, () -> roleService.getRoleOrThrowValidationException("role"));
    }

    @Test
    void testGetRoleOrThrowValidationException_ShouldNotThrowValidationException() {
        doReturn(Optional.of(Role.builder().build())).when(roleRepository).findByRole(any());

        assertDoesNotThrow(() -> roleService.getRoleOrThrowValidationException("role"));
    }
}