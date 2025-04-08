package com.example.bank_cards.mappers;

import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.response.CardResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.service.CardEncryptionService;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", constant = "0.0")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "number", expression = "java(encryptionService.encrypt(request.getCardNumber()))")
    @Mapping(target = "owner", ignore = true)
    CardEntity toEntity(CardRequest request, @Context CardEncryptionService encryptionService);

    @Mapping(target = "maskedNumber", expression = "java(encryptionService.mask(card.getNumber()))")
    @Mapping(target = "ownerId", source = "owner.id")
    @Named("toResponse")
    CardResponse toResponse(CardEntity card, @Context CardEncryptionService encryptionService);

    @IterableMapping(qualifiedByName = "toResponse")
    List<CardResponse> toListResponse(List<CardEntity> cards, @Context CardEncryptionService encryptionService);
}
