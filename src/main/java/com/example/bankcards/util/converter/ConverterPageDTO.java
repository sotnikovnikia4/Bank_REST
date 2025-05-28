package com.example.bankcards.util.converter;

import com.example.bankcards.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;

public abstract class ConverterPageDTO<T, S> implements Converter<Page<T>, PageDTO<S>> {
    private final Converter<T, S> converter;

    protected ConverterPageDTO(Converter<T, S> converter) {
        this.converter = converter;
    }

    @Override
    public PageDTO<S> convert(Page<T> page) {
        return PageDTO.<S>builder().data(
                page.getContent().stream().map(converter::convert).toList()
        ).totalPages(page.getTotalPages()).pageSize(page.getSize()).pageNumber(page.getNumber()).totalElements(page.getTotalElements()).build();
    }
}
