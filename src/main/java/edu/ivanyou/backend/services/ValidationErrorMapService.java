package edu.ivanyou.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;

@Service
public class ValidationErrorMapService {
    public ResponseEntity<?> mapValidationError(BindingResult result) {
        if (result.hasErrors()) {
            HashMap<String, String> errMessage = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errMessage.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(errMessage, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
