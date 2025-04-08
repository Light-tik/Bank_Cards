package com.example.bank_cards.dto.response;

import com.example.bank_cards.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserView {

    private String email;

    private UUID id;

    private String name;

    private Roles role;
}
