package ar.com.api.derivatives.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.com.api.derivatives.dto.DerivativeDTO;
import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.DerivativeExchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class DerivativesGeckoApiService {
 
 @Value("${api.derivativesGecko}")
 private String URL_GECKO_API_DERIVATIVES; 

 @Value("${api.derivativesExchangesGecko}")
 private String URL_GECKO_API_DERIVATIVES_EXCHANGE; 

 private WebClient webClient;

 public DerivativesGeckoApiService(WebClient wClient) {
  this.webClient = wClient;
 }

 public Flux<Derivative> getListOfDerivatives(DerivativeDTO filterDTO) {

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES);

  return webClient
             .get()
             .uri(URL_GECKO_API_DERIVATIVES + filterDTO.getUrlFilterString())
             .retrieve()
             .bodyToFlux(Derivative.class)
             .doOnError(throwable -> log.error("The service is unavailable!", throwable))
             .onErrorComplete();
 }

 public Flux<DerivativeExchange> getListDerivativeExhcangeOrderedAndPaginated(
                                               DerivativeExchangeDTO filterDTO
                                               ) 
 {

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES_EXCHANGE);

  return webClient
             .get()
             .uri(URL_GECKO_API_DERIVATIVES_EXCHANGE + filterDTO.getUrlFilterString())
             .retrieve()
             .bodyToFlux(DerivativeExchange.class)
             .doOnError(throwable -> log.error("The service is unavailable!", throwable))
             .onErrorComplete();
 }

}