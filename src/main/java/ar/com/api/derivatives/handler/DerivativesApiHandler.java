package ar.com.api.derivatives.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.derivatives.dto.DerivativeDTO;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.Ping;
import ar.com.api.derivatives.services.CoinGeckoServiceStatus;
import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class DerivativesApiHandler {
 
 private CoinGeckoServiceStatus serviceStatus;

 private DerivativesGeckoApiService serviceDerivatives;

 public Mono<ServerResponse> getStatusServiceCoinGecko(ServerRequest serverRequest) {

  log.info("In getStatusServiceCoinGecko");

  return ServerResponse
                .ok()
                .body(
                     serviceStatus.getStatusCoinGeckoService(), 
                     Ping.class);
 }

 public Mono<ServerResponse> getListOfDerivativesTickers(ServerRequest sRequest) {
     log.info("In getListOfDerivativesTickers");

     DerivativeDTO filterDTO = DerivativeDTO
                         .builder()
                         .includeTickers(sRequest.queryParam("includeTickers"))
                         .build();
     
     return ServerResponse
                    .ok()
                    .body(
                         serviceDerivatives.getListOfDerivatives(filterDTO),
                         Derivative.class
                         );
 }
 
}