package ar.com.api.derivatives.handler;

import java.util.Optional;

import ar.com.api.derivatives.dto.ExchangeIdDTO;
import ar.com.api.derivatives.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import ar.com.api.derivatives.utils.StringToInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class DerivativesApiHandler {

    private DerivativesGeckoApiService serviceDerivatives;

    public Mono<ServerResponse> getListOfDerivativesTickers(ServerRequest sRequest) {
        log.info("In getListOfDerivativesTickers");

        return serviceDerivatives
                .getListOfDerivatives()
                .collectList()
                .flatMap(
                        derivatives -> ServerResponse.ok().bodyValue(derivatives)
                )
                .onErrorResume(error -> {
                    log.error("Error retrieving derivatives", error);
                    int valueErrorCode = ((WebClientResponseException) error.getCause()).getStatusCode().value();
                    return ServerResponse.status(valueErrorCode)
                            .bodyValue(((WebClientResponseException) error.getCause()).getStatusText());
                });
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
                .flatMap( data ->
                        ServerResponse.ok().bodyValue(data))
                .onErrorResume( error -> {
                    log.error("Error retrieving derivatives exchanges", error);
                    int valueErrorCode = ((WebClientResponseException) error.getCause())
                            .getStatusCode().value();
                    return ServerResponse.status(valueErrorCode)
                            .bodyValue(((WebClientResponseException) error.getCause()).getStatusText());
                });
    }

    public Mono<ServerResponse> getListDerivativesOfExchangesOrderedAndPaginated(ServerRequest sRequest) {

        log.info("In getListDerivativesOfExchangesOrderedAndPaginated");

        Optional<Integer> optPerPage = Optional.empty();
        Optional<Integer> optPage = Optional.empty();

        if (sRequest.queryParam("perPage").isPresent()) {
            optPerPage = Optional
                    .of(sRequest.queryParam("perPage")
                            .get()
                            .transform(StringToInteger.INSTANCE));
        }

        if (sRequest.queryParam("page").isPresent()) {
            optPage = Optional
                    .of(sRequest.queryParam("page")
                            .get()
                            .transform(StringToInteger.INSTANCE));
        }

        DerivativeExchangeDTO filterDTO = DerivativeExchangeDTO
                .builder()
                .order(sRequest.queryParam("order"))
                .page(optPage)
                .perPage(optPerPage)
                .build();

        return ServerResponse
                .ok()
                .body(
                        serviceDerivatives.getListDerivativeExchangedOrderedAndPaginated(filterDTO), DerivativeExchange.class
                );
    }

}