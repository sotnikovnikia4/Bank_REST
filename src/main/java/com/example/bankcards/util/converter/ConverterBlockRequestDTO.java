package com.example.bankcards.util.converter;

import com.example.bankcards.dto.BlockRequestDTO;
import com.example.bankcards.entity.BlockRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConverterBlockRequestDTO implements Converter<BlockRequest, BlockRequestDTO> {
    @Override
    public BlockRequestDTO convert(BlockRequest blockRequest) {
        return BlockRequestDTO.builder().id(blockRequest.getId()).cardId(blockRequest.getCard().getId()).build();
    }
}
