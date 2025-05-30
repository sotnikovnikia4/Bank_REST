package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardFilterDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.StatusService;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {
    private final ErrorMessageCreator errorMessageCreator;
    private final CardService cardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO create(@RequestBody @Valid CreationCardDTO creationCardDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage(bindingResult));
        }

        return cardService.createCard(creationCardDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO get(@PathVariable UUID id) {
        return cardService.getCardLikeAdmin(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<CardDTO> getAll(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @ModelAttribute CardFilterDTO cardFilterDTO
            ){
        return cardService.getCardsLikeAdmin(pageNumber, pageSize, cardFilterDTO);
    }

    @PatchMapping("/{cardId}/activate")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO activate(@PathVariable UUID cardId) {
        return cardService.setStatus(cardId, StatusService.ACTIVE);
    }

    @PatchMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO block(@PathVariable UUID cardId) {
        return cardService.setStatus(cardId, StatusService.BLOCKED);
    }

    @PatchMapping("/{cardId}/add-money")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO addMoney(@PathVariable UUID cardId, @RequestParam BigDecimal amount) {
        return cardService.addMoney(cardId, amount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        cardService.deleteCard(id);
    }
}
