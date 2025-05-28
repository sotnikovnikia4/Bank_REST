package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Status;
import com.example.bankcards.repository.StatusRepository;
import com.example.bankcards.util.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class StatusServiceImplTest {
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private ErrorMessageCreator errorMessageCreator;

    @InjectMocks
    private StatusServiceImpl statusService;

    @Test
    void testGetStatusOrThrowValidationException_ShouldThrowValidationException() {
        doReturn(Optional.empty()).when(statusRepository).findByStatus(any());
        doReturn("message").when(errorMessageCreator).createErrorMessage(anyString(), anyString());

        assertThrows(ValidationException.class, () -> statusService.getStatusOrThrowValidationException("status"));
    }

    @Test
    void testGetStatusOrThrowValidationException_ShouldNotThrowValidationException() {
        doReturn(Optional.of(Status.builder().build())).when(statusRepository).findByStatus(any());

        assertDoesNotThrow(() -> statusService.getStatusOrThrowValidationException("status"));
    }
}