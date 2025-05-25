package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.dto.UserDTO;
import jakarta.validation.Valid;

public interface AuthorizationService {

    UserDTO register(CreationUserDTO userDTO);

    TokenDTO authenticate(@Valid AuthenticationDTO authenticationDTO);
}
