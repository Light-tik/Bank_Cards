package com.example.bank_cards.service.impl;

import com.example.bank_cards.service.CardEncryptionService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CardEncryptionServiceImpl implements CardEncryptionService {

    public String encrypt(String plainCardNumber) {
        return Base64.getEncoder().encodeToString(plainCardNumber.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String encryptedCardNumber) {
        return new String(Base64.getDecoder().decode(encryptedCardNumber), StandardCharsets.UTF_8);
    }

    public String mask(String encryptedNumber) {
        String decrypted = decrypt(encryptedNumber);
        return "****-****-****-" + decrypted.substring(decrypted.length() - 4);
    }
}
