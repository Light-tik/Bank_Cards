package com.example.bank_cards.service;

import java.util.UUID;

public interface UserCardService {

    void deleteUserWithCards(UUID targetUserId);
}
