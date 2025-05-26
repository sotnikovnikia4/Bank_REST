package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockRequestServiceImpl implements BlockRequestService {
    private final CardService cardService;
    private final UserDetailsHolder userDetailsHolder;
    private final BlockRequestRepository blockRequestRepository;

    private final ErrorMessageCreator errorMessageCreator;

    @Override
    public void createBlockRequest(UUID cardId) {
        User user = userDetailsHolder.getUserFromSecurityContext();

        Card card = cardService.getCardOrThrowValidationException(cardId);

        cardService.checkOwnerOrThrowException(card, user.getId());

        BlockRequest blockRequest = BlockRequest.builder()
                .card(card)
                .build();

        blockRequestRepository.save(blockRequest);
    }

    @Override
    public void closeBlockRequest(UUID id) {
        Optional<BlockRequest> blockRequest = blockRequestRepository.findById(id);

        if (blockRequest.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Block request with this id does not exist"));
        }

        blockRequestRepository.delete(blockRequest.get());
    }
}
