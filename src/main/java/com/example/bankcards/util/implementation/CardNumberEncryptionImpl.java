package com.example.bankcards.util.implementation;

import com.example.bankcards.entity.CardNumber;
import com.example.bankcards.util.CardNumberEncryption;
import com.example.bankcards.util.EncryptionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class CardNumberEncryptionImpl implements CardNumberEncryption {
    private final EncryptionHelper encryptionHelper;

    @Override
    @SneakyThrows
    public byte[] encryptCardNumber(@NonNull SecretKey key, CardNumber cardNumber) {
        return encryptionHelper.encrypt(key, cardNumber.getNumber());
    }

    @Override
    @SneakyThrows
    public CardNumber decryptCardNumber(@NonNull SecretKey key, byte[] encryptedCardNumber) {
        byte[] decrypted = encryptionHelper.decrypt(key, encryptedCardNumber);
        return CardNumber.builder().number(decrypted).build();
    }
}
