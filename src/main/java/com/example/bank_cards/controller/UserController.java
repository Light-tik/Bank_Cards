package com.example.bank_cards.controller;

import com.example.bank_cards.dto.request.SignUpRequest;
import com.example.bank_cards.dto.response.BaseResponse;
import com.example.bank_cards.dto.response.CustomResponse;
import com.example.bank_cards.dto.response.PublicUserView;
import com.example.bank_cards.service.UserCardService;
import com.example.bank_cards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    private final UserCardService userCardService;

    @GetMapping("/admin/user")
    public ResponseEntity<CustomResponse<List<PublicUserView>>> getAll() {
        return ResponseEntity.ok().body(new CustomResponse<>(userService.getListUsers()));
    }

    @GetMapping("/admin/user/{id}")
    public ResponseEntity<CustomResponse<PublicUserView>> getUserInfoById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(new CustomResponse<>(userService.getUser(id)));
    }

    @GetMapping("/info")
    public ResponseEntity<CustomResponse<PublicUserView>> getUserInfo() {
        return ResponseEntity.ok(new CustomResponse<>(userService.getUserInfo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteUser(
            @PathVariable UUID id) {
        userCardService.deleteUserWithCards(id);
        return ResponseEntity.ok(new BaseResponse(1));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<PublicUserView>> replaceUser(
            @PathVariable UUID id,
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(new CustomResponse<>(userService.putUser(id, request)));
    }
}

