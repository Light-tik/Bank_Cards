package com.example.bank_cards.repositories;

import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.enums.StatusCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, UUID> {

    @Query("""
        SELECT c FROM CardEntity c
        WHERE (:ownerId IS NULL OR c.owner.id = :ownerId)
        AND (:status IS NULL OR c.status = :status)
        AND (:maskedNumber IS NULL OR c.number LIKE %:maskedNumber%)
    """)
    Page<CardEntity> searchCards(UUID ownerId, StatusCard status, String maskedNumber, Pageable pageable);

    @Query("SELECT t FROM CardEntity t LEFT JOIN t.owner n WHERE n.id IS NULL")
    List<CardEntity> findCardsWithoutUser();

    List<CardEntity> findByOwnerId(UUID userId);
}
