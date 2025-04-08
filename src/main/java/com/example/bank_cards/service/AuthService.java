package com.example.bank_cards.service;

import com.example.bank_cards.dto.request.SignInRequest;
import com.example.bank_cards.dto.response.LoginUserResponse;
import com.example.bank_cards.dto.request.SignUpRequest;

public interface AuthService {

    LoginUserResponse registerUser(SignUpRequest user);

    LoginUserResponse loginUser(SignInRequest user);
}
