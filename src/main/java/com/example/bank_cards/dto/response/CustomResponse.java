package com.example.bank_cards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> extends BaseResponse{

    private T data;

    public CustomResponse(Integer statusCode, T data) {
        super(statusCode);
        this.data = data;
    }
}
