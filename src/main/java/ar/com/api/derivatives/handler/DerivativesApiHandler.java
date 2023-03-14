package ar.com.api.derivatives.handler;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ar.com.api.derivatives.dto.DerivativeDTO;
import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.DerivativeExchange;
import ar.com.api.derivatives.model.Ping;
import ar.com.api.derivatives.services.CoinGeckoServiceStatus;
import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import ar.com.api.derivatives.utils.StringToInteger;
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

 public Mono<ServerResponse> getListDerivativesOfExchangesOrderedAndPaginated(ServerRequest sRequest) {

     log.info("In getListDerivativesOfExchangesOrderedAndPaginated");

     Optional<Integer> optPerPage =  Optional.empty();
     Optional<Integer> optPage = Optional.empty();

     if(sRequest.queryParam("perPage").isPresent()){
          optPerPage =  Optional
                    .of(sRequest.queryParam("perPage")
                    .get()
                    .transform(StringToInteger.INSTANCE));
     }
     
     if(sRequest.queryParam("page").isPresent()){
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
                         serviceDerivatives.getListDerivativeExhcangeOrderedAndPaginated(filterDTO), DerivativeExchange.class
                         );
 }
 
}