package ar.com.api.derivatives.handler;

import ar.com.api.derivatives.dto.ExchangeIdDTO;
import ar.com.api.derivatives.enums.ErrorTypeEnums;
import ar.com.api.derivatives.exception.ApiClientErrorException;
import ar.com.api.derivatives.handler.utils.MapperHandler;
import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class DerivativesApiHandler {

    private DerivativesGeckoApiService serviceDerivatives;

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

    public Mono<ServerResponse> getAllDerivativesExchanges(ServerRequest serverRequest) {

        log.info("Starting getAllDerivativesExchanges");

        return serviceDerivatives
                .getListOfDerivativesExchanges()
                .collectList()
                .flatMap(
                        exchanges -> ServerResponse.ok().bodyValue(exchanges)
                )
                .onErrorResume(error -> {
                    log.error("Error retrieving derivatives exchanges", error);
                    int valueErrorCode = ((WebClientResponseException) error.getCause()).getStatusCode().value();
                    return ServerResponse.status(valueErrorCode)
                            .bodyValue(((WebClientResponseException) error.getCause()).getStatusText());
                });
    }

    public Mono<ServerResponse> getShowDerivativeExchangeData(ServerRequest sRequest) {

        log.info("Starting getShowDerivativeExchangeData");

        Optional<String> opIncludeTickers = Optional.empty();

        if (sRequest.queryParam("includeTickers").isPresent()) {
            opIncludeTickers = Optional.
                    of(sRequest.queryParam("includeTickers")
                            .get());
        }

        ExchangeIdDTO dto = ExchangeIdDTO.builder()
                .idExchange(sRequest.pathVariable("idExchange"))
                .includeTickers(opIncludeTickers).build();

        return serviceDerivatives
                .getShowDerivativeExchangeData(dto)
                .flatMap(data ->
                        ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> {
                    log.error("Error retrieving derivatives exchanges", error);
                    int valueErrorCode = ((WebClientResponseException) error.getCause())
                            .getStatusCode().value();
                    return ServerResponse.status(valueErrorCode)
                            .bodyValue(((WebClientResponseException) error.getCause()).getStatusText());
                });
    }

}