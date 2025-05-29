package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreationCardDTO;
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

import javax.crypto.SecretKey;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private UUID id;

    @BeforeEach
    @SneakyThrows
    void setUp(){
        id = UUID.randomUUID();
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
    void testTransfer() {
        setUpGetCard(Card.builder().build());

    }

    private void setUpGetCard(Card card){
        ProxyGetCard handler = new ProxyGetCard(card);
        ClassLoader cardServiceClassLoader
        CardService cardService = Proxy.newProxyInstance()
    }

    @Test
    void checkOwnerOrThrowException() {
    }

    @Test
    void getCardsLikeAdmin() {
    }

    @Test
    void getCardsLikeUser() {
    }

    @Test
    void getCardLikeUser() {
    }

    @Test
    void addMoney() {
    }
}