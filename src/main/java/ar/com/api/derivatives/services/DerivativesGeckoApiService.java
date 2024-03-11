package ar.com.api.derivatives.services;

import ar.com.api.derivatives.configuration.ExternalServerConfig;
import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.dto.ExchangeIdDTO;
import ar.com.api.derivatives.exception.ManageExceptionCoinGeckoServiceApi;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.DerivativeData;
import ar.com.api.derivatives.model.DerivativeExchange;
import ar.com.api.derivatives.model.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DerivativesGeckoApiService extends CoinGeckoServiceApi {

    private WebClient webClient;

    private ExternalServerConfig externalServerConfig;

    public DerivativesGeckoApiService(WebClient wClient, ExternalServerConfig eServerConfig) {
        this.webClient = wClient;
        this.externalServerConfig = eServerConfig;
    }

    /**
     * @return
     */
    public Flux<Derivative> getListOfDerivatives() {

        log.info("Calling method: {}", externalServerConfig.getDerivativesGecko());

        return webClient
                .get()
                .uri(externalServerConfig.getDerivativesGecko())
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError(),
                        getClientResponseMonoDataException()
                )
                .onStatus(
                        httpStatus -> httpStatus.is5xxServerError(),
                        getClientResponseMonoServerException()
                )
                .bodyToFlux(Derivative.class)
                .doOnError(
                        ManageExceptionCoinGeckoServiceApi::throwServiceException
                );
    }

    /**
     * @param filterDTO
     * @return
     */
    public Flux<DerivativeExchange> getListDerivativeExchangedOrderedAndPaginated(
            DerivativeExchangeDTO filterDTO) {

        log.info("Calling method: {}", externalServerConfig.getDerivativesExchangesGecko());

        return webClient
                .get()
                .uri(externalServerConfig.getDerivativesExchangesGecko() +
                        filterDTO.getUrlFilterString())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        getClientResponseMonoDataException()
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        getClientResponseMonoServerException()
                )
                .bodyToFlux(DerivativeExchange.class)
                .doOnError(
                        ManageExceptionCoinGeckoServiceApi::throwServiceException
                );
    }

    public Mono<DerivativeData> getShowDerivativeExchangeData(ExchangeIdDTO filterDTO) {

        log.info("Calling method: {}", externalServerConfig.getDerivativesExchangesByIdGecko());

        String urlDerivativesByExchangeId = String.format(
                externalServerConfig.getDerivativesExchangesByIdGecko(),
                filterDTO.getIdExchange());

        return webClient
                .get()
                .uri(urlDerivativesByExchangeId + filterDTO.getUrlFilterString())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        getClientResponseMonoDataException()
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        getClientResponseMonoServerException()
                )
                .bodyToMono(DerivativeData.class)
                .doOnError(
                        ManageExceptionCoinGeckoServiceApi::throwServiceException
                );
    }

    public Flux<Exchange> getListOfDerivativesExchanges() {

        log.info("Calling method: " + externalServerConfig.getDerivativesExchangesList());

        return webClient
                .get()
                .uri(externalServerConfig.getDerivativesExchangesList())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        getClientResponseMonoDataException()
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        getClientResponseMonoServerException()
                )
                .bodyToFlux(Exchange.class)
                .doOnError(
                        ManageExceptionCoinGeckoServiceApi::throwServiceException
                );
    }

}