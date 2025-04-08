package com.example.bank_cards.service.impl;

import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import com.example.bank_cards.mappers.UserMapper;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.security.SecurityContextFacade;
import com.example.bank_cards.service.UserService;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository repository;

    private final SecurityContextFacade securityContext;

    @Override
    public List<PublicUserView> getListUsers() {
        var listEntity = repository.findAll();
        return userMapper.toPublicUserList(listEntity);
    }

    @Override
    public PublicUserView getUser(UUID id) {
        UserEntity userEntity = getUserEntity(id);
        return userMapper.toPublicUser(userEntity);
    }

    @Override
    public PublicUserView getUserInfo() {
        UserEntity userEntity = repository.findByEmail(getUserEmail()).orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND));
        return userMapper.toPublicUser(userEntity);
    }

    @Override
    public String getUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public void delete(UserEntity userEntity) {
        repository.delete(userEntity);
    }


    @Override
    public PublicUserView putUser(UUID targetUserId, SignUpRequest request) {
        if (!hasRightsTo(targetUserId)) {
            throw new CustomException(ErrorCodes.NO_RIGHTS);
        }
        UserEntity user = getUserEntity(targetUserId);
        userMapper.updateUserFromRequest(request, user);
        UserEntity updated = repository.save(user);

        return userMapper.toPublicUser(updated);
    }

    @Override
    public UserEntity getCurrentUser() {
        return repository.findById(securityContext.getCurrentUserId()).orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND));
    }

    @Deprecated
    public void setAdmin() {
        var user = getCurrentUser();
        user.setRole(Roles.ROLE_ADMIN);
        repository.save(user);
    }

    @Override
    public boolean hasRightsTo(UUID targetUserId) {
        return securityContext.getCurrentUserId().equals(targetUserId)
                || securityContext.getCurrentUserRole() == Roles.ROLE_ADMIN;
    }

    @Override
    public UserEntity getUserEntity(UUID id){
        return repository.findById(id).orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND));
    }
}
