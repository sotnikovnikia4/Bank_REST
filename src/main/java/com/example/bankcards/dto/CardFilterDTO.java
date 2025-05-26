package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFilterDTO {
    private String ownerName;
    private String status;

    private LocalDate expireBefore;
    private LocalDate expireAfter;

    @DecimalMin(value = "0,01", message = "balanceMin should be greater than 0")
    private BigDecimal balanceMin;
    @DecimalMin(value = "0,01", message = "balanceMax should be greater than 0")
    private BigDecimal balanceMax;
}
