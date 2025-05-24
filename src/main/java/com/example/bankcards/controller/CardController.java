package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.utl.CreationCardValidator;
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

@RestController
@RequestMapping(value = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {
    //удаление карты
    //изменение карты
    //получение данных карты
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
}
