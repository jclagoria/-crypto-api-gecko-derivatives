package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.exception.ManageExceptionCoinGeckoServiceApi
import ar.com.api.derivatives.exception.ServiceException
import ar.com.api.derivatives.model.Derivative
import ar.com.api.derivatives.model.Exchange
import org.instancio.Instancio
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Shared
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
        externalServerConfig.getDerivativesGecko() >> "mockUriDerivatives"

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
            simulateWebClientErrorResponse(400, "Bad Request", Derivative)

        when:
            Flux<Exchange> result = derivativesGeckoApiServiceMock.getListOfDerivatives()

        then:
            StepVerifier.create(result)
                    .expectErrorMatches(
                            throwable ->
                                    throwable instanceof ServiceException &&
                                            throwable.getCause() instanceof WebClientResponseException &&
                                            ((WebClientResponseException) throwable.getCause())
                                                    .getStatusCode().is4xxClientError())
                    .verify()
    }

    def "getListOfDerivatives handles 500 Internal Server Exception"() {
        given:
            simulateWebClientErrorResponse(500, "Internal Server Exception", Derivative)

        when:
            Flux<Exchange> error5xxResult = derivativesGeckoApiServiceMock.getListOfDerivatives()

        then:
            StepVerifier.create(error5xxResult)
                    .expectErrorMatches {throwable ->
                        throwable instanceof ServiceException &&
                                throwable.getCause() instanceof  WebClientResponseException &&
                                ((WebClientResponseException) throwable.getCause())
                                        .getStatusCode().is5xxServerError()
                    }
                    .verify()
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
        simulateWebClientErrorResponse(400, "Bad Request", Exchange)

        when:
        Flux<Exchange> result4xxError = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

        then:
        StepVerifier.create (result4xxError).expectErrorMatches (
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is4xxClientError()
        ).verify()
    }

    def "getListOfDerivativesExchanges handles 500 Internal Server Error"() {
        given:
        simulateWebClientErrorResponse(500, "Internal Server Error", Exchange)

        when:
        Flux<Exchange> result4xxError = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

        then:
        StepVerifier.create (result4xxError).expectErrorMatches (
                throwable -> throwable instanceof ServiceException &&
                        throwable.getCause() instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable.getCause()).getStatusCode().is5xxServerError()
        ).verify()
    }

    private void simulateWebClientErrorResponse(int statusCode, String statusMessage, Class responseType) {
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




}
