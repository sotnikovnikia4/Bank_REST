package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.StatusService;
import com.example.bankcards.utl.ErrorMessageCreator;
import com.example.bankcards.utl.validation.CreationCardValidator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {
    private final ErrorMessageCreator errorMessageCreator;
    private final CardService cardService;

    private final CreationCardValidator creationCardValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO create(@RequestBody @Valid CreationCardDTO creationCardDTO, BindingResult bindingResult) {
        creationCardValidator.validate(creationCardDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return cardService.createCard(creationCardDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO get(@PathVariable UUID id) {
        return cardService.getCard(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CardDTO> getAll(){
        return null; //TODO
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO activate(@PathVariable UUID id) {
        return cardService.setStatus(id, StatusService.ACTIVE);
    }

    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO block(@PathVariable UUID id) {
        return cardService.setStatus(id, StatusService.BLOCKED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        cardService.deleteCard(id);
    }
}
