package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotNull(message = "You should point id of card 'from'")
    private UUID fromCardId;

    @NotNull(message = "You should point id of card 'to'")
    private UUID toCardId;

    @DecimalMin(value = "0,01", message = "Amount should be greater than 0")
    private BigDecimal amount;
}
