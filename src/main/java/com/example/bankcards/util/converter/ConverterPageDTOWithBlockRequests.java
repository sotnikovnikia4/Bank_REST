package com.example.bankcards.util.converter;

import com.example.bankcards.dto.BlockRequestDTO;
import com.example.bankcards.entity.BlockRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterPageDTOWithBlockRequests extends ConverterPageDTO<BlockRequest, BlockRequestDTO> {
    protected ConverterPageDTOWithBlockRequests(Converter<BlockRequest, BlockRequestDTO> converter) {
        super(converter);
    }
}
