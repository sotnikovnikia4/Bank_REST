package com.example.bankcards.util.converter;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterPageDTOWithCards extends ConverterPageDTO<Card, CardDTO> {
    public ConverterPageDTOWithCards(Converter<Card, CardDTO> converter) {
        super(converter);
    }
}
