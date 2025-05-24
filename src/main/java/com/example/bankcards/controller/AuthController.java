package com.example.bankcards.controller;

import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.security.JWTUtil;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {
    private final JWTUtil jwtUtil;

    private final AuthenticationManager authManager;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TokenDTO> register(@RequestBody @Valid CreationUserDTO accountDTO, BindingResult bindingResult){
        return null;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        return null;
    }
}
