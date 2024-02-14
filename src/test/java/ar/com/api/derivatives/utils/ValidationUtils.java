package ar.com.api.derivatives.utils;

import ar.com.api.derivatives.exception.ServiceException;
import ar.com.api.derivatives.model.DerivativeData;
import ar.com.api.derivatives.model.Exchange;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ValidationUtils {

    public static Duration validate4xxError(Mono<Class> result4xxError) {
        return StepVerifier.create (result4xxError).expectErrorMatches (
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is4xxClientError()
        ).verify();
    }

    public static Duration validate5xxError(Mono<Class> result4xxError) {
        return StepVerifier.create (result4xxError).expectErrorMatches (
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is5xxServerError()
        ).verify();
    }

    public static Duration validate4xxError(Flux<Exchange> result4xxError) {
        return StepVerifier.create (result4xxError).expectErrorMatches (
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is4xxClientError()
        ).verify();
    }

    public static Duration validate5xxError(Flux<Exchange> result5xxError) {
        return StepVerifier.create(result5xxError).expectErrorMatches(
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is5xxServerError()
        ).verify();
    }

}
