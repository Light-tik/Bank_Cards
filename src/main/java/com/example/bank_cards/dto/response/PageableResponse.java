package com.example.bank_cards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "Страница с данными")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableResponse<T> {

    @Schema(description = "Список элементов на странице")
    private List<T> content;

    @Schema(description = "Количество элементов на текущей странице")
    private Long numberOfElements;
}
