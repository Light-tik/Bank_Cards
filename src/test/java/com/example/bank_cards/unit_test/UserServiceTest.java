package com.example.bank_cards.unit_test;

import com.example.bank_cards.DataSet;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.mappers.UserMapper;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.service.impl.UserServiceImpl;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository repository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private final UserEntity entity = new UserEntity();

    @Test
    void getListUserTest(){
        var users = DataSet.getUser();
        var listEntity = List.of(users);
        var listUsers = List.of(DataSet.createPublicUserView(users));

        when(repository.findAll()).thenReturn(listEntity);
        when(userMapper.toPublicUserList(listEntity)).thenReturn(listUsers);

        List<PublicUserView> result = userService.getListUsers();

        assertEquals(listUsers, result);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getUserTest_success(){
        var id = UUID.randomUUID();
        var entity = DataSet.getUser();
        var userInfo = DataSet.createPublicUserView(entity);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(userMapper.toPublicUser(entity)).thenReturn(userInfo);

        PublicUserView result = userService.getUser(id);

        assertEquals(userInfo, result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getUserTest_UserNotFound(){
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, ()-> userService.getUser(id));
        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getErrorCodes());
    }

    @Test
    void getUserInfoTest_success(){
        var id = UUID.randomUUID();
        var entity = DataSet.getUser();
        var userInfo = DataSet.createPublicUserView(entity);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(id.toString());
        SecurityContextHolder.setContext(securityContext);
        when(userMapper.toPublicUser(entity)).thenReturn(userInfo);

        PublicUserView result = userService.getUserInfo();

        assertEquals(userInfo, result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getUserInfoTest_UserNotFound(){
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, ()-> userService.getUser(id));
        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getErrorCodes());
    }

    @Test
    void putUser_Success() {
        var user = DataSet.getUser();
        var request = DataSet.createUserRequest(user);
        var response = DataSet.createPublicUserView(user);
        when(userMapper.toPublicUser(user)).thenReturn(response);

        PublicUserView result = userService.putUser(user.getId(), request);

        assertEquals(response, result);
        verify(userMapper, times(1)).toPublicUser(user);
    }

    @Test
    void getCurrentUser_Success() {
        var userId = UUID.randomUUID();
        var userEntity = DataSet.getUser();
        when(repository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId.toString());
        SecurityContextHolder.setContext(securityContext);

        UserEntity result = userService.getCurrentUser();

        assertEquals(userEntity, result);
        verify(repository, times(1)).findById(userId);
    }

    @Test
    void getCurrentUser_NotFound() {
        var userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId.toString());
        SecurityContextHolder.setContext(securityContext);

        CustomException exception = assertThrows(CustomException.class, () -> userService.getCurrentUser());

        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getErrorCodes());
    }
}