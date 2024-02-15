package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.dto.DerivativeExchangeDTO
import ar.com.api.derivatives.dto.ExchangeIdDTO
import ar.com.api.derivatives.model.Derivative
import ar.com.api.derivatives.model.DerivativeData
import ar.com.api.derivatives.model.DerivativeExchange
import ar.com.api.derivatives.model.Exchange
import ar.com.api.derivatives.utils.ValidationUtils
import org.instancio.Instancio
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.function.Function
import java.util.function.Predicate

@Stepwise
class DerivativesGeckoApiServiceTest extends Specification {

    DerivativesGeckoApiService derivativesGeckoApiServiceMock
    WebClient webClientMock = Mock()
    WebClient.ResponseSpec responseSpecMock = Mock()
    ExternalServerConfig externalServerConfig = Mock()

    def setup() {
        def requestHeaderUriMock = Mock(WebClient.RequestHeadersUriSpec)
        def requestHeadersMock = Mock(WebClient.RequestHeadersSpec)

        externalServerConfig.getUrlCoinGecko() >> "mockUrlGlobal"
        externalServerConfig.getDerivativesExchangesList() >> "mockUriListDerivatives"
        externalServerConfig.getDerivativesGecko() >> "mockUriDerivativesGecko"
        externalServerConfig.getDerivativesExchangesByIdGecko() >> "mockUriDerivativeData"
        externalServerConfig.getDerivativesExchangesGecko() >> "mockUriDerivativesExchange"

        webClientMock.get() >> requestHeaderUriMock
        requestHeaderUriMock.uri(_ as String) >> requestHeadersMock
        requestHeadersMock.retrieve() >> responseSpecMock
        responseSpecMock.onStatus(_ as Predicate, _ as Function) >> responseSpecMock

        derivativesGeckoApiServiceMock = new DerivativesGeckoApiService(webClientMock, externalServerConfig)
    }

    def "getListOfDerivatives returns a list of derivatives successfully"() {
        given:
            Derivative expectedDerivativeMock = Instancio.create(Derivative)
            responseSpecMock.bodyToFlux(Derivative) >> Flux.just(expectedDerivativeMock)

        when:
            Flux<Derivative> actualDerivative = derivativesGeckoApiServiceMock.getListOfDerivatives()

        then:
            StepVerifier
                    .create(actualDerivative)
                    .expectNext(expectedDerivativeMock)
                    .verifyComplete()
    }

    def "getListOfDerivatives handles 400 Bad Request Error"() {
        given:
            simulateWebClientErrorResponseForFlux(400, "Bad Request", Derivative)

        when:
            Flux<Exchange> result4xxError = derivativesGeckoApiServiceMock.getListOfDerivatives()

        then:
            ValidationUtils.validate4xxError(result4xxError)
    }

    def "getListOfDerivatives handles 500 Internal Server Exception"() {
        given:
            simulateWebClientErrorResponseForFlux(500, "Internal Server Exception", Derivative)

        when:
            Flux<Exchange> error5xxResult = derivativesGeckoApiServiceMock.getListOfDerivatives()

        then:
            ValidationUtils.validate5xxError(error5xxResult)
    }

    def "getListOfDerivativesExchanges returns a list of exchanges successfully"() {
        given:
            Exchange expectedExchangeMock = Instancio.create(Exchange)
            responseSpecMock.bodyToFlux(Exchange) >> Flux.just(expectedExchangeMock)

        when:
            Flux<Exchange> actualExchange = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

        then:
            StepVerifier
                    .create(actualExchange)
                    .expectNext(expectedExchangeMock)
                    .verifyComplete()
    }

    def "getListOfDerivativesExchanges handles 400 Bad Request Error"() {
        given:
            simulateWebClientErrorResponseForFlux(400, "Bad Request", Exchange)

        when:
            Flux<Exchange> result4xxError = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

        then:
            ValidationUtils.validate4xxError(result4xxError)
    }

    def "getListOfDerivativesExchanges handles 500 Internal Server Error"() {
        given:
            simulateWebClientErrorResponseForFlux(500, "Internal Server Error", Exchange)

        when:
            Flux<Exchange> result5xxError = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

        then:
            ValidationUtils.validate5xxError(result5xxError)
    }

    def "getShowDerivativeExchangeData returns derivative data successfully"() {
        given:
            ExchangeIdDTO filterDTO = Instancio.create(ExchangeIdDTO)
            DerivativeData expectedDerivativeData = Instancio.create(DerivativeData)
            responseSpecMock.bodyToMono(DerivativeData) >> Mono.just(expectedDerivativeData)

        when:
            Mono<DerivativeData> actualDerivativeData = derivativesGeckoApiServiceMock
                    .getShowDerivativeExchangeData(filterDTO)

        then:
            StepVerifier
                    .create(actualDerivativeData)
                    .expectNext(expectedDerivativeData)
                    .verifyComplete()
    }

    def "getShowDerivativeExchangeData handles 400 Bad Request Error"() {
        given:
            ExchangeIdDTO filterDTO = Instancio.create(ExchangeIdDTO)
            simulateWebClientErrorResponseForMono(400, "Bad Request", DerivativeData)

        when:
        Mono<DerivativeData> result4xxError = derivativesGeckoApiServiceMock.getShowDerivativeExchangeData(filterDTO)

        then:
            ValidationUtils.validate4xxError(result4xxError)
    }

    def "getShowDerivativeExchangeData handles 500 Internal Server Error"() {
        given:
            ExchangeIdDTO filterDTO = Instancio.create(ExchangeIdDTO)
            simulateWebClientErrorResponseForMono(500, "Internal Server Error", DerivativeData)

        when:
            Mono<DerivativeData> result5xxError = derivativesGeckoApiServiceMock.getShowDerivativeExchangeData(filterDTO)

        then:
            ValidationUtils.validate5xxError(result5xxError)
    }

    def "getListDerivativeExchangedOrderedAndPaginated returns derivative data successfully"() {
        given:
            DerivativeExchangeDTO filterDTO = Instancio.create(DerivativeExchangeDTO)
            DerivativeExchange expectedDerivativeExchange = Instancio.create(DerivativeExchange)
            responseSpecMock.bodyToFlux(DerivativeExchange) >> Flux.just(expectedDerivativeExchange)

        when:
            Flux<DerivativeExchange> actualDerivativeExchange = derivativesGeckoApiServiceMock
                    .getListDerivativeExchangedOrderedAndPaginated(filterDTO)
        then:
            StepVerifier
                    .create(actualDerivativeExchange)
                    .expectNext(expectedDerivativeExchange)
                    .verifyComplete()
    }

    def "getListDerivativeExchangedOrderedAndPaginated handles 400 Bad Request Error"() {
        given:
            DerivativeExchangeDTO filterDTO = Instancio.create(DerivativeExchangeDTO)
            simulateWebClientErrorResponseForFlux(400, "Bad Request", DerivativeExchange)

        when:
            Flux<DerivativeExchange> result4xxError = derivativesGeckoApiServiceMock
                    .getListDerivativeExchangedOrderedAndPaginated(filterDTO)

        then:
            ValidationUtils.validate4xxError(result4xxError)
    }

    def "getListDerivativeExchangedOrderedAndPaginated handles 500 Server Internal Error"() {
        given:
            DerivativeExchangeDTO filterDTO = Instancio.create(DerivativeExchangeDTO)
            simulateWebClientErrorResponseForFlux(500,
                    "Server Internal Error", DerivativeExchange)

        when:
        Flux<DerivativeExchange> result5xxError = derivativesGeckoApiServiceMock
                .getListDerivativeExchangedOrderedAndPaginated(filterDTO)

        then:
        ValidationUtils.validate5xxError(result5xxError)
    }

    private void simulateWebClientErrorResponseForFlux(int statusCode, String statusMessage, Class responseType) {
        WebClient.ResponseSpec errorResponseSpecMock = Mock()
        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mock()
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mock()
        webClientMock.get() >> requestHeadersUriSpecMock
        requestHeadersUriSpecMock.uri(_) >> requestHeadersSpecMock
        requestHeadersSpecMock.retrieve() >> errorResponseSpecMock
        WebClientResponseException mockException = WebClientResponseException
                .create(statusCode, statusMessage, null, null, null)
        responseSpecMock.bodyToFlux(responseType) >> Flux.error(mockException)
    }

    private void simulateWebClientErrorResponseForMono(int statusCode, String statusMessage, Class responseType) {
        WebClient.ResponseSpec errorResponseSpecMock = Mock()
        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mock()
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mock()
        webClientMock.get() >> requestHeadersUriSpecMock
        requestHeadersUriSpecMock.uri(_) >> requestHeadersSpecMock
        requestHeadersSpecMock.retrieve() >> errorResponseSpecMock
        WebClientResponseException mockException = WebClientResponseException
                .create(statusCode, statusMessage, null, null, null)
        responseSpecMock.bodyToMono(responseType) >> Mono.error(mockException)
    }

}