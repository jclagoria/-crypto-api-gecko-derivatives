package ar.com.api.derivatives.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiValidationError extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorMessage;

    public ApiValidationError(String message, String errorMessage, HttpStatus status) {
        super(message);
        this.httpStatus = status;
        this.errorMessage = errorMessage;
    }

}
