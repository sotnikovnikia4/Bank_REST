package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Card")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String encryptedCardNumber;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(nullable = false)
    private LocalDate expiresAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @Transient
    private CardNumber cardNumber;
}
