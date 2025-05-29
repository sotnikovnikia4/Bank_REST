package com.example.bankcards.util;

import com.example.bankcards.entity.CardNumber;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public interface CardNumberEncryption {
    byte[] encryptCardNumber(@NonNull SecretKey key, CardNumber cardNumber);

    CardNumber decryptCardNumber(@NonNull SecretKey key, byte[] encryptedCardNumber);
}
