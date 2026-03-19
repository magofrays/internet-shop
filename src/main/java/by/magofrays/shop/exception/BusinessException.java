package by.magofrays.shop.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus errorStatus;
    @Getter
    private final String message;
    public BusinessException(HttpStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return errorStatus;
    }
}
