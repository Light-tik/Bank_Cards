package com.example.bank_cards.service.impl;

import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.LimitEntity;
import com.example.bank_cards.mappers.LimitMapper;
import com.example.bank_cards.repositories.LimitRepository;
import com.example.bank_cards.service.LimitService;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LimitServiceImpl implements LimitService {

    private final LimitMapper limitMapper;

    private final LimitRepository limitRepository;

    @Override
    public LimitResponse setMonthlyLimit(LimitRequest request, CardEntity card) {
        LimitEntity limit = limitMapper.toEntity(request);
        limit.setCard(card);
        limitRepository.save(limit);
        return limitMapper.toResponse(limit);
    }

    @Override
    public boolean checkLimit(UUID cardId, Double amount) {
        LimitEntity limit = limitRepository.findByCardId(cardId)
                .orElseThrow(() -> new CustomException(ErrorCodes.LIMIT_NOT_SET));

        return (limit.getUsedThisMonth() + amount) <= limit.getMonthlyLimit();
    }

    @Override
    public void sumAmount(Double amount, UUID cardId) {
        LimitEntity limit = limitRepository.findByCardId(cardId)
                .orElseThrow(() -> new CustomException(ErrorCodes.LIMIT_NOT_SET));

        double newUsed = limit.getUsedThisMonth() + amount;

        if (newUsed > limit.getMonthlyLimit()) {
            throw new CustomException(ErrorCodes.LIMIT_EXCEEDED);
        }

        limit.setUsedThisMonth(newUsed);
        limitRepository.save(limit);
    }

    @Override
    public LimitResponse getLimit(UUID cardId) {
        LimitEntity limit = limitRepository.findByCardId(cardId).orElseThrow(() -> new CustomException(ErrorCodes.LIMIT_NOT_SET));
        return limitMapper.toResponse(limit);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetAllLimitsMonthly() {
        List<LimitEntity> limits = limitRepository.findAll();
        for (LimitEntity limit : limits) {
            limit.setUsedThisMonth(0.0);
            limit.setLastReset(LocalDateTime.now());
        }
        limitRepository.saveAll(limits);
    }
}
