package com.example.bank_cards.repositories;

import com.example.bank_cards.entity.LimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LimitRepository extends JpaRepository<LimitEntity, UUID> {

    Optional<LimitEntity> findByCardId(UUID cardId);
}
