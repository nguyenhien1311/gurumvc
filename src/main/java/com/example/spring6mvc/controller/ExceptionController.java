package com.example.spring6mvc.controller;

import com.example.spring6mvc.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity notFoundExHandle() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBindErrors(MethodArgumentNotValidException e) {
        List<HashMap<String, String>> list = e.getFieldErrors().stream().map(fieldError -> {
            HashMap<String, String> map = new HashMap<>();
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
            return map;
        }).toList();
        return ResponseEntity.badRequest().body(list);
    }

    @ExceptionHandler
    public ResponseEntity handleJPAViolations(TransactionSystemException e) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();
        if (e.getCause().getCause() instanceof ConstraintViolationException exception) {
            List<HashMap<String, String>> list = exception.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(constraintViolation.getPropertyPath().toString(),
                                constraintViolation.getMessage());
                        return map;
                    }).toList();
            return bodyBuilder.body(list);
        }
        return bodyBuilder.build();
    }
}
