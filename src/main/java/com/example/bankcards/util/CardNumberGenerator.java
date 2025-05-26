package com.example.bankcards.util;

import com.example.bankcards.entity.CardNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class CardNumberGenerator {
    private final Random random;

    public CardNumber generateCardNumber() {
        byte[] number = new byte[CardNumber.CARD_SIZE];

        random.nextBytes(number);
        int checkSum = 0;
        for(int i = 0; i < CardNumber.CARD_SIZE - 1; i++) {
            number[i] %= 10;

            if(i % 2 == 0){
                int toAdd = (byte) (number[i] * 2);
                while(toAdd >= 10){
                    toAdd = sumDigits(toAdd);
                }
            }
            else{
                checkSum += number[i];
            }
        }

        number[CardNumber.CARD_SIZE - 1] = (byte) (10 - checkSum % 10);

        return CardNumber.builder().number(number).build();
    }

    private int sumDigits(int number){
        int result = 0;

        while(number > 0){
            result += number % 10;
            number /= 10;
        }

        return result;
    }
}
