package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthenticationDTO;
import com.example.bankcards.dto.CreationUserDTO;
import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.service.AuthorizationService;
import com.example.bankcards.utl.ErrorMessageCreator;
import com.example.bankcards.utl.validation.RegistrationValidator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {
    private final ErrorMessageCreator errorMessageCreator;
    private final AuthorizationService authorizationService;
    private final RegistrationValidator registrationValidator;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO register(@RequestBody @Valid CreationUserDTO creationUserDTO, BindingResult bindingResult){
        registrationValidator.validate(creationUserDTO, bindingResult);
        if(bindingResult.hasErrors()){
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return authorizationService.register(creationUserDTO);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        TokenDTO tokenDTO = authorizationService.authenticate(authenticationDTO);

        return ResponseEntity.ok(tokenDTO);
    }
}
