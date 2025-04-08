package com.example.bank_cards.unit_test;

import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.LimitEntity;
import com.example.bank_cards.mappers.LimitMapper;
import com.example.bank_cards.repositories.LimitRepository;
import com.example.bank_cards.service.impl.LimitServiceImpl;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class LimitServiceTest {

    @Mock
    private LimitMapper limitMapper;

    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private LimitServiceImpl limitService;

    private CardEntity card;
    private LimitEntity limitEntity;
    private LimitRequest limitRequest;

    @BeforeEach
    void setUp() {
        card = new CardEntity();
        card.setId(UUID.randomUUID());

        limitRequest = new LimitRequest(card.getId(), 5000.0);

        limitEntity = new LimitEntity();
        limitEntity.setCard(card);
        limitEntity.setMonthlyLimit(5000.0);
        limitEntity.setUsedThisMonth(1000.0);
    }

    @Test
    void setMonthlyLimit_shouldSetLimitAndReturnResponse() {
        when(limitMapper.toEntity(any(LimitRequest.class))).thenReturn(limitEntity);
        when(limitMapper.toResponse(any(LimitEntity.class))).thenReturn(new LimitResponse(card.getId(), 5000.0, 0.0));
        when(limitRepository.save(any(LimitEntity.class))).thenReturn(limitEntity);

        LimitResponse response = limitService.setMonthlyLimit(limitRequest, card);

        assertEquals(card.getId(), response.getCardId());
        assertEquals(5000.0, response.getMonthlyLimit());
        assertEquals(0.0, response.getUsedThisMonth());
        verify(limitRepository, times(1)).save(any(LimitEntity.class));
    }

    @Test
    void checkLimit_shouldReturnTrueWhenLimitIsNotExceeded() {
        when(limitRepository.findByCardId(card.getId())).thenReturn(Optional.of(limitEntity));

        boolean result = limitService.checkLimit(card.getId(), 3000.0);

        assertTrue(result);
    }

    @Test
    void checkLimit_shouldThrowExceptionWhenLimitIsExceeded() {
        when(limitRepository.findByCardId(card.getId())).thenReturn(Optional.of(limitEntity));

        CustomException exception = assertThrows(CustomException.class, () -> {
            limitService.checkLimit(card.getId(), 5000.0);
        });

        assertEquals(ErrorCodes.LIMIT_NOT_SET, exception.getErrorCodes());
    }

    @Test
    void sumAmount_shouldAddAmountAndSaveLimit() {
        when(limitRepository.findByCardId(card.getId())).thenReturn(Optional.of(limitEntity));

        limitService.sumAmount(2000.0, card.getId());

        assertEquals(3000.0, limitEntity.getUsedThisMonth());
        verify(limitRepository, times(1)).save(any(LimitEntity.class));
    }

    @Test
    void sumAmount_shouldThrowExceptionWhenLimitExceeded() {
        when(limitRepository.findByCardId(card.getId())).thenReturn(Optional.of(limitEntity));

        CustomException exception = assertThrows(CustomException.class, () -> {
            limitService.sumAmount(5000.0, card.getId());
        });

        assertEquals(ErrorCodes.LIMIT_EXCEEDED, exception.getErrorCodes());
    }

    @Test
    void resetAllLimitsMonthly_shouldResetAllLimits() {
        List<LimitEntity> limits = new ArrayList<>();
        limits.add(limitEntity);

        when(limitRepository.findAll()).thenReturn(limits);
        when(limitRepository.saveAll(anyList())).thenReturn(limits);

        limitService.resetAllLimitsMonthly();

        assertEquals(0.0, limitEntity.getUsedThisMonth());
        verify(limitRepository, times(1)).saveAll(anyList());
    }
}
