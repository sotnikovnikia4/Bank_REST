package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JWTUtil;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  AuthenticationManager authenticationManager;
    @Mock
    private  UserDetailsHolder userDetailsHolder;
    @Mock
    private  JWTUtil jwtUtil;

    @Mock
    private  RoleService roleService;
    @Mock
    private  UserService userService;
    
    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Test
    void testRegisterWhenLoginNotFree_ShouldThrowException(){
        doThrow(new ValidationException("login not free")).when(userService).checkIfLoginFreeOtherwiseThrowValidationException(any());

        assertThrows(ValidationException.class, ()->authorizationService.register(CreationUserDTO.builder().login("login").build()));
    }

    @Test
    void testRegisterWhenRoleNotExists_ShouldThrowException(){
        doNothing().when(userService).checkIfLoginFreeOtherwiseThrowValidationException(any());
        doThrow(new ValidationException("login not free")).when(roleService).getRoleOrThrowValidationException(any());

        assertThrows(ValidationException.class, ()->authorizationService.register(CreationUserDTO.builder().login("login").role("role").build()));
    }

    @Test
    void testRegisterWhenDTOCorrect_ShouldReturnDTOWithId(){
        UUID uuid = UUID.randomUUID();
        doNothing().when(userService).checkIfLoginFreeOtherwiseThrowValidationException(any());
        doAnswer(answer -> Role.builder().role(answer.getArgument(0)).build()).when(roleService).getRoleOrThrowValidationException(any());
        doReturn("encoded").when(passwordEncoder).encode(any());
        doAnswer(answer -> UserDTO.builder().id(uuid).build()).when(userService).saveUser(any());

        CreationUserDTO creationUserDTO = CreationUserDTO.builder().login("login").role("role").build();

        UserDTO actual = authorizationService.register(creationUserDTO);

        assertEquals(uuid, actual.getId());
    }

    @Test
    void testAuthenticateWhenIncorrectData_ShouldThrowException(){
        doThrow(new UsernameNotFoundException("Not found")).when(authenticationManager).authenticate(any());

        assertThrows(UsernameNotFoundException.class, () -> authorizationService.authenticate(AuthenticationDTO.builder().login("login").password("password").build()));
    }

    @Test
    void testAuthenticateWhenCorrectData_ShouldReturnNotNull(){
        doReturn(new UsernamePasswordAuthenticationToken("login", "password")).when(authenticationManager).authenticate(any());
        doReturn(User.builder().build()).when(userDetailsHolder).getUserFromPrincipal(any());
        doReturn("token").when(jwtUtil).generateToken(any());

        assertNotNull(authorizationService.authenticate(AuthenticationDTO.builder().login("login").password("password").build()));
    }
}