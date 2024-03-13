package ar.com.api.derivatives.exception;

import ar.com.api.derivatives.enums.ErrorTypeEnums;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiClientErrorException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorTypeEnums errorTypeEnums;

    public ApiClientErrorException(String message, ErrorTypeEnums typeEnums, HttpStatus status) {
        super(message);
        this.httpStatus = status;
        this.errorTypeEnums = typeEnums;
    }

}
