package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CardService {
    CardDTO createCard(CreationCardDTO creationCardDTO);

    CardDTO getCardLikeAdmin(UUID id) throws ValidationException;

    CardDTO setStatus(UUID id, String active);

    List<CardDTO> transfer(TransferDTO transferDTO);

    void deleteCard(UUID id);

    Card getCardOrThrowValidationException(UUID id) throws ValidationException;

    void checkOwnerOrThrowException(Card card, UUID userId);

    PageDTO<CardDTO> getCardsLikeAdmin(int pageNumber, int pageSize, CardFilterDTO cardFilterDTO);

    PageDTO<CardDTO> getCardsLikeUser(int pageNumber, int pageSize, CardFilterDTO cardFilterDTO);

    CardDTO getCardLikeUser(UUID cardId);

    CardDTO addMoney(UUID cardId, BigDecimal amount);
}
