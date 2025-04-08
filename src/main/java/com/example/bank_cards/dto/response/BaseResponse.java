package com.example.bank_cards.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {

    private Boolean success = true;

    private Integer statusCode;

    private List<Integer> codes;


    public BaseResponse(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public BaseResponse(List<Integer> codes) {
        this.codes = codes;
    }
}
