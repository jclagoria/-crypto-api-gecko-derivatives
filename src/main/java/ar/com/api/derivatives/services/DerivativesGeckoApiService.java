package ar.com.api.derivatives.services;

import ar.com.api.derivatives.exception.ManageExceptionCoinGeckoServiceApi;
import ar.com.api.derivatives.model.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.com.api.derivatives.dto.DerivativeDTO;
import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import ar.com.api.derivatives.dto.ExchangeIdDTO;
import ar.com.api.derivatives.model.Derivative;
import ar.com.api.derivatives.model.DerivativeData;
import ar.com.api.derivatives.model.DerivativeExchange;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DerivativesGeckoApiService extends CoinGeckoServiceApi {
 
 @Value("${api.derivativesGecko}")
 private String URL_GECKO_API_DERIVATIVES; 

 @Value("${api.derivativesExchangesGecko}")
 private String URL_GECKO_API_DERIVATIVES_EXCHANGE;

 @Value("${api.derivativesExchangesByIdGecko}")
 private String URL_GECKO_API_DERIVATIVES_EXCHANGE_ID;

 @Value("${api.derivativesExchangesListNameAndId}")
 private String URL_GECKO_API_DERIVATIVES_LIST;

 private WebClient webClient;

 public DerivativesGeckoApiService(WebClient wClient) {
  this.webClient = wClient;
 }

 /**
  * 
  * @param filterDTO
  * @return
  */
 public Flux<Derivative> getListOfDerivatives(DerivativeDTO filterDTO) {

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES);

  return webClient
          .get()
          .uri(URL_GECKO_API_DERIVATIVES + filterDTO.getUrlFilterString())
          .retrieve()
          .onStatus(
                  HttpStatusCode::is4xxClientError,
                  getClientResponseMonoDataException()
          )
          .onStatus(
                  HttpStatusCode::is5xxServerError,
                  getClientResponseMonoServerException()
          )
          .bodyToFlux(Derivative.class)
          .doOnError(
                  ManageExceptionCoinGeckoServiceApi::throwServiceException
          );
 }

 /**
  * 
  * @param filterDTO
  * @return
  */
 public Flux<DerivativeExchange> getListDerivativeExhcangeOrderedAndPaginated(
                                               DerivativeExchangeDTO filterDTO
                                               ) {

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES_EXCHANGE);

  return webClient
          .get()
          .uri(URL_GECKO_API_DERIVATIVES_EXCHANGE + filterDTO.getUrlFilterString())
          .retrieve()
          .onStatus(
                  HttpStatusCode::is4xxClientError,
                  getClientResponseMonoDataException()
          )
          .onStatus(
                  HttpStatusCode::is5xxServerError,
                  getClientResponseMonoServerException()
          )
          .bodyToFlux(DerivativeExchange.class)
          .doOnError(
                  ManageExceptionCoinGeckoServiceApi::throwServiceException
          );
 }

 public Mono<DerivativeData> showDerivativeExchangeData(ExchangeIdDTO filterDTO) {

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES_EXCHANGE_ID);

  String urlDerivativesByExchangeId = String.format(URL_GECKO_API_DERIVATIVES_EXCHANGE_ID, filterDTO.getIdExchange());

  return webClient
          .get()
          .uri(urlDerivativesByExchangeId + filterDTO.getUrlFilterString())
          .retrieve()
          .onStatus(
                  HttpStatusCode::is4xxClientError,
                  getClientResponseMonoDataException()
          )
          .onStatus(
                  HttpStatusCode::is5xxServerError,
                  getClientResponseMonoServerException()
          )
          .bodyToMono(DerivativeData.class)
          .doOnError(
                  ManageExceptionCoinGeckoServiceApi::throwServiceException
          );
 }

 public Flux<Exchange> getListOfDerivativesExchanges(){

  log.info("Calling method: ", URL_GECKO_API_DERIVATIVES_LIST);

  return webClient
          .get()
          .uri(URL_GECKO_API_DERIVATIVES_LIST)
          .retrieve()
          .onStatus(
                  HttpStatusCode::is4xxClientError,
                  getClientResponseMonoDataException()
          )
          .onStatus(
                  HttpStatusCode::is5xxServerError,
                  getClientResponseMonoServerException()
          )
          .bodyToFlux(Exchange.class)
          .doOnError(
                  ManageExceptionCoinGeckoServiceApi::throwServiceException
          );
 }

}