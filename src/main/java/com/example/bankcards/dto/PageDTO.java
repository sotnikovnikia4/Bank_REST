package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private int pageNumber;
    private int pageSize;

    private long totalPages;

    private List<T> data;
}
