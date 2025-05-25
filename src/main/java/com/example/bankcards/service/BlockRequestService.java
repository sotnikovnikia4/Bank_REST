package com.example.bankcards.service;

import java.util.UUID;

public interface BlockRequestService {

    void createBlockRequest(UUID id);

    void closeBlockRequest(UUID id);
}
