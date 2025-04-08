package com.example.bank_cards.service.impl;

import com.example.bank_cards.dto.request.SignInRequest;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.mappers.UserMapper;
import com.example.bank_cards.repositories.UserRepository;
import com.example.bank_cards.security.JwtService;
import com.example.bank_cards.service.AuthService;
import com.example.bank_cards.validation.CustomException;
import com.example.bank_cards.validation.ErrorCodes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginUserResponse registerUser(SignUpRequest user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new CustomException(ErrorCodes.USER_ALREADY_EXISTS);
        }
        UserEntity entity = userMapper.toEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(entity);
        var token = jwtService.generateToken(entity);
        return userMapper.toLogin(entity, token);
    }

    @Override
    public LoginUserResponse loginUser(SignInRequest user) {
        UserEntity entity = repository.findByEmail(user.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCodes.USER_NOT_FOUND));
        if (passwordEncoder.matches(user.getPassword(), entity.getPassword())) {
            var token = jwtService.generateToken(entity);
            return userMapper.toLogin(entity, token);
        } else {
            throw new CustomException(ErrorCodes.PASSWORD_NOT_VALID);
        }
    }
}
