package com.example.bank_cards.unit_test;

import com.example.bank_cards.DataSet;
import com.example.bank_cards.MyTestConstants;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.mappers.UserMapper;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.security.JwtService;
import com.example.bank_cards.service.impl.AuthServiceImpl;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository repository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UserEntity entity = new UserEntity();

    @Test
    void registerUser_success(){
        var user = DataSet.getUser();
        var request = DataSet.createUserRequest(user);
        var response = DataSet.createLoginUserResponse(user);

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(request.getPassword());
        when(repository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(MyTestConstants.TOKEN);
        when(userMapper.toLogin(user, MyTestConstants.TOKEN)).thenReturn(response);

        LoginUserResponse result = authService.registerUser(request);

        verify(repository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        assertNotNull(result);
        assertEquals(MyTestConstants.TOKEN, result.getToken());
    }

    @Test
    void registerUser_UserByExist(){
        var user = DataSet.getUser();
        var request = DataSet.createUserRequest(user);
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);
        CustomException exception = assertThrows(CustomException.class, () -> authService.registerUser(request));
        assertEquals(ErrorCodes.USER_ALREADY_EXISTS, exception.getErrorCodes());
    }

    @Test
    void loginUser_success(){
        var user = DataSet.getUser();
        var request = DataSet.createAuthUserRequest(user);
        var response = DataSet.createLoginUserResponse(user);

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(request.getPassword(), entity.getPassword())).thenReturn(true);
        when(jwtService.generateToken(entity)).thenReturn(MyTestConstants.TOKEN);
        when(userMapper.toLogin(entity, MyTestConstants.TOKEN)).thenReturn(response);

        LoginUserResponse result = authService.loginUser(request);

        assertNotNull(result);
        assertEquals(MyTestConstants.TOKEN, result.getToken());
        assertEquals(request.getEmail(), result.getEmail());
    }

    @Test
    void loginUser_PasswordNotRight(){
        var user = DataSet.getUser();
        var request = DataSet.createAuthUserRequest(user);
        request.setPassword("");
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(request.getPassword(), entity.getPassword())).thenReturn(false);
        CustomException exception = assertThrows(CustomException.class, () -> authService.loginUser(request));
        assertEquals(ErrorCodes.PASSWORD_NOT_VALID, exception.getErrorCodes());
    }

    @Test
    void loginUser_EmailNotExist(){
        var user = DataSet.getUser();
        var request = DataSet.createAuthUserRequest(user);
        request.setEmail("");
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> authService.loginUser(request));
        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getErrorCodes());
    }
}