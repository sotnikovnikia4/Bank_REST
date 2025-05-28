package com.example.bankcards.service;

import com.example.bankcards.dto.BlockRequestDTO;
import com.example.bankcards.dto.PageDTO;

import java.util.List;
import java.util.UUID;

public interface BlockRequestService {

    void createBlockRequest(UUID id);

    void closeBlockRequest(UUID id);

    PageDTO<BlockRequestDTO> getAll(int pageNumber, int pageSize);

    BlockRequestDTO getOne(UUID id);
}
