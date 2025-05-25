package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.UUID;

@Entity
@Table(name = "BlockRequests")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false, referencedColumnName = "id")
    private Card card;
}