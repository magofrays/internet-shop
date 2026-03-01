package by.magofrays.shop.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus errorStatus;
    public BusinessException(HttpStatus errorStatus){
        this.errorStatus = errorStatus;
    }

    public HttpStatus getHttpStatus() {
        return errorStatus;
    }
}
