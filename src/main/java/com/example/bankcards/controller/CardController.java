package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.utl.validation.CreationCardValidator;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {
    //пагинация и фильтрация карт
    //перевод между двумя картами одного пользователя
    private final ErrorMessageCreator errorMessageCreator;
    private final CardService cardService;

    private final CreationCardValidator creationCardValidator;

    @PostMapping
    public CardDTO create(@RequestBody @Valid CreationCardDTO creationCardDTO, BindingResult bindingResult) {
        creationCardValidator.validate(creationCardDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return cardService.createCard(creationCardDTO);
    }

//    @GetMapping("/{id}")
//    public CardDTO get(@PathVariable UUID id) {
//
//    }
//
//    @GetMapping
//    public List<CardDTO> getAll(){
//
//    }
//
//    @PatchMapping("/{id}/activate")
//    public CardDTO activate(@PathVariable UUID id) {
//
//    }
//
//    @PatchMapping("/{id}/block")
//    public CardDTO block(@PathVariable UUID id) {
//
//    }


}
