package com.example.bankcards.util.converter;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardNumberEncryption;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class ConverterCardDTO implements Converter<Card, CardDTO> {
    private final CardNumberEncryption cardNumberEncryption;
    private final SecretKey secretKey;

    @Override
    public CardDTO convert(Card card) {
        if(card.getCardNumber() == null){
            card.setCardNumber(cardNumberEncryption.decryptCardNumber(secretKey, card.getEncryptedCardNumber()));
        }

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
