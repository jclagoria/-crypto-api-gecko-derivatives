package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.configuration.HttpServiceCall
import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiServerErrorException
import ar.com.api.derivatives.model.Derivative
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class DerivativesGeckoApiServiceTest extends Specification {

    HttpServiceCall httpServiceCallMock
    ExternalServerConfig externalServerConfigMock
    DerivativesGeckoApiService derivativesGeckoApiService

    def setup() {
        httpServiceCallMock = Mock(HttpServiceCall)
        externalServerConfigMock = Mock(ExternalServerConfig)

        externalServerConfigMock.getUrlCoinGecko() >> "mockUrlGlobal"
        externalServerConfigMock.getDerivativesExchangesList() >> "mockUriListDerivatives"
        externalServerConfigMock.getDerivativesGecko() >> "mockUriDerivativesGecko"
        externalServerConfigMock.getDerivativesExchangesByIdGecko() >> "mockUriDerivativeData"
        externalServerConfigMock.getDerivativesExchangesGecko() >> "mockUriDerivativesExchange"

        derivativesGeckoApiService = new DerivativesGeckoApiService(httpServiceCallMock, externalServerConfigMock)
    }

    def "GetListOfDerivatives should successfully retrieve a list of Derivatives"() {
        given: "A mock setup for HttpServiceCall and Mock List of Derivatives"
        def expectedListDerivatives = Instancio.ofList(Derivative).size(7).create()
        httpServiceCallMock.getFluxObject(_ as String, Derivative.class)
                >> Flux.fromIterable(expectedListDerivatives)

        when: "GetListOfDerivatives is called"
        def returnListObject = derivativesGeckoApiService.getListOfDerivatives()

        then: "The correct number of Derivatives is returned, and content is verified"
        StepVerifier.create(returnListObject)
                .recordWith(ArrayList::new)
                .expectNextCount(expectedListDerivatives.size())
                .consumeRecordedWith {actualListDerivatives ->
                    assert actualListDerivatives.containsAll(expectedListDerivatives): "The content of the List should not be different"
                }
                .verifyComplete()
    }

    def "GetListOfDerivatives should handle 4xx client error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttpServiceCall with a 4xx client error"
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiClient",
                "Request Timeout",
                ErrorTypeEnums.GECKO_CLIENT_ERROR,
                HttpStatus.REQUEST_TIMEOUT)
        httpServiceCallMock.getFluxObject(_ as String, Derivative.class) >> Flux.error(clientErrorExpected)

        when: "GetListOfDerivatives is called with a 4xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService.getListOfDerivatives()

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }.verify()
    }

    def "GetListOfDerivatives should handle 5xx server error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttpServiceCall with a 5xx server error"
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiServer",
                "Bad Gateway",
                ErrorTypeEnums.GECKO_SERVER_ERROR,
                HttpStatus.BAD_GATEWAY)
        httpServiceCallMock.getFluxObject(_ as String, Derivative.class) >> Flux.error(clientErrorExpected)

        when: "GetListOfDerivatives is called with a 5xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService.getListOfDerivatives()

        then: "The service return 5xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }.verify()
    }

}