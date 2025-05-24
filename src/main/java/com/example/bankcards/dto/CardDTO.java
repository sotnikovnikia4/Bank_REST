package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private UUID id;

    private LocalDate expiresAt;

    private String status;

    private BigDecimal balance;

    private String cardNumber;

    private UUID ownerId;
}
