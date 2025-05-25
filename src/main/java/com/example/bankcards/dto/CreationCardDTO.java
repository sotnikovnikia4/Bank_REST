package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreationCardDTO {

    @NotNull(message = "You should point when card expires")
    private LocalDate expiresAt;

    @NotNull(message = "You should point owner's id")
    private UUID ownerId;
}
