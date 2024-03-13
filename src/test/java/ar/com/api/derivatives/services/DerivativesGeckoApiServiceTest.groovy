package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import ar.com.api.derivatives.configuration.HttpServiceCall
import ar.com.api.derivatives.dto.DerivativeExchangeDTO
import ar.com.api.derivatives.dto.ExchangeIdDTO
import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiServerErrorException
import ar.com.api.derivatives.model.Derivative
import ar.com.api.derivatives.model.DerivativeData
import ar.com.api.derivatives.model.DerivativeExchange
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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
        externalServerConfigMock.getDerivativesExchangesByIdGecko() >> "/mockUriDerivativeData/exchanges/bitmex"
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
                .consumeRecordedWith { actualListDerivatives ->
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
                .expectErrorMatches { errorActual ->
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
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }.verify()
    }

    def "GetListDerivativeExchangedOrderedAndPaginated should successfully retrieve a list of all derivatives exchanges"() {
        given: "A mock setup for HttpServiceCall and Mock of DerivativeExchangeDTO and Mock of List DerivativeExchange"
        def filterDTOMock = Instancio.create(DerivativeExchangeDTO.class)
        def expectedDerivativeListMock = Instancio.ofList(DerivativeExchange).size(5).create()
        httpServiceCallMock.getFluxObject(_ as String, DerivativeExchange.class) >>
                Flux.fromIterable(expectedDerivativeListMock)

        when: "GetListDerivativeExchangedOrderedAndPaginated is called and return an DerivativeExchange"
        def returnListObject = derivativesGeckoApiService
                .getListDerivativeExchangedOrderedAndPaginated(filterDTOMock)

        then: "The correct number of DerivativeExchange is returned, and content is verified"
        StepVerifier.create(returnListObject)
                .recordWith(ArrayList::new)
                .expectNextCount(expectedDerivativeListMock.size())
                .consumeRecordedWith { listDerivativesExchanges ->
                    assert listDerivativesExchanges.containsAll(expectedDerivativeListMock): "The content of the List should not be different"
                }
                .verifyComplete()
    }

    def "GetListDerivativeExchangedOrderedAndPaginated should handle 4xx client error gracefully"() {
        given: "A mock setup for HttpServiceCall with a 4xx client error and Mock of DerivativeExchangeDTO"
        def filterDTO = Instancio.create(DerivativeExchangeDTO.class)
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiClient", "Forbidden",
                ErrorTypeEnums.GECKO_CLIENT_ERROR, HttpStatus.FORBIDDEN)
        httpServiceCallMock.getFluxObject(_ as String, DerivativeExchange.class) >>
                Flux.error(clientErrorExpected)

        when: "GetListDerivativeExchangedOrderedAndPaginated is called with a 4xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService
                .getListDerivativeExchangedOrderedAndPaginated(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetListDerivativeExchangedOrderedAndPaginated should handle 5xx client error gracefully"() {
        given: "A mock setup for HttpServiceCall with a 5xx client error and Mock of DerivativeExchangeDTO"
        def filterDTO = Instancio.create(DerivativeExchangeDTO.class)
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiServer", "Insufficient Storage",
                ErrorTypeEnums.GECKO_SERVER_ERROR, HttpStatus.INSUFFICIENT_STORAGE)
        httpServiceCallMock.getFluxObject(_ as String, DerivativeExchange.class) >>
                Flux.error(clientErrorExpected)

        when: "GetListDerivativeExchangedOrderedAndPaginated is called with a 5xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService
                .getListDerivativeExchangedOrderedAndPaginated(filterDTO)

        then: "The service return 5xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetShowDerivativeExchangeData should successfully retrieve an DerivativeData object"() {
        given: "A mock setup HttpServiceCall and Mock of ExchangeIdDTO and Mock of DerivativeData"
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        def expectedObject = Instancio.create(DerivativeData.class)
        httpServiceCallMock.getMonoObject(_ as String, DerivativeData.class) >> Mono.just(expectedObject)

        when: "GetShowDerivativeExchangeData is called and return on Object DerivativeData"
        def actualObject = derivativesGeckoApiService.getShowDerivativeExchangeData(filterDTO)

        then: "The service return an Object successfully, and validate the content"
        StepVerifier.create(actualObject)
                .assertNext { derivativeDataExp ->
                    assert derivativeDataExp != null: "The expected object should not be null"
                    assert derivativeDataExp.getName() == expectedObject.getName(): "The name of the derivative date on expected and actual should not be different"
                    assert derivativeDataExp.getCountry() == expectedObject.getCountry(): "The country of the country date on expected and actual should not be different"
                    assert derivativeDataExp.getImage() == expectedObject.getImage(): "The image of the country date on expected and actual should not be different"
                }
                .verifyComplete()
    }

    def "GetShowDerivativeExchangeData should handle 4xx client error gracefully"() {
        given: "A mock setup for HttpServiceCall with a 4xx client error and Mock of DerivativeExchangeDTO"
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiClient", "Forbidden",
                ErrorTypeEnums.GECKO_CLIENT_ERROR, HttpStatus.FORBIDDEN)
        httpServiceCallMock.getMonoObject(_ as String, DerivativeData.class) >>
                Mono.error(clientErrorExpected)

        when: "GetListDerivativeExchangedOrderedAndPaginated is called with a 4xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService
                .getShowDerivativeExchangeData(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetShowDerivativeExchangeData should handle 5xx client error gracefully"() {
        given: "A mock setup for HttpServiceCall with a 5xx client error and Mock of DerivativeExchangeDTO"
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        def clientErrorExpected = new ApiServerErrorException("An error occurred in ApiServer", "Bandwidth Limit Exceeded",
                ErrorTypeEnums.GECKO_SERVER_ERROR, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
        httpServiceCallMock.getMonoObject(_ as String, DerivativeData.class) >>
                Mono.error(clientErrorExpected)

        when: "GetListDerivativeExchangedOrderedAndPaginated is called with a 5xx error scenario"
        def actualExceptionObject = derivativesGeckoApiService
                .getShowDerivativeExchangeData(filterDTO)

        then: "The service return 5xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServerErrorException &&
                            errorActual.getErrorTypeEnums() == ErrorTypeEnums.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

}