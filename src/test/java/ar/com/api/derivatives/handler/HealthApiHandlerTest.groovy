package ar.com.api.derivatives.handler

import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiClientErrorException
import ar.com.api.derivatives.model.Ping
import ar.com.api.derivatives.services.CoinGeckoServiceStatus
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class HealthApiHandlerTest extends Specification {

    CoinGeckoServiceStatus coinGeckoServiceStatusMock
    ServerRequest serverRequestMock
    HealthApiHandler apiHandler

    def setup() {
        coinGeckoServiceStatusMock = Mock(CoinGeckoServiceStatus)
        serverRequestMock = Mock(ServerRequest)

        apiHandler = new HealthApiHandler(coinGeckoServiceStatusMock)
    }

    def "GetStatusServiceCoinGecko return 200 Ok with expected body successfully response"() {
        given: "A mock CoinGeckoServiceStatus and a successfully Ping response"
        def expectedPing = Instancio.create(Ping.class)
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >> Mono.just(expectedPing)

        when: "CoinGeckoServiceStatus is called and return a Object"
        def actualObject = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "The response is 200 Ok with the expected body"
        StepVerifier.create(actualObject)
                .assertNext {actualResponse ->
                    assert actualResponse.statusCode() == HttpStatus.OK: "Status should not be different to OK"
                    assert actualResponse.headers().getContentType() ==
                            MediaType.APPLICATION_JSON: "ContentType should not be different to Application Json"
                }
                .verifyComplete()
    }

    def "GetStatusServiceCoinGecko return 404 Not Found for empty service response"() {
        given: "A mock CoinGeckoServiceStatus and a empty response"
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >> Mono.empty()

        when: "GetStatusServiceCoinGecko is called and return empty object"
        def actualEmptyObject = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "The response expected is 404 Not Found"
        StepVerifier.create(actualEmptyObject)
                .assertNext {actualResponse ->
                    assert actualResponse.statusCode() == HttpStatus.NOT_FOUND: "StatusCode should by Not Found"
                    assert actualResponse.headers().isEmpty()
                }
                .verifyComplete()
    }

    def "GetStatusServiceCoinGecko handle error gracefully"() {
        given: "A mock GetStatusServiceCoinGecko and an error response"
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >>
                Mono.error(new RuntimeException("An error occurred"))

        when: "GetStatusServiceCoinGecko is called"
        def actualErrorResponse = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "It handles the error and returns an internal server error"
        StepVerifier.create(actualErrorResponse)
                .expectErrorMatches {actualError ->
                    actualError instanceof ApiClientErrorException &&
                            actualError.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR &&
                            actualError.getErrorTypeEnums() == ErrorTypeEnums.API_SERVER_ERROR &&
                            actualError.getMessage() == "An expected error occurred in getStatusServiceCoinGecko"
                }
                .verify()
    }

}
