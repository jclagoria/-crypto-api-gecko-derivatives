package ar.com.api.derivatives.services;

import ar.com.api.derivatives.configuration.ExternalServerConfig;
import ar.com.api.derivatives.configuration.HttpServiceCall;
import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.dto.ExchangeIdDTO;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.DerivativeData;
import ar.com.api.derivatives.model.DerivativeExchange;
import ar.com.api.derivatives.model.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DerivativesGeckoApiService {

    private final ExternalServerConfig externalServerConfig;
    private final HttpServiceCall httpServiceCall;

    public DerivativesGeckoApiService(HttpServiceCall serviceCall, ExternalServerConfig eServerConfig) {
        this.httpServiceCall = serviceCall;
        this.externalServerConfig = eServerConfig;
    }

    /**
     * @return
     */
    public Flux<Derivative> getListOfDerivatives() {

        log.info("Calling method: {}", externalServerConfig.getDerivativesGecko());

        return null;
    }

    /**
     * @param filterDTO
     * @return
     */
    public Flux<DerivativeExchange> getListDerivativeExchangedOrderedAndPaginated(
            DerivativeExchangeDTO filterDTO) {

        log.info("Calling method: {}", externalServerConfig.getDerivativesExchangesGecko()
                + filterDTO.getUrlFilterString());

        return null;
    }

    public Mono<DerivativeData> getShowDerivativeExchangeData(ExchangeIdDTO filterDTO) {

        String urlDerivativesByExchangeId = String.format(
                externalServerConfig.getDerivativesExchangesByIdGecko(),
                filterDTO.getIdExchange());

        log.info("Calling method: {}", externalServerConfig.getDerivativesExchangesByIdGecko() +
                filterDTO.getUrlFilterString());

        return null;
    }

    public Flux<Exchange> getListOfDerivativesExchanges() {

        log.info("Calling method: " + externalServerConfig.getDerivativesExchangesList());

        return null;
    }

}