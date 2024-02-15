package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.model.Ping
import ar.com.api.derivatives.utils.ValidationUtils
import org.instancio.Instancio
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.function.Function
import java.util.function.Predicate

@Stepwise
class CoinGeckoServiceStatusTest extends Specification {

    CoinGeckoServiceStatus coinGeckoServiceStatus
    WebClient webClientMock = Mock()
    WebClient.ResponseSpec responseSpecMock = Mock()
    ExternalServerConfig externalServerConfig = Mock()

    def setup() {
        def requestHeaderUriMock = Mock(WebClient.RequestHeadersUriSpec)
        def requestHeaderMock = Mock(WebClient.RequestHeadersSpec)

        externalServerConfig.getUrlCoinGecko() >> "mockUrlGlobal"
        externalServerConfig.getPing() >> "mockUrlPing"

        webClientMock.get() >> requestHeaderUriMock
        requestHeaderUriMock.uri(_ as String) >> requestHeaderMock
        requestHeaderMock.retrieve() >> responseSpecMock
        responseSpecMock.onStatus(_ as Predicate, _ as Function) >> responseSpecMock

        coinGeckoServiceStatus = new CoinGeckoServiceStatus(webClientMock, externalServerConfig)
    }

    def "getStatusCoinGeckoService returns a String successfully"() {
        given:
            Ping expectedPingMock = Instancio.create(Ping)
            responseSpecMock.bodyToMono(Ping) >> Mono.just(expectedPingMock)

        when:
            Mono<Ping> actualPing = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then:
            StepVerifier
                    .create(actualPing)
                    .expectNext(expectedPingMock)
                    .verifyComplete()
    }

    def "getStatusCoinGeckoService handle 400 Bad Request Error"() {
        given:
            simulateWebClientErrorResponseForMono(400, "Bad Request", Ping)

        when:
            Mono<Ping> result4xxError = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then:
            ValidationUtils.validate4xxError(result4xxError)
    }

    def "getStatusCoinGeckoService handle 500 Internal Server Error"() {
        given:
            simulateWebClientErrorResponseForMono(500, "Internal Server Error", Ping)

        when:
            Mono<Ping> result5xxError = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then:
            ValidationUtils.validate5xxError(result5xxError)
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
