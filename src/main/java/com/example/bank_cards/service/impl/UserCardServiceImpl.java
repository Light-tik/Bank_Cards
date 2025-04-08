package com.example.bank_cards.service.impl;

import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.service.CardService;
import com.example.bank_cards.service.UserCardService;
import com.example.bank_cards.service.UserService;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCardServiceImpl implements UserCardService {

    private final CardService cardService;
    private final UserService userService;

    @Override
    public void deleteUserWithCards(UUID targetUserId) {
        if (!userService.hasRightsTo(targetUserId)) {
            throw new CustomException(ErrorCodes.NO_RIGHTS);
        }

        UserEntity user = userService.getUserEntity(targetUserId);

        cardService.deleteAllCard();

        userService.delete(user);
    }
}
