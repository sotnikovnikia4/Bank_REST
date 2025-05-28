package com.example.bankcards.util.converter;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterPageDTOWithUsers extends ConverterPageDTO<User, UserDTO> {
    public ConverterPageDTOWithUsers(Converter<User, UserDTO> converter) {
        super(converter);
    }
}
