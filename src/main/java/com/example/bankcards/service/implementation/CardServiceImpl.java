package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.*;
import com.example.bankcards.utl.CardNumberGenerator;
import com.example.bankcards.utl.EncryptionHelper;
import com.example.bankcards.utl.ErrorMessageCreator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final EncryptionHelper encryptionHelper;
    private final SecretKey secretKey;
    private final CardNumberGenerator cardNumberGenerator;

    private final StatusService statusService;

    private final UserDetailsHolder userDetailsHolder;

    private final ErrorMessageCreator errorMessageCreator;
    private final View error;

    @Override
    public CardDTO createCard(CreationCardDTO creationCardDTO) {
        Card card = Card.builder()
                .owner(User.builder().id(creationCardDTO.getOwnerId()).build())
                .expiresAt(creationCardDTO.getExpiresAt())
                .balance(new BigDecimal(0))
                .status(Status.builder().status(StatusService.ACTIVE).build())
                .build();

        Optional<Card> cardWithSameNumber;
        do{
            card.setCardNumber(cardNumberGenerator.generateCardNumber());
            card.setEncryptedCardNumber(encryptionHelper.encryptCardNumber(secretKey, card.getCardNumber()));
            cardWithSameNumber = cardRepository.findByEncryptedCardNumber(card.getEncryptedCardNumber());
        }while(cardWithSameNumber.isPresent());

        card = cardRepository.save(card);
        return convertToCardDTO(card);
    }

    @Override
    public CardDTO getCard(UUID id) throws ValidationException {
        Card card = getCardOrThrowValidationException(id);

        User user = userDetailsHolder.getUserFromSecurityContext();
        if(card.getOwner().getId().equals(user.getId()) || user.getRole().getRole().equals(RoleService.PREFIX_ROLE + RoleService.ADMIN_ROLE)){
            return convertToCardDTO(card);
        }

        throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Card does not belong user or user is not admin"));
    }

    private Card getCardOrThrowValidationException(UUID id) throws ValidationException{
        Optional<Card> card = cardRepository.findById(id);
        if(card.isEmpty()){
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Card with id not found"));
        }

        return card.get();
    }

    @Override
    public CardDTO setStatus(UUID id, String statusName) {
        Card card = getCardOrThrowValidationException(id);
        Status status = statusService.getStatusOrThrowValidationException(statusName);

        if(card.getStatus().getStatus().equals(StatusService.EXPIRED)){
            throw new ValidationException(errorMessageCreator.createErrorMessage("status", "Card has expired"));
        }
        else if(status.getStatus().equals(StatusService.EXPIRED)){
            throw new ValidationException(errorMessageCreator.createErrorMessage("status", "Status 'EXPIRED' can be set according expiration date by system"));
        }

        throw new ValidationException(errorMessageCreator.createErrorMessage("status", "Invalid status"));
    }

    @Override
    public List<CardDTO> transfer(TransferDTO transferDTO) {
        Card card1 = getCardOrThrowValidationException(transferDTO.getFromCardId());
        Card card2 = getCardOrThrowValidationException(transferDTO.getToCardId());
        BigDecimal amount = card1.getBalance();
        User user = userDetailsHolder.getUserFromSecurityContext();

        if(!(card1.getOwner().getId().equals(user.getId()) && card2.getOwner().getId().equals(user.getId()))){
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Card 'from' or card 'to' does not belong to user"));
        }

        if(card1.getBalance().compareTo(amount) < 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("amount", "Card 'from' has less money than amount"));
        }

        card1.setBalance(card1.getBalance().subtract(amount));
        card2.setBalance(card2.getBalance().add(amount));

        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);

        return List.of(convertToCardDTO(card1), convertToCardDTO(card2));
    }

    @Override
    public void deleteCard(UUID id) {
        Card card = getCardOrThrowValidationException(id);

        cardRepository.delete(card);
    }

    private CardDTO convertToCardDTO(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .balance(card.getBalance())
                .expiresAt(card.getExpiresAt())
                .status(card.getStatus().getStatus())
                .ownerId(card.getOwner().getId())
                .cardNumber(card.getCardNumber().getMask())
                .build();
    }
}