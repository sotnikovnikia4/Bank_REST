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
    private int[] number;

    private String getMask(){
        StringBuffer sb = new StringBuffer(16);

        sb.append("**** **** **** ");
        sb.append(number[12]);
        sb.append(number[13]);
        sb.append(number[14]);
        sb.append(number[15]);

        return sb.toString();
    }
}
