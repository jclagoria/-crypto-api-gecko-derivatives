package ar.com.api.derivatives.services;

import ar.com.api.derivatives.configuration.ExternalServerConfig;
import ar.com.api.derivatives.exception.ManageExceptionCoinGeckoServiceApi;
import ar.com.api.derivatives.model.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CoinGeckoServiceStatus extends CoinGeckoServiceApi {

    private ExternalServerConfig externalServerConfig;

    private WebClient webClient;

    public CoinGeckoServiceStatus(WebClient wClient, ExternalServerConfig eServerConfig) {
        this.webClient = wClient;
        this.externalServerConfig = eServerConfig;
    }

    public Mono<Ping> getStatusCoinGeckoService() {

        log.info("Calling method: {}", externalServerConfig.getPing());

        return webClient
                .get()
                .uri(externalServerConfig.getPing())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        getClientResponseMonoDataException()
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        getClientResponseMonoServerException()
                )
                .bodyToMono(Ping.class)
                .doOnError(
                        ManageExceptionCoinGeckoServiceApi::throwServiceException
                );
    }

}
