package com.example.bankcards.service.implementation;

import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BlockRequestServiceImpl implements BlockRequestService {
    private final CardService cardService;
    private final UserDetailsService userDetailsService;

    @Override
    public void createBlockRequest(UUID cardId) {

    }

    @Override
    public void closeBlockRequest(UUID id) {

    }
}
