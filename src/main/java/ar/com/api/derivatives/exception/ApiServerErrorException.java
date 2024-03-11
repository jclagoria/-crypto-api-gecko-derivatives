package ar.com.api.derivatives.exception;

import ar.com.api.derivatives.enums.ErrorTypeEnums;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiServerErrorException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String originalMessage;
    private final ErrorTypeEnums errorTypeEnums;

    public ApiServerErrorException(String message, String originalMessage,
                                   ErrorTypeEnums typeEnums, HttpStatus status) {
        super(message);
        this.originalMessage = originalMessage;
        this.errorTypeEnums = typeEnums;
        this.httpStatus = status;
    }

}
