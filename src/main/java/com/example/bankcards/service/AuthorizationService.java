package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import jakarta.validation.Valid;

public interface AuthorizationService {
    String ADMIN_ROLE = "ADMIN";

    TokenDTO register(CreationUserDTO userDTO);

    TokenDTO authenticate(@Valid AuthenticationDTO authenticationDTO);
}
