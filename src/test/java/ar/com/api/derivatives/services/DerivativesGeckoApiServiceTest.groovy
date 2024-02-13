package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.exception.ManageExceptionCoinGeckoServiceApi
import ar.com.api.derivatives.exception.ServiceException
import ar.com.api.derivatives.model.Exchange
import org.instancio.Instancio
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
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

        webClientMock.get() >> requestHeaderUriMock
        requestHeaderUriMock.uri(_ as String) >> requestHeadersMock
        requestHeadersMock.retrieve() >> responseSpecMock
        responseSpecMock.onStatus(_ as Predicate, _ as Function) >> responseSpecMock

        derivativesGeckoApiServiceMock = new DerivativesGeckoApiService(webClientMock, externalServerConfig)
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
            WebClient.ResponseSpec badRequestResponseSpecMock = Mock()
            WebClient.RequestHeadersSpec requestHeadersSpecMock = Mock()
            WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mock()
            webClientMock.get() >> requestHeadersUriSpecMock
            requestHeadersUriSpecMock.uri(_) >> requestHeadersSpecMock
            requestHeadersSpecMock.retrieve() >> badRequestResponseSpecMock
            WebClientResponseException mockException = WebClientResponseException
                .create(400, "Bad Request", null, null, null)
            responseSpecMock.bodyToFlux(Exchange.class) >> Flux.error(mockException)

        when:
            Flux<Exchange> result = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

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

    def "getListOfDerivativesExchanges handles 500 Internal Server Exception"() {
        given:
            WebClient.ResponseSpec serverExceptionRequestResponseSpecMock = Mock()
            WebClient.RequestHeadersSpec requestHeadersSpecMock = Mock()
            WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mock()
            webClientMock.get() >> requestHeadersUriSpecMock
            requestHeadersUriSpecMock.uri(_) >> requestHeadersSpecMock
            requestHeadersSpecMock.retrieve(_) >> serverExceptionRequestResponseSpecMock
            WebClientResponseException mockException = WebClientResponseException
                .create(500, "Internal Server Exception", null, null, null)
            responseSpecMock.bodyToFlux(Exchange.class) >> Flux.error(mockException)

        when:
            Flux<Exchange> error5xxResult = derivativesGeckoApiServiceMock.getListOfDerivativesExchanges()

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
}
