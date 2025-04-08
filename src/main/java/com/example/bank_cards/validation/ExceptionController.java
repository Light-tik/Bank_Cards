package com.example.bank_cards.validation;

import com.example.bank_cards.constants.MyConstants;
import com.example.bank_cards.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.bank_cards.validation.ErrorCodes.errorMap;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse> exceptionHandler(
            CustomException e,
            HttpServletRequest request
    ) {
        request.setAttribute(MyConstants.ERROR_MESSAGE, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(e.getErrorCodes().getCode()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> exception2Handler(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        request.setAttribute(MyConstants.ERROR_MESSAGE, ErrorCodes.HTTP_MESSAGE_NOT_READABLE_EXCEPTION.getMessage());
        return ResponseEntity.badRequest()
                .body(new BaseResponse(ErrorCodes.HTTP_MESSAGE_NOT_READABLE_EXCEPTION.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> exceptionValidateArgument(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        request.setAttribute(MyConstants.ERROR_MESSAGE,
                e.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(MyConstants.DELIMITER)));
        var exception = e.getBindingResult().getAllErrors();
        if (exception.size() < 2) {
            Integer error = exception.stream()
                    .map(i -> errorMap.get(i.getDefaultMessage()))
                    .findFirst()
                    .orElse(0);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(error));
        } else {
            List<Integer> errors = exception.stream()
                    .map(i -> errorMap.get(i.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(errors));
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        request.setAttribute(MyConstants.ERROR_MESSAGE,
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(MyConstants.DELIMITER)));
        var exception = e.getConstraintViolations();
        if (exception.size() < 2) {
            Integer error = exception.stream()
                    .map(i -> errorMap.get(i.getMessage()))
                    .findFirst()
                    .orElse(0);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(error));
        } else {
            List<Integer> errors = exception.stream()
                    .map(i -> errorMap.get(i.getMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(errors));
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handleMissingParams(
            MissingServletRequestParameterException e,
            HttpServletRequest request
    ) {
        request.setAttribute(MyConstants.ERROR_MESSAGE, ErrorCodes.CARD_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(ErrorCodes.CARD_NOT_FOUND.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleMultipartException(Exception e, HttpServletRequest request) {
        request.setAttribute(MyConstants.ERROR_MESSAGE, ErrorCodes.UNKNOWN.getMessage());
        return ResponseEntity.badRequest().body(new BaseResponse(ErrorCodes.UNKNOWN.getCode()));
    }
}
