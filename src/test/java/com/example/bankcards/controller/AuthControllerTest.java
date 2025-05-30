package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.security.JWTFilter;
import com.example.bankcards.security.JWTUtil;
import com.example.bankcards.service.AuthorizationService;
import com.example.bankcards.util.CardNumberEncryption;
import com.example.bankcards.util.ErrorMessageCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.BindingResult;

import javax.crypto.SecretKey;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErrorMessageCreator errorMessageCreator;
    @MockitoBean
    private AuthorizationService authorizationService;
    @MockitoBean
    private JWTFilter jwtFilter;
    @MockitoBean
    private JWTUtil jwtUtil;
    @MockitoBean
    private CardNumberEncryption cardNumberEncryption;
    @MockitoBean
    private SecretKey secretKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void testRegisterWhenSomethingIsMissing_ShouldReturn400(){
        doAnswer(answer -> Objects.requireNonNull(((BindingResult) answer.getArgument(0)).getFieldError("role")).getField()).when(errorMessageCreator).createErrorMessage(any(BindingResult.class));
        CreationUserDTO creationUserDTO = CreationUserDTO.builder().build();

        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationUserDTO))
        );

        resultActions.andExpect(status().isBadRequest());
        verify(errorMessageCreator).createErrorMessage(any(BindingResult.class));
    }

    @Test
    @SneakyThrows
    void testRegisterWhenCorrect_ShouldCallRegister(){
        CreationUserDTO creationUserDTO = CreationUserDTO.builder().login("login").password("password").name("name").role("role").build();

        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationUserDTO))
        );

        resultActions.andExpect(status().isCreated());
        verify(authorizationService).register(any());
    }

    @Test
    @SneakyThrows
    void testLoginWhenSomethingIsMissing_ShouldReturn400(){
        doAnswer(answer -> Objects.requireNonNull(((BindingResult) answer.getArgument(0)).getFieldError("login")).getField()).when(errorMessageCreator).createErrorMessage(any(BindingResult.class));
        AuthenticationDTO authenticationDTO = AuthenticationDTO.builder().build();

        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDTO))
        );

        resultActions.andExpect(status().isBadRequest());
        verify(errorMessageCreator).createErrorMessage(any(BindingResult.class));
    }

    @Test
    @SneakyThrows
    void testLoginWhenCorrect_ShouldCallRegister(){
        AuthenticationDTO authenticationDTO = AuthenticationDTO.builder().login("login").password("password").build();

        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationDTO))
        );

        resultActions.andExpect(status().isOk());
        verify(authorizationService).authenticate(any());
    }
}