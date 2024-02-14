package ar.com.api.derivatives.router;

import ar.com.api.derivatives.configuration.ApiServiceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.derivatives.handler.DerivativesApiHandler;

@Configuration
public class DerivativesApiRouter extends AbstractRouterConfig {

    private ApiServiceConfig apiServiceConfig;

    public DerivativesApiRouter(ApiServiceConfig apiServiceConfig) {
        this.apiServiceConfig = apiServiceConfig;
    }

    @Bean
    public RouterFunction<ServerResponse> route(DerivativesApiHandler handler) {

        return RouterFunctions
                .route()
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getDerivativesApi(),
                        handler::getListOfDerivativesTickers)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getDerivativesExchangesListNameAndId(),
                        handler::getAllDerivativesExchanges)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getDerivativesExchangesByIdGecko(),
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getShowDerivativeExchangeData)
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getDerivativesExchangesListNameAndId(),
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        handler::getListDerivativesOfExchangesOrderedAndPaginated)
                .build();
    }

}
