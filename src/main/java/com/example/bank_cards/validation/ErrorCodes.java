package com.example.bank_cards.validation;

import com.example.bank_cards.constants.ValidationConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ErrorCodes {
    UNKNOWN(0, "unknown"),
    USERNAME_SIZE_NOT_VALID(1, ValidationConstants.USERNAME_SIZE_NOT_VALID),
    USER_NOT_FOUND(2, ValidationConstants.USER_NOT_FOUND),
    USER_ALREADY_EXISTS(3, ValidationConstants.USER_ALREADY_EXIST),
    PASSWORD_NOT_VALID(4, ValidationConstants.PASSWORD_NOT_VALID ),
    CARD_NOT_FOUND(5, ValidationConstants.CARD_NOT_FOUND),
    UNAUTHORISED(6, ValidationConstants.UNAUTHORISED),
    NO_RIGHTS(7, ValidationConstants.NO_RIGHTS),
    INVALID_AMOUNT(8, ValidationConstants.INVALID_AMOUNT ),
    LIMIT_EXCEEDED(9, ValidationConstants.LIMIT_EXCEEDED ),
    INSUFFICIENT_FUNDS(10,ValidationConstants.INSUFFICIENT_FUNDS ),
    LIMIT_NOT_SET(11,ValidationConstants.LIMIT_NOT_SET ),
    HTTP_MESSAGE_NOT_READABLE_EXCEPTION(12, ValidationConstants.HTTP_MESSAGE_NOT_READABLE_EXCEPTION);

    public static Map<String, Integer> errorMap = new HashMap<>();

    private final Integer code;

    private final String message;

    static {
        for (ErrorCodes e : ErrorCodes.values()) {
            errorMap.put(e.getMessage(), e.code);
        }
    }
}
