package com.example.bankcards.utl;

import com.example.bankcards.entity.CardNumber;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Component
public class EncryptionHelper {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int KEY_ITERATION_COUNT = 100_000;
    private static final int KEY_SIZE = 32;
    private static final int IV_SIZE = 16;

//    @SneakyThrows
//    public SecretKey generateKey() {
//        KeyGenerator generator = KeyGenerator.getInstance("AES");
//        int keySizeBits = KEY_SIZE * 8;
//        generator.init(keySizeBits, RANDOM);
//        return generator.generateKey();
//    }

    @SneakyThrows
    public SecretKey generateKey(char[] password, byte[] salt) {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        int keySizeBits = KEY_SIZE * 8;
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, KEY_ITERATION_COUNT, keySizeBits);
        SecretKey temporaryKey = factory.generateSecret(keySpec);
        keySpec.clearPassword();
        return new SecretKeySpec(temporaryKey.getEncoded(), "AES");
    }

    @SneakyThrows
    public String encryptCardNumber(@NonNull SecretKey key, CardNumber cardNumber) {
        byte[] encrypted = encrypt(key, cardNumber.getNumber());
        return new String(encrypted);
    }

    @SneakyThrows
    public CardNumber decryptCardNumber(@NonNull SecretKey key, String encryptedCardNumber) {
        byte[] decrypted = decrypt(key, encryptedCardNumber.getBytes());
        return CardNumber.builder().number(decrypted).build();
    }

    @SneakyThrows
    public byte[] encrypt(@NonNull SecretKey key, byte[] clearText) {
        byte[] ivBytes = new byte[IV_SIZE];
        RANDOM.nextBytes(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
        byte[] cipherBytes = cipher.doFinal(clearText);
        return concat(ivBytes, cipherBytes);
    }

    @SneakyThrows
    public byte[] decrypt(@NonNull SecretKey key, byte[] cipherText) {
        byte[][] byteArrays = split(cipherText);
        byte[] ivBytes = byteArrays[0];
        byte[] cipherBytes = byteArrays[1];
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
        return cipher.doFinal(cipherBytes);
    }

    private byte[] concat(byte[] ivBytes, byte[] cipherBytes) {
        byte[] concatenatedBytes = new byte[ivBytes.length + cipherBytes.length];
        System.arraycopy(ivBytes, 0, concatenatedBytes, 0, ivBytes.length);
        System.arraycopy(cipherBytes, 0, concatenatedBytes, ivBytes.length, cipherBytes.length);
        return concatenatedBytes;
    }

    private byte[][] split(byte[] concatenatedBytes) {
        byte[] ivBytes = new byte[IV_SIZE];
        byte[] cipherBytes = new byte[concatenatedBytes.length - IV_SIZE];
        System.arraycopy(concatenatedBytes, 0, ivBytes, 0, IV_SIZE);
        System.arraycopy(concatenatedBytes, IV_SIZE, cipherBytes, 0, concatenatedBytes.length - IV_SIZE);
        return new byte[][]{ivBytes, cipherBytes};
    }

}
