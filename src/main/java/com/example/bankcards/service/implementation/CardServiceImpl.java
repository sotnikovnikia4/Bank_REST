package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.StatusService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.CardNumberEncryption;
import com.example.bankcards.util.ErrorMessageCreator;
import com.example.bankcards.util.PageSizeAndNumberValidator;
import com.example.bankcards.util.converter.ConverterPageDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardNumberEncryption cardNumberEncryption;
    private final SecretKey secretKey;
    private final CardNumberGenerator cardNumberGenerator;

    private final StatusService statusService;

    private final UserDetailsHolder userDetailsHolder;

    private final ErrorMessageCreator errorMessageCreator;
    private final UserService userService;
    private final Converter<Card, CardDTO> cardConverter;
    private final ConverterPageDTO<Card, CardDTO> cardPageConverter;

    private final PageSizeAndNumberValidator pageSizeAndNumberValidator;

    @Override
    public CardDTO createCard(CreationCardDTO creationCardDTO) {
        if(LocalDate.now().isAfter(creationCardDTO.getExpiresAt())){
            throw new ValidationException(errorMessageCreator.createErrorMessage("expiresAt", "Value is less than current date"));
        }
        User owner = userService.getUserOrThrowValidationException(creationCardDTO.getOwnerId(), "ownerId");
        Status status = statusService.getStatusOrThrowValidationException(StatusService.ACTIVE);

        Card card = Card.builder()
                .owner(owner)
                .expiresAt(creationCardDTO.getExpiresAt())
                .balance(new BigDecimal(0))
                .status(status)
                .build();

        Optional<Card> cardWithSameNumber;
        do{
            card.setCardNumber(cardNumberGenerator.generateCardNumber());
            card.setEncryptedCardNumber(cardNumberEncryption.encryptCardNumber(secretKey, card.getCardNumber()));
            cardWithSameNumber = cardRepository.findByEncryptedCardNumber(card.getEncryptedCardNumber());
        }while(cardWithSameNumber.isPresent());

        card = cardRepository.save(card);
        return cardConverter.convert(card);
    }

    @Override
    public CardDTO getCardLikeAdmin(UUID id) throws ValidationException {
        Card card = getCardOrThrowValidationException(id);
        return cardConverter.convert(card);
    }

    @Override
    public Card getCardOrThrowValidationException(UUID id) throws ValidationException {
        Optional<Card> card = cardRepository.findById(id);
        if(card.isEmpty()){
            throw new ValidationException(errorMessageCreator.createErrorMessage("id", "Card with id not found"));
        }

        card.get().setCardNumber(cardNumberEncryption.decryptCardNumber(secretKey, card.get().getEncryptedCardNumber()));

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
            throw new ValidationException(errorMessageCreator.createErrorMessage("status", "Status 'EXPIRED' can be set according expiration date and only by system"));
        }

        card.setStatus(status);
        card = cardRepository.save(card);

        return cardConverter.convert(card);
    }

    @Override
    public List<CardDTO> transfer(TransferDTO transferDTO) {
        Card card1 = getCardOrThrowValidationException(transferDTO.getFromCardId());
        Card card2 = getCardOrThrowValidationException(transferDTO.getToCardId());

        if(card1.getId().equals(card2.getId())){
            throw new ValidationException(errorMessageCreator.createErrorMessage("toCardId", "toCardId should be different from fromCardId"));
        }

        BigDecimal amount = transferDTO.getAmount();
        User user = userDetailsHolder.getUserFromSecurityContext();

        checkOwnerOrThrowException(card1, user.getId());
        checkOwnerOrThrowException(card2, user.getId());

        if(card1.getBalance().compareTo(amount) < 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("amount", "Card 'from' has less money than amount"));
        }

        card1.setBalance(card1.getBalance().subtract(amount));
        card2.setBalance(card2.getBalance().add(amount));

        card1 = cardRepository.save(card1);
        card2 = cardRepository.save(card2);

        return List.of(cardConverter.convert(card1), cardConverter.convert(card2));
    }

    @Override
    public void deleteCard(UUID id) {
        Card card = getCardOrThrowValidationException(id);

        cardRepository.delete(card);
    }

    @Override
    public void checkOwnerOrThrowException(Card card, UUID userId){
        if(card.getOwner().getId().compareTo(userId) != 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("card", "Card does not belong user"));
        }
    }

    @Override
    public PageDTO<CardDTO> getCardsLikeAdmin(int pageNumber, int pageSize, CardFilterDTO cardFilterDTO) {
        pageSizeAndNumberValidator.validateOrThrowValidationException(pageNumber, pageSize);

        Page<Card> page = cardRepository.findAll(
                findCardsSpecificationLikeAdmin(cardFilterDTO),
                PageRequest.of(pageNumber, pageSize)
        );

        return cardPageConverter.convert(page);
    }

    @Override
    public PageDTO<CardDTO> getCardsLikeUser(int pageNumber, int pageSize, CardFilterDTO cardFilterDTO) {
        pageSizeAndNumberValidator.validateOrThrowValidationException(pageNumber, pageSize);

        Page<Card> page = cardRepository.findAll(
                findCardsSpecificationLikeUser(cardFilterDTO),
                PageRequest.of(pageNumber, pageSize)
        );

        return cardPageConverter.convert(page);
    }

    @Override
    public CardDTO getCardLikeUser(UUID cardId) {
        Card card = getCardOrThrowValidationException(cardId);
        User user = userDetailsHolder.getUserFromSecurityContext();
        checkOwnerOrThrowException(card, user.getId());
        return cardConverter.convert(card);
    }

    @Override
    public CardDTO addMoney(UUID cardId, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new ValidationException(errorMessageCreator.createErrorMessage("amount", "Amount should be greater than 0"));
        }

        Card card = getCardOrThrowValidationException(cardId);
        card.setBalance(card.getBalance().add(amount));

        card = cardRepository.save(card);

        return cardConverter.convert(card);
    }

    private Specification<Card> findCardsSpecificationLikeUser(CardFilterDTO cardFilterDTO){
        return (root, query, builder) -> {
            List<Predicate> predicates = findCardsSpecification(cardFilterDTO, root, builder);

            User user = userDetailsHolder.getUserFromSecurityContext();
            predicates.add(builder.equal(root.get(Card_.owner), user));

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private List<Predicate> findCardsSpecification(CardFilterDTO cardFilterDTO, Root<Card> root, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if(cardFilterDTO.getStatus() != null && !cardFilterDTO.getStatus().isBlank()){
            Join<Card, Status> statusJoin = root.join(Card_.STATUS);
            predicates.add(builder.equal(statusJoin.get(Status_.STATUS), cardFilterDTO.getStatus()));
        }

        if (cardFilterDTO.getBalanceMin() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Card_.BALANCE), cardFilterDTO.getBalanceMin()));
        }
        if (cardFilterDTO.getBalanceMax() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Card_.BALANCE), cardFilterDTO.getBalanceMax()));
        }

        if(cardFilterDTO.getExpireBefore() != null){
            predicates.add(builder.lessThanOrEqualTo(root.get(Card_.EXPIRES_AT), cardFilterDTO.getExpireBefore()));
        }
        if(cardFilterDTO.getExpireAfter() != null){
            predicates.add(builder.greaterThanOrEqualTo(root.get(Card_.EXPIRES_AT), cardFilterDTO.getExpireAfter()));
        }
        return predicates;
    }

    private Specification<Card> findCardsSpecificationLikeAdmin(CardFilterDTO cardFilterDTO){
        return (root, query, builder) -> {
            List<Predicate> predicates = findCardsSpecification(cardFilterDTO, root, builder);
            if(cardFilterDTO.getOwnerName() != null && !cardFilterDTO.getOwnerName().isBlank()){
                Join<Card, User> ownerJoin = root.join(Card_.OWNER);
                predicates.add(builder.like(ownerJoin.get(User_.name), "%" + cardFilterDTO.getOwnerName() + "%"));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}