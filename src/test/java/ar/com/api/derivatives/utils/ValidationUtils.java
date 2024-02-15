package ar.com.api.derivatives.utils;

import ar.com.api.derivatives.exception.ServiceException;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ValidationUtils {

    public static <T> Duration validate4xxError(Publisher<T> resultError) {
        return StepVerifier.create(resultError)
                .expectErrorMatches(throwable ->
                        throwable instanceof ServiceException &&
                                throwable.getCause() instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable.getCause()).getStatusCode().is4xxClientError())
                .verify();
    }

    public static <T> Duration validate5xxError(Publisher<T> resultError) {
        return StepVerifier.create(resultError)
                .expectErrorMatches(throwable ->
                        throwable instanceof ServiceException &&
                                throwable.getCause() instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable.getCause()).getStatusCode().is5xxServerError() )
                .verify();
    }
}
