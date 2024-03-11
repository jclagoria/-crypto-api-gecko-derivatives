package ar.com.api.derivatives.handler;

import ar.com.api.derivatives.enums.ErrorTypeEnums;
import ar.com.api.derivatives.exception.ApiClientErrorException;
import ar.com.api.derivatives.services.CoinGeckoServiceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class HealthApiHandler {

    private CoinGeckoServiceStatus serviceStatus;

    public Mono<ServerResponse> getStatusServiceCoinGecko(ServerRequest serverRequest) {
        log.info("In getStatusServiceCoinGecko, handling request {}", serverRequest.path());

        return serviceStatus.getStatusCoinGeckoService()
                .flatMap(ping -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ping))
                .doOnSubscribe(subscription -> log.info("Retrieving status of Gecko Service"))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException(
                                "An expected error occurred in getStatusServiceCoinGecko",
                                ErrorTypeEnums.API_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                ));
    }

}
