package ar.com.api.derivatives.handler;

import ar.com.api.derivatives.enums.ErrorTypeEnums;
import ar.com.api.derivatives.exception.ApiClientErrorException;
import ar.com.api.derivatives.handler.utils.MapperHandler;
import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import ar.com.api.derivatives.validators.ValidatorOfCTOComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class DerivativesApiHandler {

    private final DerivativesGeckoApiService serviceDerivatives;
    private final ValidatorOfCTOComponent validatorOfCTOComponent;

    public Mono<ServerResponse> getListOfDerivativesTickers(ServerRequest sRequest) {
        log.info("Fetching List of Derivatives from CoinGecko API");

        return serviceDerivatives
                .getListOfDerivatives()
                .collectList()
                .flatMap(
                        derivatives -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(derivatives)
                )
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving List of Exchanges from CoinGecko"))
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException("An unexpected error occurred in getListOfDerivativesTickers",
                                ErrorTypeEnums.API_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR)
                ));
    }

    public Mono<ServerResponse> getListDerivativesOfExchangesOrderedAndPaginated(ServerRequest sRequest) {
        log.info("Fetching List of Derivative Exchanges from CoinGecko Api");

        return Mono.just(sRequest)
                .flatMap(MapperHandler::createDerivativeExchangeDTOFromRequest)
                .flatMapMany(serviceDerivatives::getListDerivativeExchangedOrderedAndPaginated)
                .collectList()
                .flatMap(listDerivatives -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(listDerivatives)
                ).switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving list of DerivativeExchange"))
                .onErrorResume(error -> Mono
                        .error(new ApiClientErrorException(
                                "An unexpected error occurred in getListDerivativesOfExchangesOrderedAndPaginated",
                                ErrorTypeEnums.API_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR))
                );
    }

    public Mono<ServerResponse> getShowDerivativeExchangeData(ServerRequest sRequest) {
        log.info("Fetching List of Derivative Data from CoinGecko Api");

        return Mono.just(sRequest)
                .flatMap(MapperHandler::createExchangeIdDTODTOFromRequest)
                .flatMap(validatorOfCTOComponent::validation)
                .flatMap(serviceDerivatives::getShowDerivativeExchangeData)
                .flatMap(listDerivativeData -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(listDerivativeData))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving list of DerivativeData"))
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException(
                                "An unexpected error occurred in getShowDerivativeExchangeData",
                                ErrorTypeEnums.API_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR)
                ));
    }

    public Mono<ServerResponse> getAllDerivativesExchanges(ServerRequest serverRequest) {
        log.info("Fetching List of Exchanges Data from CoinGecko Api");

        return serviceDerivatives
                .getListOfDerivativesExchanges()
                .collectList()
                .flatMap(
                        exchanges -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(exchanges)
                )
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSubscribe(subscription -> log.info("Retrieving list of Exchange"))
                .onErrorResume(error -> Mono.error(
                        new ApiClientErrorException(
                                "An unexpected error occurred in getAllDerivativesExchanges",
                                ErrorTypeEnums.API_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR)
                ));
    }

}