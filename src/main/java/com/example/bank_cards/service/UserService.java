package com.example.bank_cards.service;

import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<PublicUserView> getListUsers();

    PublicUserView getUser(UUID id);

    PublicUserView getUserInfo();

    void delete(UserEntity userEntity);

    String getUserEmail();

    PublicUserView putUser(UUID targetUserId, SignUpRequest request);

    UserEntity getCurrentUser();

    UserDetailsService userDetailsService();

    UserEntity getUserEntity(UUID id);

    boolean hasRightsTo(UUID targetUserId);
}
