package com.example.bank_cards.mappers;

import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    UserEntity toEntity(SignUpRequest userRequest);

    LoginUserResponse toLogin(UserEntity user, String token);

    PublicUserView toPublicUser(UserEntity user);

    List<PublicUserView> toPublicUserList(List<UserEntity> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromRequest(SignUpRequest request, @MappingTarget UserEntity user);
}
