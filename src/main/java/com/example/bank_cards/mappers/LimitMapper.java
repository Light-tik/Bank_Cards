package com.example.bank_cards.mappers;

import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.entity.LimitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LimitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedThisMonth", ignore = true)
    @Mapping(target = "lastReset", ignore = true)
    @Mapping(target = "card.id", source = "cardId")
    LimitEntity toEntity(LimitRequest request);

    @Mapping(target = "cardId", source = "card.id")
    LimitResponse toResponse(LimitEntity entity);
}
