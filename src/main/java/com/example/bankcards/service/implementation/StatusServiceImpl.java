package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Status;
import com.example.bankcards.repository.StatusRepository;
import com.example.bankcards.service.StatusService;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;
    private final ErrorMessageCreator errorMessageCreator;

    @Override
    public Status getStatusOrThrowValidationException(String statusName) throws ValidationException {
        Optional<Status> status = statusRepository.findByStatus(statusName);

        if(status.isEmpty()) {
            throw new ValidationException(errorMessageCreator.createErrorMessage("role", "This role does not exists"));
        }

        return status.get();
    }
}
