package com.example.bank_cards.mappers;

import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.TransactionResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.TransactionEntity;
import com.example.bank_cards.enums.TransactionType;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "card", source = "card")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "type", source = "transactionType")
    TransactionEntity toEntity(TransactionRequest request, CardEntity card, TransactionType transactionType);


    @Named("toResponse")
    @Mapping(target = "cardId", source = "card.id")
    TransactionResponse toResponse(TransactionEntity transaction);

    @IterableMapping(qualifiedByName = "toResponse")
    List<TransactionResponse> toTransactionResponseList(List<TransactionEntity> entities);
}
