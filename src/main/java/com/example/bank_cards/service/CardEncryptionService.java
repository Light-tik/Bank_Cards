package com.example.bank_cards.service;

public interface CardEncryptionService {

    String encrypt(String plainCardNumber);

    String decrypt(String encryptedCardNumber);

    String mask(String encryptedNumber);
}
