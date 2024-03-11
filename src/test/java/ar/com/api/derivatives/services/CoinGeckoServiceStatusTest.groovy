package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.configuration.HttpServiceCall
import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiServerErrorException
import ar.com.api.derivatives.model.Ping
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class CoinGeckoServiceStatusTest extends Specification {

    HttpServiceCall httpServiceCallMock
    ExternalServerConfig externalServerConfigMock
    CoinGeckoServiceStatus coinGeckoServiceStatus

    def setup() {
        httpServiceCallMock = Mock(HttpServiceCall)
        externalServerConfigMock = Mock(ExternalServerConfig)

        externalServerConfigMock.getPing() >> "mockUrlPing"

        coinGeckoServiceStatus = new CoinGeckoServiceStatus(httpServiceCallMock, externalServerConfigMock)
    }

    def "GetStatusCoinGeckoService should successfully retrieve service status"() {
        given: "A mock setup for HttpServiceCall and ExternalServerConfig"
        def expectedPingObject = Instancio.create(Ping.class)
        httpServiceCallMock.getMonoObject(_ as String, Ping.class) >> Mono.just(expectedPingObject)

        when: "GetStatusCoinGeckoService is called without filterDTO"
        def actualObject = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then: "The service returns the expected Ping object"
        StepVerifier.create(actualObject)
                .assertNext { pingObject ->
                    assert pingObject.getGeckoSays() != null: "Ping should not be null"
                    assert pingObject.getGeckoSays() == expectedPingObject.getGeckoSays(): "Gecko says field not match"
                }
                .verifyComplete()
    }

    def "GetStatusCoinGeckoService should handle 4xx client error gracefully"() {
        given: "A mock setup HttpServiceCall and ExternalServerConfig with a 4xx client error"
        def expectedApiClientError = new ApiServerErrorException("An error occurred on APIClient",
                "Bad Request",
                ErrorTypeEnums.GECKO_CLIENT_ERROR, HttpStatus.BAD_REQUEST)
        httpServiceCallMock.getMonoObject(_ as String, Ping.class) >> Mono.error(expectedApiClientError)

        when: "GetStatusCoinGeckoService is called with a 4xx error scenario"
        def actualErrorObject = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then: "The service gracefully handle the error"
        StepVerifier.create(actualErrorObject)
                .expectErrorMatches { errorObject ->
                    errorObject instanceof ApiServerErrorException &&
                            errorObject.getHttpStatus().is4xxClientError() &&
                            errorObject.getErrorTypeEnums() == ErrorTypeEnums.GECKO_CLIENT_ERROR &&
                            errorObject.getOriginalMessage() == expectedApiClientError.getOriginalMessage()
                }
                .verify()
    }

    def "GetStatusCoinGeckoService should handle 5xx client error gracefully"() {
        given: "A mock setup HttpServiceCall and ExternalServerConfig with a 5xx client error"
        def expectedApiClientError = new ApiServerErrorException("An error occurred on APIServer",
                "Internal Server Error",
                ErrorTypeEnums.GECKO_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        httpServiceCallMock.getMonoObject(_ as String, Ping.class) >> Mono.error(expectedApiClientError)

        when: "GetStatusCoinGeckoService is called with a 5xx error scenario"
        def actualErrorObject = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then: "The service gracefully handle the error"
        StepVerifier.create(actualErrorObject)
                .expectErrorMatches { errorObject ->
                    errorObject instanceof ApiServerErrorException &&
                            errorObject.getHttpStatus().is5xxServerError() &&
                            errorObject.getErrorTypeEnums() == ErrorTypeEnums.GECKO_SERVER_ERROR &&
                            errorObject.getOriginalMessage() == expectedApiClientError.getOriginalMessage()
                }
                .verify()
    }

}
