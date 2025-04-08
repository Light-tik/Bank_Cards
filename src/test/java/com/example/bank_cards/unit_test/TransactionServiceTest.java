package com.example.bank_cards.unit_test;

import com.example.bank_cards.DataSet;
import com.example.bank_cards.dto.request.TransactionRequest;
import com.example.bank_cards.dto.response.PageableResponse;
import com.example.bank_cards.dto.response.TransactionResponse;
import com.example.bank_cards.entity.CardEntity;
import com.example.bank_cards.entity.TransactionEntity;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.repositories.CardRepository;
import com.example.bank_cards.repositories.TransactionRepository;
import com.example.bank_cards.service.LimitService;
import com.example.bank_cards.service.impl.TransactionServiceImpl;
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
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private LimitService limitService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UserEntity user;
    private CardEntity fromCard;
    private CardEntity toCard;

    @BeforeEach
    void setup() {
        user = DataSet.getUser();
        fromCard = DataSet.getCard(user);
        toCard = DataSet.getCard(user);
    }

    @Test
    void transfer_Success() {
        TransactionRequest request = DataSet.getTransferRequest(fromCard.getId(), toCard.getId(), 100.0);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.transfer(request);

        assertThat(response.getAmount()).isEqualTo(100.0);
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void transfer_CardNotFound_ThrowsException() {
        TransactionRequest request = DataSet.getTransferRequest(UUID.randomUUID(), toCard.getId(), 100.0);
        when(cardRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCodes.CARD_NOT_FOUND.getMessage());
    }

    @Test
    void replenish_Success() {
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.replenish(fromCard.getId(), 200.0);

        assertThat(response.getAmount()).isEqualTo(200.0);
        verify(transactionRepository).save(any());
    }

    @Test
    void debit_Success() {
        fromCard.setBalance(500.0);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.debit(fromCard.getId(), 300.0);

        assertThat(response.getAmount()).isEqualTo(-300.0);
        verify(transactionRepository).save(any());
    }

    @Test
    void debit_InsufficientFunds_ThrowsException() {
        fromCard.setBalance(100.0);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));

        assertThatThrownBy(() -> transactionService.debit(fromCard.getId(), 200.0))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCodes.INSUFFICIENT_FUNDS.getMessage());
    }

    @Test
    void getTransactionsByCard_ReturnsList() {
        List<TransactionEntity> txList = DataSet.getTransactionList(fromCard, 3);
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(transactionRepository.findTransactionEntitiesByCardId(fromCard.getId())).thenReturn(txList);

        List<TransactionResponse> responses = transactionService.getTransactionsByCard(fromCard.getId());

        assertThat(responses).hasSize(3);
    }

    @Test
    void getTransactionsOnPage_ReturnsPage() {
        Page<TransactionEntity> txPage = DataSet.getTransactionEntityPage(fromCard, 3, 0, 3);
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(txPage);

        PageableResponse<TransactionResponse> responsePage = transactionService.getTransactionsOnPage(0, 3);

        assertThat(responsePage.getContent()).hasSize(3);
    }

    @Test
    void filterTransactions_ReturnsFilteredList() {
        List<TransactionEntity> txList = DataSet.getTransactionList(fromCard, 5);
        Page<TransactionEntity> page = new PageImpl<>(txList);

        when(transactionRepository.filterTransactions(any(), any(), any(), any(), any())).thenReturn(page);

        PageableResponse<TransactionResponse> filtered = transactionService.filterTransactions(fromCard.getId(), null, null, null, 1, 1);

        assertThat(filtered).isNotNull();
    }

    @Test
    void getTransactionsByCard_WrongUser_ThrowsException() {
        UserEntity anotherUser = DataSet.getUser();
        anotherUser.setId(UUID.randomUUID());
        fromCard.setOwner(anotherUser);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));

        assertThatThrownBy(() -> transactionService.getTransactionsByCard(fromCard.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCodes.NO_RIGHTS.getMessage());
    }
}
