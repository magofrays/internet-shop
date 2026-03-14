package by.magofrays.shop.controller;

import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.exception.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

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

    @SneakyThrows
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorMessage errorResponse = new ErrorMessage();
        errorResponse.setTitle("Validation Exception");
        errorResponse.setProperty("timestamp", LocalDateTime.now().toString());
        errorResponse.setProperty("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorResponse.setProperty("fieldErrors", objectMapper.writeValueAsString(errors));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
