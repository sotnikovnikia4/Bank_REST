package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.utl.CardNumberGenerator;
import com.example.bankcards.utl.EncryptionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.Optional;

public interface CardService {
    String ACTIVE = "ACTIVE";
    String BLOCKED = "BLOCKED";
    String EXPIRED = "EXPIRED";

    CardDTO createCard(CreationCardDTO creationCardDTO);
}
