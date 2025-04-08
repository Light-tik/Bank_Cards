package com.example.bank_cards.unit_test;

import com.example.bank_cards.DataSet;
import com.example.bank_cards.dto.request.CardRequest;
import com.example.bank_cards.dto.request.LimitRequest;
import com.example.bank_cards.dto.response.CardResponse;
import com.example.bank_cards.dto.response.LimitResponse;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.StatusCard;
import com.example.bank_cards.mappers.CardMapper;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.security.SecurityContextFacade;
import com.example.bank_cards.service.*;
import com.example.bank_cards.service.impl.CardServiceImpl;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private SecurityContextFacade securityContextFacade;
    @Mock private CardMapper cardMapper;
    @Mock private CardEncryptionService cardEncryptionService;
    @Mock private LimitService limitService;
    @Mock private TransactionService transactionService;
    @Mock private UserService userService;

    @InjectMocks private CardServiceImpl cardService;

    private UserEntity user;
    private CardEntity cardEntity;
    private CardRequest cardRequest;
    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        user = DataSet.getUser();
        cardEntity = DataSet.getCard(user);
        cardRequest = DataSet.getCardRequest(cardEntity);
        cardResponse = DataSet.getCardResponse(cardEntity);
    }

    @Test
    void createCard_ShouldReturnResponse() {
        when(userService.getUserEntity(cardRequest.getOwnerId())).thenReturn(user);
        when(cardMapper.toEntity(cardRequest, cardEncryptionService)).thenReturn(cardEntity);
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardMapper.toResponse(cardEntity, cardEncryptionService)).thenReturn(cardResponse);

        CardResponse result = cardService.createCard(cardRequest);

        assertThat(result).isEqualTo(cardResponse);
        verify(cardRepository).save(cardEntity);
    }

    @Test
    void createCard_shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();
        CardRequest request = new CardRequest();
        request.setOwnerId(userId);

        when(userService.getUserEntity(userId)).thenThrow(new CustomException(ErrorCodes.USER_NOT_FOUND));

        assertThrows(CustomException.class, () -> cardService.createCard(request));
    }


    @Test
    void deleteCard_ShouldCallRepositoryAndTransactionService() {
        UUID cardId = UUID.randomUUID();

        cardService.deleteCard(cardId);

        verify(cardRepository).deleteById(cardId);
        verify(transactionService).deleteAllByCard();
    }

    @Test
    void changeCardStatus_ShouldUpdateStatus() {
        UUID cardId = cardEntity.getId();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(any())).thenReturn(cardEntity);
        when(cardMapper.toResponse(cardEntity, cardEncryptionService)).thenReturn(cardResponse);

        CardResponse result = cardService.changeCardStatus(cardId, StatusCard.BLOCKED);

        assertThat(result).isNotNull();
        assertThat(cardEntity.getStatus()).isEqualTo(StatusCard.BLOCKED);
    }

    @Test
    void deleteAllCard_ShouldRemoveOrphanCards() {
        List<CardEntity> orphanCards = List.of(cardEntity);
        when(cardRepository.findCardsWithoutUser()).thenReturn(orphanCards);

        cardService.deleteAllCard();

        verify(cardRepository).deleteAll(orphanCards);
        verify(transactionService).deleteAllByCard();
    }

    @Test
    void requestBlockCard_ShouldSetWaitingStatus() {
        UUID cardId = cardEntity.getId();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));

        cardService.requestBlockCard(cardId);

        assertThat(cardEntity.getStatus()).isEqualTo(StatusCard.WAITING_FOR_LOCK);
    }

    @Test
    void getCard_ShouldReturnMappedResponse() {
        UUID cardId = cardEntity.getId();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toResponse(cardEntity, cardEncryptionService)).thenReturn(cardResponse);

        CardResponse result = cardService.getCard(cardId);

        assertThat(result).isEqualTo(cardResponse);
    }

    @Test
    void getCardsOnPage_ShouldReturnPageableResponse() {
        Page<CardEntity> cardPage = new PageImpl<>(List.of(cardEntity));
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(cardPage);
        when(cardMapper.toListResponse(cardPage.getContent(), cardEncryptionService)).thenReturn(List.of(cardResponse));

        PageableResponse<CardResponse> result = cardService.getCardsOnPage(1, 10);

        assertEquals(cardPage.getTotalElements(), result.getNumberOfElements());
    }

    @Test
    void searchCards_ShouldReturnFilteredResults() {
        Page<CardEntity> cardPage = new PageImpl<>(List.of(cardEntity));
        when(cardRepository.searchCards(any(), any(), any(), any())).thenReturn(cardPage);
        when(cardMapper.toListResponse(cardPage.getContent(), cardEncryptionService)).thenReturn(List.of(cardResponse));

        PageableResponse<CardResponse> result = cardService.searchCards(user.getId(), StatusCard.ACTIVE, "1234", 1, 10);

        assertEquals(cardPage.getTotalElements(), result.getNumberOfElements());
    }

    @Test
    void deleteCard_shouldDeleteCardAndRelatedTransactions() {
        UUID cardId = UUID.randomUUID();

        cardService.deleteCard(cardId);

        verify(cardRepository).deleteById(cardId);
        verify(transactionService).deleteAllByCard();
    }

    @Test
    void deleteCard_shouldStillCallDeleteEvenIfCardDoesNotExist() {
        UUID cardId = UUID.randomUUID();

        doThrow(new CustomException(ErrorCodes.CARD_NOT_FOUND)).when(cardRepository).deleteById(cardId);

        assertThrows(CustomException.class, () -> cardService.deleteCard(cardId));
    }

    @Test
    void changeCardStatus_shouldUpdateStatusAndReturnResponse() {
        UUID cardId = UUID.randomUUID();
        CardEntity card = new CardEntity();
        card.setId(cardId);
        card.setStatus(StatusCard.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toResponse(card, cardEncryptionService)).thenReturn(new CardResponse());

        CardResponse result = cardService.changeCardStatus(cardId, StatusCard.BLOCKED);

        assertNotNull(result);
        assertEquals(StatusCard.BLOCKED, card.getStatus());
    }

    @Test
    void changeCardStatus_shouldThrowExceptionIfCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cardService.changeCardStatus(cardId, StatusCard.BLOCKED));
    }

    @Test
    void deleteAllCard_shouldDeleteOrphanCardsAndCallTransactionCleanup() {
        List<CardEntity> orphanCards = List.of(new CardEntity());

        when(cardRepository.findCardsWithoutUser()).thenReturn(orphanCards);

        cardService.deleteAllCard();

        verify(cardRepository).deleteAll(orphanCards);
        verify(transactionService).deleteAllByCard();
    }

    @Test
    void deleteAllCard_shouldNotFailIfNoOrphanCards() {
        when(cardRepository.findCardsWithoutUser()).thenReturn(List.of());

        assertDoesNotThrow(() -> cardService.deleteAllCard());

        verify(cardRepository).findCardsWithoutUser();
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void setMonthlyLimit_shouldDelegateAndReturnLimitResponse() {
        UUID cardId = UUID.randomUUID();
        CardEntity card = new CardEntity();
        LimitRequest request = new LimitRequest();
        request.setCardId(cardId);
        LimitResponse response = new LimitResponse();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(limitService.setMonthlyLimit(request, card)).thenReturn(response);

        LimitResponse result = cardService.setMonthlyLimit(request);

        assertEquals(response, result);
    }

    @Test
    void setMonthlyLimit_shouldThrowIfCardNotFound() {
        LimitRequest request = new LimitRequest();
        request.setCardId(UUID.randomUUID());

        when(cardRepository.findById(request.getCardId())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cardService.setMonthlyLimit(request));
    }

    @Test
    void requestBlockCard_shouldChangeStatusToWaitingForLock() {
        UUID cardId = UUID.randomUUID();
        CardEntity card = new CardEntity();
        card.setStatus(StatusCard.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        cardService.requestBlockCard(cardId);

        assertEquals(StatusCard.WAITING_FOR_LOCK, card.getStatus());
    }

    @Test
    void requestBlockCard_shouldThrowIfCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cardService.requestBlockCard(cardId));
    }

    @Test
    void getUserCards_shouldReturnMappedCardResponses() {
        UUID userId = UUID.randomUUID();
        List<CardEntity> cards = List.of(new CardEntity());
        List<CardResponse> responses = List.of(new CardResponse());

        when(securityContextFacade.getCurrentUserId()).thenReturn(userId);
        when(cardRepository.findByOwnerId(userId)).thenReturn(cards);
        when(cardMapper.toListResponse(cards, cardEncryptionService)).thenReturn(responses);

        List<CardResponse> result = cardService.getUserCards();

        assertEquals(1, result.size());
    }

    @Test
    void getUserCards_shouldReturnEmptyListIfNoCards() {
        UUID userId = UUID.randomUUID();

        when(securityContextFacade.getCurrentUserId()).thenReturn(userId);
        when(cardRepository.findByOwnerId(userId)).thenReturn(List.of());

        List<CardResponse> result = cardService.getUserCards();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCard_shouldReturnMappedCardResponse() {
        UUID cardId = UUID.randomUUID();
        CardEntity card = new CardEntity();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponse(card, cardEncryptionService)).thenReturn(new CardResponse());

        CardResponse result = cardService.getCard(cardId);

        assertNotNull(result);
    }

    @Test
    void getCard_shouldThrowIfCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cardService.getCard(cardId));
    }

    @Test
    void getCardsOnPage_shouldReturnPaginatedResponse() {
        int page = 1;
        int perPage = 2;
        List<CardEntity> entities = List.of(new CardEntity());
        List<CardResponse> responses = List.of(new CardResponse());

        Page<CardEntity> resultPage = new PageImpl<>(entities);

        when(cardRepository.findAll(PageRequest.of(0, perPage))).thenReturn(resultPage);
        when(cardMapper.toListResponse(entities, cardEncryptionService)).thenReturn(responses);

        PageableResponse<CardResponse> result = cardService.getCardsOnPage(page, perPage);

        assertEquals(resultPage.getTotalElements(), result.getNumberOfElements());
    }

    @Test
    void getCardsOnPage_shouldThrowIfPageOrPerPageInvalid() {
        assertThrows(CustomException.class, () -> cardService.getCardsOnPage(0, 5));
        assertThrows(CustomException.class, () -> cardService.getCardsOnPage(1, 0));
    }

    @Test
    void searchCards_shouldReturnFilteredCards() {
        UUID ownerId = UUID.randomUUID();
        String masked = "****-1234";
        List<CardEntity> list = List.of(new CardEntity());
        List<CardResponse> mapped = List.of(new CardResponse());

        Page<CardEntity> resultPage = new PageImpl<>(list);

        when(cardRepository.searchCards(ownerId, StatusCard.ACTIVE, masked, PageRequest.of(0, 10))).thenReturn(resultPage);
        when(cardMapper.toListResponse(list, cardEncryptionService)).thenReturn(mapped);

        PageableResponse<CardResponse> result = cardService.searchCards(ownerId, StatusCard.ACTIVE, masked, 1, 10);

        assertEquals(resultPage.getTotalElements(), result.getNumberOfElements());
    }

    @Test
    void searchCards_shouldThrowIfPaginationInvalid() {
        assertThrows(CustomException.class, () -> cardService.searchCards(null, null, null, 0, 10));
        assertThrows(CustomException.class, () -> cardService.searchCards(null, null, null, 1, 0));
    }

    @Test
    void getCardEntity_shouldReturnEntityIfExists() {
        UUID cardId = UUID.randomUUID();
        CardEntity card = new CardEntity();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardEntity result = cardService.getCardEntity(cardId);

        assertEquals(card, result);
    }

    @Test
    void getCardEntity_shouldThrowIfNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cardService.getCardEntity(cardId));
    }
}
