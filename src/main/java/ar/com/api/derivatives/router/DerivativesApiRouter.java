package ar.com.api.derivatives.router;

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
 
 @Value("${coins.baseURL}")
 private String URL_SERVICE_API;

 @Value("${coins.derivativesApi}") 
 private String URL_DERIVATIVES_GECKO_API;

 @Value("${coins.derivativesExchangesApi}") 
 private String URL_DERIVATIVES_EXCHANGE_GECKO_API;

 @Value("${coins.derivativesExchangesByIdGecko}")
 private String URL_DERIVATIVES_EXCHANGE_BY_ID_GECKO_API;

 @Value("${coins.derivativesExchangesListNameAndId}")
 private String URL_ALL_DERIVATIVES_EXCHANGE_ONLY_NAME_AND_ID;
 
 @Bean
 public RouterFunction<ServerResponse> route(DerivativesApiHandler handler) {

  return RouterFunctions
          .route()
          .GET(URL_SERVICE_API + URL_ALL_DERIVATIVES_EXCHANGE_ONLY_NAME_AND_ID,
                  handler::getAllDerivativesExchanges)
          .GET(URL_SERVICE_API + URL_DERIVATIVES_GECKO_API,
                  RequestPredicates.accept(MediaType.APPLICATION_JSON),
                  handler::getListOfDerivativesTickers)
          .GET(URL_SERVICE_API + URL_DERIVATIVES_EXCHANGE_GECKO_API,
                  RequestPredicates.accept(MediaType.APPLICATION_JSON),
                  handler::getListDerivativesOfExchangesOrderedAndPaginated)
          .GET(URL_SERVICE_API + URL_DERIVATIVES_EXCHANGE_BY_ID_GECKO_API,
                  RequestPredicates.accept(MediaType.APPLICATION_JSON),
                  handler::getShowDerivativeExchangeData)
          .build();
 }

}
