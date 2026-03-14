package by.magofrays.shop.controller;

import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.exception.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDetail(ex.getMessage());
        errorMessage.setTitle("Business Exception");
        errorMessage.setProperty("timestamp", LocalDateTime.now().toString());
        errorMessage.setProperty("code", String.valueOf(ex.getHttpStatus().value()));
        return new ResponseEntity<>(
                errorMessage,
                ex.getHttpStatus()
        );
    }
}
