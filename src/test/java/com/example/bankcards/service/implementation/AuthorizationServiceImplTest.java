package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JWTUtil;
import com.example.bankcards.security.UserDetailsHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsHolder userDetailsHolder;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @Test
    void testRegister_ShouldCreateUserAndReturnToken(){
        UUID uuid = UUID.randomUUID();
        when(jwtUtil.generateToken(any(User.class))).thenAnswer(answer -> {
            User user = answer.getArgument(0, User.class);
            return user.getLogin() + user.getPassword() + user.getRole().getRole() + user.getName() + user.getId();
        });

        when(userRepository.save(any(User.class))).thenAnswer(answer -> {
            User user = answer.getArgument(0, User.class);

            user.setId(uuid);

            return user;
        });

        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        CreationUserDTO creationUserDTO = CreationUserDTO.builder()
                .login("login")
                .password("12345678")
                .name("name")
                .role(Role.builder().role("role").build())
                .build();

        TokenDTO tokenDTO = authorizationService.register(creationUserDTO);

        assertEquals("login" + "password" + "role" + "name" + uuid, tokenDTO.getToken());
    }
}