package com.example.bankcards.service;

import com.example.bankcards.dto.BlockRequestDTO;

import java.util.List;
import java.util.UUID;

public interface BlockRequestService {

    void createBlockRequest(UUID id);

    void closeBlockRequest(UUID id);

    List<BlockRequestDTO> getAll(int pageNumber, int pageSize);
}
