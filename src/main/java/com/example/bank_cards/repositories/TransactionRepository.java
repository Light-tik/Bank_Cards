package com.example.bank_cards.repositories;

import com.example.bank_cards.entity.TransactionEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT t FROM TransactionEntity t LEFT JOIN t.card n WHERE n.id IS NULL")
    Set<TransactionEntity> findTransactionEntitiesWithoutCards();

    List<TransactionEntity> findTransactionEntitiesByCardId(UUID cardId);

    @Query("""
    SELECT t FROM TransactionEntity t
    WHERE (:cardId IS NULL OR t.card.id = :cardId)
    AND (:amount IS NULL OR t.amount = :amount)
    AND (:fromDate IS NULL OR t.timestamp >= :fromDate)
    AND (:toDate IS NULL OR t.timestamp <= :toDate)
""")
    Page<TransactionEntity> filterTransactions(
            @Nullable UUID cardId,
            @Nullable Double amount,
            @Nullable LocalDateTime fromDate,
            @Nullable LocalDateTime toDate,
            Pageable pageable
    );
}
