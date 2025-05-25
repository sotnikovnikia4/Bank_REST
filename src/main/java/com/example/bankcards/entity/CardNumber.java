package com.example.bankcards.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardNumber {
    public static final int CARD_SIZE = 16;

    private byte[] number;

    public String getMask(){

        return "**** **** **** " +
                number[12] +
                number[13] +
                number[14] +
                number[15];
    }
}
