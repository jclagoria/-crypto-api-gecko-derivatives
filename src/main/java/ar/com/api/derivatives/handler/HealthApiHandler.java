package ar.com.api.derivatives.handler;

import ar.com.api.derivatives.services.CoinGeckoServiceStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class HealthApiHandler {

    private CoinGeckoServiceStatus serviceStatus;

    public Mono<ServerResponse> getStatusServiceCoinGecko(ServerRequest serverRequest) {

        log.info("Fetching CoinGecko service status");

        return serviceStatus.getStatusCoinGeckoService()
                .flatMap(ping -> ServerResponse.ok().bodyValue(ping))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> {
                    log.error("Error fetching CoinGecko service status", error);
                    int valueErrorCode = ((WebClientResponseException) error.getCause())
                            .getStatusCode().value();
                    return ServerResponse.status(valueErrorCode)
                            .bodyValue(((WebClientResponseException) error.getCause()).getStatusText());
                });
    }

}
