package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.dto.UpdatingUserDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.dto.UserFilterDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.util.ErrorMessageCreator;
import com.example.bankcards.util.PageSizeAndNumberValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private RoleService roleService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ErrorMessageCreator errorMessageCreator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Converter<User, UserDTO> converterUserDTO;
    @Mock
    private Converter<Page<User>, PageDTO<UserDTO>> converterPageDTO;
    @Mock
    private PageSizeAndNumberValidator pageSizeAndNumberValidator;

    private UserServiceImpl userServiceImpl;

    private UUID id;

    @BeforeEach
    void setUp(){
        id = UUID.randomUUID();

        userServiceImpl = new UserServiceImpl(
                userRepository,
                errorMessageCreator,
                roleService,
                null,
                passwordEncoder,
                converterUserDTO,
                converterPageDTO,
                pageSizeAndNumberValidator
        );
    }

    @AfterEach
    void tearDown() {
        reset(userRepository, roleService, errorMessageCreator, passwordEncoder, converterUserDTO, converterPageDTO);
    }

    @Test
    void testUpdateUserWhenUserNotFound_ShouldThrowException() {
        doReturn(Optional.empty()).when(userRepository).findById(any());
        doReturn("Message").when(errorMessageCreator).createErrorMessage(any(), any());

        assertThrows(ValidationException.class, () -> userServiceImpl.updateUser(id, UpdatingUserDTO.builder().build()));
    }

    @Test
    void testUpdateUserWhenRoleNotFound_ShouldThrowException() {
        doReturn(Optional.of(User.builder().build())).when(userRepository).findById(any());
        doThrow(new ValidationException("Not found")).when(roleService).getRoleOrThrowValidationException(any());

        assertThrows(ValidationException.class, () -> userServiceImpl.updateUser(id, UpdatingUserDTO.builder().role("role").build()));
    }

    @Test
    void testUpdateUserWhenDTOCorrect_ShouldUpdateUserAndCallConverterDTO() {
        User user = User.builder().id(id).build();
        doReturn(Optional.of(user)).when(userRepository).findById(any());
        doAnswer(answer -> Role.builder().role(answer.getArgument(0)).build()).when(roleService).getRoleOrThrowValidationException(any());
        doAnswer(answer -> answer.getArgument(0)).when(userRepository).save(any());
        doReturn("encoded").when(passwordEncoder).encode(any());
        doReturn(UserDTO.builder().build()).when(converterUserDTO).convert(any());

        UpdatingUserDTO updatingUserDTO = UpdatingUserDTO.builder()
                .login("login")
                .name("name")
                .password("password")
                .role(RoleService.PREFIX_ROLE + "role")
                .build();

        userServiceImpl.updateUser(id, updatingUserDTO);

        assertEquals("encoded", user.getPassword());
        assertEquals(updatingUserDTO.getLogin(), user.getLogin());
        assertEquals(updatingUserDTO.getName(), user.getName());
        assertEquals(updatingUserDTO.getRole(), user.getRole().getRole());
        verify(converterUserDTO).convert(any());
    }



    @Test
    void testGetUsersWhenIncorrectInput_ShouldThrowException() {
        doThrow(new ValidationException()).when(pageSizeAndNumberValidator).validateOrThrowValidationException(anyInt(), anyInt());

        assertThrows(ValidationException.class, () -> userServiceImpl.getUsers(0, -1, UserFilterDTO.builder().build()));
    }

    @Test
    void testGetUsersWhenCorrectInput_ShouldCallConverterDTO() {
        Page<User> page = Page.empty();
        doReturn(page).when(userRepository).findAll(any(Specification.class), any(PageRequest.class));
        doReturn(PageDTO.builder().build()).when(converterPageDTO).convert(page);

        userServiceImpl.getUsers(0, 1, UserFilterDTO.builder().build());

        verify(converterPageDTO).convert(page);
    }

    @Test
    void testCheckIfLoginFreeOtherwiseThrowValidationException_ShouldThrowException() {
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByLoginIgnoreCase(anyString());

        assertThrows(ValidationException.class, () -> userServiceImpl.checkIfLoginFreeOtherwiseThrowValidationException(id.toString()));
    }

    @Test
    void testCheckIfLoginFreeOtherwiseThrowValidationException_ShouldNotThrowException() {
        doReturn(Optional.empty()).when(userRepository).findByLoginIgnoreCase(anyString());

        assertDoesNotThrow(() -> userServiceImpl.checkIfLoginFreeOtherwiseThrowValidationException(id.toString()));
    }

    @Test
    void testGetUserOrThrowValidationException_ShouldThrowException() {
        doReturn(Optional.empty()).when(userRepository).findById(any());

        assertThrows(ValidationException.class, () -> userServiceImpl.getUserOrThrowValidationException(id, "field"));
    }

    @Test
    void testGetUserOrThrowValidationException_ShouldNotThrowException() {
        doReturn(Optional.of(User.builder().build())).when(userRepository).findById(any());

        assertDoesNotThrow(() -> userServiceImpl.getUserOrThrowValidationException(id, "field"));
    }
}