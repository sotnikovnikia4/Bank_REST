package com.example.bankcards.config;

import com.example.bankcards.util.CardNumberEncryption;
import com.example.bankcards.util.EncryptionHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Random;

@Configuration
public class EncryptionConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey(EncryptionHelper encryptionHelper, @Value("${encryption.password}") String password, @Value("${encryption.salt}") String salt) {
        return encryptionHelper.generateKey(password.toCharArray(), salt.getBytes());
    }

    @Bean
    public Random random(){
        return new Random();
    }
}
