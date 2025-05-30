package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserDetailsHolder;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.StatusService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardNumberEncryption;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.ErrorMessageCreator;
import com.example.bankcards.util.PageSizeAndNumberValidator;
import com.example.bankcards.util.converter.ConverterPageDTO;
import jakarta.validation.ValidationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.crypto.SecretKey;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardNumberEncryption cardNumberEncryption;
    @Mock
    private SecretKey secretKey;
    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @Mock
    private StatusService statusService;

    @Mock
    private UserDetailsHolder userDetailsHolder;

    @Mock
    private ErrorMessageCreator errorMessageCreator;
    @Mock
    private UserService userService;
    @Mock
    private Converter<Card, CardDTO> cardConverter;
    @Mock
    private ConverterPageDTO<Card, CardDTO> cardPageConverter;

    @Mock
    private PageSizeAndNumberValidator pageSizeAndNumberValidator;

    @InjectMocks
    private CardServiceImpl cardService;

    private final UUID id = UUID.fromString("d1ce02c7-b511-4c75-826a-4bbcd7baa9f6");
    private final UUID id2 = UUID.fromString("769f47be-8ff5-48d1-89fd-081158e213ac");

    @BeforeEach
    @SneakyThrows
    void setUp(){
        Field fieldCardConverter = CardServiceImpl.class.getDeclaredField("cardConverter");
        Field fieldPageCardConverter = CardServiceImpl.class.getDeclaredField("cardPageConverter");
        fieldCardConverter.setAccessible(true);
        fieldPageCardConverter.setAccessible(true);
        fieldCardConverter.set(cardService, cardConverter);
        fieldPageCardConverter.set(cardService, cardPageConverter);
    }

    @Test
    void testCreateCardWhenDTOExpired_ShouldThrowValidationException() {
        CreationCardDTO creationCardDTO = CreationCardDTO.builder().expiresAt(LocalDate.MIN).build();
        doReturn("").when(errorMessageCreator).createErrorMessage(anyString(), anyString());

        assertThrows(ValidationException.class, () -> cardService.createCard(creationCardDTO));
    }

    @Test
    void testCreateCardWhenOwnerNotFound_ShouldThrowValidationException() {
        CreationCardDTO creationCardDTO = CreationCardDTO.builder().expiresAt(LocalDate.MAX).build();
        doThrow(new ValidationException()).when(userService).getUserOrThrowValidationException(any(), anyString());

        assertThrows(ValidationException.class, () -> cardService.createCard(creationCardDTO));
    }

    @Test
    void testCreateCardWhenStatusNotFound_ShouldThrowValidationException() {
        CreationCardDTO creationCardDTO = CreationCardDTO.builder().expiresAt(LocalDate.MAX).ownerId(id).build();
        doReturn(User.builder().id(id).build()).when(userService).getUserOrThrowValidationException(any(), anyString());
        doThrow(new ValidationException()).when(statusService).getStatusOrThrowValidationException(anyString());

        assertThrows(ValidationException.class, () -> cardService.createCard(creationCardDTO));
    }

    @Test
    void testCreateCardWhenInputCorrect_ShouldCallConverterAndSave() {
        CreationCardDTO creationCardDTO = CreationCardDTO.builder().expiresAt(LocalDate.MAX).ownerId(id).build();
        doReturn(User.builder().id(id).build()).when(userService).getUserOrThrowValidationException(any(), anyString());
        doAnswer(answer -> Status.builder().status(answer.getArgument(0)).build()).when(statusService).getStatusOrThrowValidationException(anyString());

        AtomicReference<Card> createdCard = new AtomicReference<>();

        doReturn(CardNumber.builder().build()).when(cardNumberGenerator).generateCardNumber();
        doReturn(new byte[]{}).when(cardNumberEncryption).encryptCardNumber(any(), any());
        doReturn(Optional.empty()).when(cardRepository).findByEncryptedCardNumber(any());
        doAnswer(answer -> {
            createdCard.set(answer.getArgument(0));
            return answer.getArgument(0);
        }).when(cardRepository).save(any());

        cardService.createCard(creationCardDTO);

        Card card = createdCard.get();

        assertNotNull(card.getCardNumber());
        assertNotNull(card.getEncryptedCardNumber());
        assertEquals(BigDecimal.ZERO, card.getBalance());
        assertNotNull(card.getStatus());
        assertNotNull(card.getOwner());
        assertNotNull(card.getExpiresAt());

        verify(cardConverter).convert(any());
        verify(cardRepository).save(any());
    }

    @Test
    void testGetCardOrThrowValidationExceptionWhenCardNotFound_ShouldThrowValidationException() {
        doReturn(Optional.empty()).when(cardRepository).findById(id);
        doReturn("").when(errorMessageCreator).createErrorMessage(anyString(), anyString());

        assertThrows(ValidationException.class, () -> cardService.getCardOrThrowValidationException(id));
    }

    @Test
    void testGetCardOrThrowValidationExceptionWhenInputCorrect_ShouldReturnCardAndSetDecryptedCardNumber(){
        doReturn(Optional.of(Card.builder().encryptedCardNumber(new byte[]{}).build())).when(cardRepository).findById(id);
        doReturn(CardNumber.builder().build()).when(cardNumberEncryption).decryptCardNumber(any(), any());

        Card card = cardService.getCardOrThrowValidationException(id);
        assertNotNull(card.getCardNumber());
        verify(cardNumberEncryption).decryptCardNumber(any(), any());
    }

    @Test
    void testSetStatusWhenStatusExpired_ShouldThrowValidationException(){
        Status status = Status.builder().status(StatusService.EXPIRED).build();
        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).status(Status.builder().status(StatusService.ACTIVE).build()).build())).when(cardRepository).findById(any());
        doReturn(Status.builder().status(status.getStatus()).build()).when(statusService).getStatusOrThrowValidationException(anyString());

        assertThrows(ValidationException.class, () -> cardService.setStatus(id, status.getStatus()));
    }

    @Test
    void testSetStatusWhenCardExpired_ShouldThrowValidationException(){
        Status status = Status.builder().status(StatusService.ACTIVE).build();
        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).status(Status.builder().status(StatusService.EXPIRED).build()).build())).when(cardRepository).findById(any());
        doReturn(Status.builder().status(status.getStatus()).build()).when(statusService).getStatusOrThrowValidationException(anyString());

        assertThrows(ValidationException.class, () -> cardService.setStatus(id, status.getStatus()));
    }

    @Test
    void testSetStatusWhenInputCorrect_ShouldCallConverterAndSave(){
        Status status = Status.builder().status(StatusService.ACTIVE).build();
        Card card = Card.builder().id(UUID.randomUUID()).status(status).build();

        doAnswer(answer -> Optional.of(card)).when(cardRepository).findById(any());

        doReturn(Status.builder().status(status.getStatus()).build()).when(statusService).getStatusOrThrowValidationException(anyString());

        cardService.setStatus(id, status.getStatus());
        assertEquals(status, card.getStatus());
        verify(cardConverter).convert(any());
        verify(cardRepository).save(any());
    }

    @Test
    void testTransferWhenCardsTheSame_ShouldThrowValidationException(){
        doReturn("").when(errorMessageCreator).createErrorMessage(anyString(), anyString());
        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).build())).when(cardRepository).findById(any());

        TransferDTO transferDTO = TransferDTO.builder().amount(BigDecimal.TEN).fromCardId(id).toCardId(id).build();

        assertThrows(ValidationException.class, () -> cardService.transfer(transferDTO));
    }

    @Test
    void testTransferWhenCardsNotBelongUser_ShouldThrowValidationException(){
        User user = User.builder().id(id).build();

        doReturn("").when(errorMessageCreator).createErrorMessage(anyString(), anyString());
        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).balance(BigDecimal.TEN).owner(User.builder().id(id2).build()).build())).when(cardRepository).findById(any());
        doReturn(user).when(userDetailsHolder).getUserFromSecurityContext();

        TransferDTO transferDTO = TransferDTO.builder().amount(BigDecimal.TEN).fromCardId(id).toCardId(id2).build();

        assertThrows(ValidationException.class, () -> cardService.transfer(transferDTO)).getMessage();
    }

    @Test
    void testTransferWhenAmountGreaterThanBalanceOnCardFrom_ShouldThrowValidationException(){
        User user = User.builder().id(id).build();

        doReturn("").when(errorMessageCreator).createErrorMessage(anyString(), anyString());
        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).balance(BigDecimal.TEN).owner(User.builder().id(id2).build()).build())).when(cardRepository).findById(any());
        doReturn(user).when(userDetailsHolder).getUserFromSecurityContext();

        TransferDTO transferDTO = TransferDTO.builder().amount(BigDecimal.TEN).fromCardId(id).toCardId(id2).build();

        assertThrows(ValidationException.class, () -> cardService.transfer(transferDTO));
    }

    @Test
    void testTransferWhenCorrect_ShouldCallConverterAndSaveAndChangeBalance(){
        User user = User.builder().id(id).build();

        doAnswer(answer -> Optional.of(Card.builder().id(answer.getArgument(0)).balance(BigDecimal.TEN).owner(User.builder().id(id).build()).build())).when(cardRepository).findById(any());
        doReturn(user).when(userDetailsHolder).getUserFromSecurityContext();
        doAnswer(answer -> answer.getArgument(0)).when(cardRepository).save(any());
        doAnswer(answer -> CardDTO.builder().balance(((Card)answer.getArgument(0)).getBalance()).build()).when(cardConverter).convert(any());

        TransferDTO transferDTO = TransferDTO.builder().amount(BigDecimal.ONE).fromCardId(id).toCardId(id2).build();

        List<CardDTO> actual = cardService.transfer(transferDTO);

        assertEquals(BigDecimal.TEN.subtract(BigDecimal.ONE), actual.get(0).getBalance());
        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), actual.get(1).getBalance());
        verify(cardRepository, times(2)).save(any());
        verify(cardConverter, times(2)).convert(any());
    }

    @Test
    void testCheckOwnerOrThrowExceptionWhenCardNotBelongUser_ShouldThrowValidationException(){
        Card card = Card.builder().id(id).owner(User.builder().id(id).build()).build();

        assertThrows(ValidationException.class, () -> cardService.checkOwnerOrThrowException(card, id2));
    }

    @Test
    void testCheckOwnerOrThrowExceptionWhenCardBelongsUser_ShouldReturnNothing(){
        Card card = Card.builder().id(id).owner(User.builder().id(id).build()).build();

        assertDoesNotThrow(() -> cardService.checkOwnerOrThrowException(card, id));
    }

    @Test
    void testGetCardsLikeAdminWhenCorrect_ShouldCallConverterAndFindAll(){
        doNothing().when(pageSizeAndNumberValidator).validateOrThrowValidationException(anyInt(), anyInt());

        doReturn(Page.empty()).when(cardRepository).findAll(any(Specification.class), any(Pageable.class));

        cardService.getCardsLikeAdmin(0, 1, null);

        verify(cardPageConverter).convert(any());
        verify(cardRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetCardsLikeUserWhenCorrect_ShouldCallConverterAndFindAll(){
        doNothing().when(pageSizeAndNumberValidator).validateOrThrowValidationException(anyInt(), anyInt());

        doReturn(Page.empty()).when(cardRepository).findAll(any(Specification.class), any(Pageable.class));

        cardService.getCardsLikeUser(0, 1, null);

        verify(cardPageConverter).convert(any());
        verify(cardRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetCardLikeUserWhenCorrect_ShouldCallConverter(){
        User user = User.builder().id(id).build();
        doReturn(user).when(userDetailsHolder).getUserFromSecurityContext();
        doReturn(Optional.of(Card.builder().id(id).owner(user).build())).when(cardRepository).findById(any());

        cardService.getCardLikeUser(id);

        verify(cardConverter).convert(any());
    }
}