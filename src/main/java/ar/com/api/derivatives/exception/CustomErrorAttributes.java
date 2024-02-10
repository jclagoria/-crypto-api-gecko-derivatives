package ar.com.api.derivatives.exception;

import ar.com.api.derivatives.exception.external.CoinGeckoBadRequestException;
import ar.com.api.derivatives.exception.external.CoinGeckoServerException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Configuration
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);


        Optional<HttpStatus> errorStatus = determineHttpStatus(getError(webRequest));


        errorStatus.ifPresent(
                httpStatus -> {
                    errorAttributes.replace("status", httpStatus.value());
                    errorAttributes.replace("error", httpStatus.getReasonPhrase());
                }
        );

        return errorAttributes;
    }

    private Optional<HttpStatus> determineHttpStatus(Throwable error) {

        if (error instanceof CoinGeckoBadRequestException) {
            return Optional.of(HttpStatus.NOT_FOUND);
        }

        if (error instanceof ResponseStatusException) {
            error.getMessage();
            return Optional.of(HttpStatus.BAD_REQUEST);
        }

        if (error instanceof CoinGeckoServerException ||
                error instanceof ServiceException) {
            return Optional.of(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return Optional.empty();

    }

}
