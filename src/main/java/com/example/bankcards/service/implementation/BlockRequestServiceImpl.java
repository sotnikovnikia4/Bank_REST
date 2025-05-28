package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.BlockRequestDTO;
import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.ErrorMessageCreator;
import com.example.bankcards.util.PageSizeAndNumberValidator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockRequestServiceImpl implements BlockRequestService {
    private final CardService cardService;
    private final UserDetailsHolder userDetailsHolder;
    private final BlockRequestRepository blockRequestRepository;

    private final ErrorMessageCreator errorMessageCreator;
    private final Converter<BlockRequest, BlockRequestDTO> requestConverter;
    private final Converter<Page<BlockRequest>, PageDTO<BlockRequestDTO>> pageConverter;
    private final PageSizeAndNumberValidator pageSizeAndNumberValidator;

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

    @Override
    public PageDTO<BlockRequestDTO> getAll(int pageNumber, int pageSize) {
        pageSizeAndNumberValidator.validateOrThrowValidationException(pageNumber, pageSize);

        Page<BlockRequest> page = blockRequestRepository.findAll(PageRequest.of(pageNumber, pageSize));

        return pageConverter.convert(page);
    }

    @Override
    public BlockRequestDTO getOne(UUID id) {
        Optional<BlockRequest> blockRequest = blockRequestRepository.findById(id);

        if (blockRequest.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Block request with this id does not exist"));
        }

        return requestConverter.convert(blockRequest.get());
    }
}
