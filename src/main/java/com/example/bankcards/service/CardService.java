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

@Component
@RequiredArgsConstructor
public class CardService {
    private static final String ACTIVE = "ACTIVE";
    private static final String BLOCKED = "BLOCKED";
    private static final String EXPIRED = "EXPIRED";

    private final CardRepository cardRepository;
    private final EncryptionHelper encryptionHelper;
    private final SecretKey secretKey;
    private final CardNumberGenerator cardNumberGenerator;

    public CardDTO createCard(CreationCardDTO creationCardDTO) {
        Card card = Card.builder()
                .owner(User.builder().id(creationCardDTO.getOwnerId()).build())
                .expiresAt(creationCardDTO.getExpiresAt())
                .balance(new BigDecimal(0))
                .status(Status.builder().status(ACTIVE).build())
                .cardNumber(cardNumberGenerator.generateCardNumber())
                .build();

        card.setEncryptedCardNumber(encryptionHelper.encryptCardNumber(secretKey, card.getCardNumber()));

        card = cardRepository.save(card);
        return convertToCardDTO(card);
    }

    private CardDTO convertToCardDTO(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .balance(card.getBalance())
                .expiresAt(card.getExpiresAt())
                .status(card.getStatus().getStatus())
                .ownerId(card.getOwner().getId())
                .cardNumber(card.getCardNumber().getMask())
                .build();
    }
}
