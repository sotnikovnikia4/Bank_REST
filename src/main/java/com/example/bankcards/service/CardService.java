package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.dto.TransferDTO;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.UUID;

public interface CardService {
    CardDTO createCard(CreationCardDTO creationCardDTO);

    CardDTO getCard(UUID id) throws ValidationException;

    CardDTO setStatus(UUID id, String active);

    List<CardDTO> transfer(TransferDTO transferDTO);

    void deleteCard(UUID id);
}
