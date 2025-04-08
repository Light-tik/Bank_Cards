package com.example.bank_cards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "limit")
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double monthlyLimit;

    private Double usedThisMonth;

    private LocalDateTime lastReset;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", nullable = false, unique = true)
    private CardEntity card;
}
