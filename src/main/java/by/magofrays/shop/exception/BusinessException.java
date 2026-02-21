package by.magofrays.shop.exception;


import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private HttpStatus errorStatus;
    public BusinessException(HttpStatus errorStatus){
        this.errorStatus = errorStatus;
    }
}
