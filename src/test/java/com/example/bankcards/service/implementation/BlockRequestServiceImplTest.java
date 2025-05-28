package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.BlockRequestDTO;
import com.example.bankcards.dto.PageDTO;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.ErrorMessageCreator;
import com.example.bankcards.util.PageSizeAndNumberValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockRequestServiceImplTest {
    @Mock
    private CardService cardService;
    @Mock
    private UserDetailsHolder userDetailsHolder;
    @Mock
    private BlockRequestRepository blockRequestRepository;

    @Mock
    private ErrorMessageCreator errorMessageCreator;

    @Mock
    private Converter<BlockRequest, BlockRequestDTO> requestConverter;
    @Mock
    private Converter<Page<BlockRequest>, PageDTO<BlockRequestDTO>> pageConverter;
    @Mock
    private PageSizeAndNumberValidator pageSizeAndNumberValidator;

    private BlockRequestServiceImpl blockRequestService;

    private UUID uuid;


    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        blockRequestService = new BlockRequestServiceImpl(
                cardService,
                userDetailsHolder,
                blockRequestRepository,
                errorMessageCreator,
                requestConverter,
                pageConverter,
                pageSizeAndNumberValidator
        );
    }

    @Test
    void testCreateBlockRequestWhenCardDoesNotExist_ShouldThrowException() {
        doReturn(User.builder().build()).when(userDetailsHolder).getUserFromSecurityContext();
        doThrow(new ValidationException("message")).when(cardService).getCardOrThrowValidationException(any());

        assertThrows(ValidationException.class, () -> blockRequestService.createBlockRequest(uuid));
    }

    @Test
    void testCreateBlockRequestWhenCardDoesNotBelongUser_ShouldThrowException() {
        doReturn(User.builder().build()).when(userDetailsHolder).getUserFromSecurityContext();
        doReturn(Card.builder().build()).when(cardService).getCardOrThrowValidationException(any());
        doThrow(new ValidationException("message")).when(cardService).checkOwnerOrThrowException(any(), any());

        assertThrows(ValidationException.class, () -> blockRequestService.createBlockRequest(uuid));
    }

    @Test
    void testCreateBlockRequestWhenInputCorrect_ShouldCallSave() {
        doReturn(User.builder().build()).when(userDetailsHolder).getUserFromSecurityContext();
        doReturn(Card.builder().build()).when(cardService).getCardOrThrowValidationException(any());
        doNothing().when(cardService).checkOwnerOrThrowException(any(), any());

        blockRequestService.createBlockRequest(uuid);

        verify(blockRequestRepository).save(any());
    }

    @Test
    void testCloseBlockRequestWhenNotFound_ShouldThrowException() {
        doReturn(Optional.empty()).when(blockRequestRepository).findById(any());
        doReturn("message").when(errorMessageCreator).createErrorMessage(any(), any());

        assertThrows(ValidationException.class, () -> blockRequestService.closeBlockRequest(uuid));
    }

    @Test
    void testCloseBlockRequestWhenFound_ShouldCallDelete() {
        doReturn(Optional.of(BlockRequest.builder().build())).when(blockRequestRepository).findById(any());

        blockRequestService.closeBlockRequest(uuid);

        verify(blockRequestRepository).delete(any());
    }

    @Test
    void testGetAllWhenValidationFails_ShouldThrowException() {
        doThrow(new ValidationException("message")).when(pageSizeAndNumberValidator).validateOrThrowValidationException(0, 0);

        assertThrows(ValidationException.class, () -> blockRequestService.getAll(0, 0));
        verify(blockRequestRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllWhenValidationFails_ShouldCallFindAndConverter() {
        doNothing().when(pageSizeAndNumberValidator).validateOrThrowValidationException(0, 1);
        doReturn(Page.empty()).when(blockRequestRepository).findAll(any(Pageable.class));
        doReturn(PageDTO.<BlockRequest>builder().build()).when(pageConverter).convert(any());

        blockRequestService.getAll(0, 1);

        verify(blockRequestRepository).findAll(any(Pageable.class));
        verify(pageConverter).convert(any());
    }

    @Test
    void testGetOneWhenNotFound_ShouldThrowValidationException() {
        doReturn(Optional.empty()).when(blockRequestRepository).findById(any());
        doReturn("message").when(errorMessageCreator).createErrorMessage(any(), any());

        assertThrows(ValidationException.class, () -> blockRequestService.getOne(uuid));
    }

    @Test
    void testGetOneWhenFound_ShouldCallConverter() {
        doReturn(Optional.of(BlockRequest.builder().build())).when(blockRequestRepository).findById(any());
        doReturn(BlockRequestDTO.builder().build()).when(requestConverter).convert(any());

        blockRequestService.getOne(uuid);

        verify(requestConverter).convert(any());
    }
}