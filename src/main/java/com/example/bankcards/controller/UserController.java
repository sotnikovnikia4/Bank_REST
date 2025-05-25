package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final CardService cardService;
    private final ErrorMessageCreator errorMessageCreator;

    @PostMapping("/me/transfer")
    public List<CardDTO> transfer(@RequestBody @Valid TransferDTO transferDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return cardService.transfer(transferDTO);
    }
}
