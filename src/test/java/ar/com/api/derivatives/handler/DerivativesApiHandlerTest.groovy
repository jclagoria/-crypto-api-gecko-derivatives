package ar.com.api.derivatives.handler

import ar.com.api.derivatives.dto.DerivativeExchangeDTO
import ar.com.api.derivatives.dto.ExchangeIdDTO
import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiClientErrorException
import ar.com.api.derivatives.model.Derivative
import ar.com.api.derivatives.model.DerivativeData
import ar.com.api.derivatives.model.DerivativeExchange
import ar.com.api.derivatives.model.Exchange
import ar.com.api.derivatives.services.DerivativesGeckoApiService
import ar.com.api.derivatives.validators.ValidatorOfCTOComponent
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class DerivativesApiHandlerTest extends Specification {

    DerivativesGeckoApiService derivativesGeckoApiServiceMock
    ServerRequest serverRequestMock
    ValidatorOfCTOComponent validatorOfCTOComponentMock

    DerivativesApiHandler derivativesApiHandler

    def setup() {
        derivativesGeckoApiServiceMock = Mock(DerivativesGeckoApiService)
        serverRequestMock = Mock(ServerRequest)
        validatorOfCTOComponentMock = Mock(ValidatorOfCTOComponent)

        derivativesApiHandler = new DerivativesApiHandler(derivativesGeckoApiServiceMock, validatorOfCTOComponentMock)
    }

    def "GetListOfDerivativesTickers return successfully a ServerRequest with HttpStatus Ok"() {
        given: "Mocked ServerRequest and DerivativesGeckoApiService and DerivativesApiHandler return HttStatus Ok"
        def expectedListDerivatives = Instancio.ofList(Derivative.class).size(5).create()
        derivativesGeckoApiServiceMock.getListOfDerivatives() >>
                Flux.fromIterable(expectedListDerivatives)

        when: "GetListOfDerivativesTickers is called and return successfully ServerResponse"
        def actualResponse = derivativesApiHandler.getListOfDerivativesTickers(serverRequestMock)

        then: "It returns a ServerResponse with HttpResponse 200 Ok"
        StepVerifier.create(actualResponse)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetListOfDerivativesTickers handles and error gracefully"() {
        given: "A mock ServerRequest and an Internal Error Server"
        derivativesGeckoApiServiceMock.getListOfDerivatives() >> Flux.error(new RuntimeException("An error occurred"))

        when: "GetListOfDerivativesTickers called and expected a Internal Server Error"
        def errorActualResponse = derivativesApiHandler
                .getListOfDerivativesTickers(serverRequestMock)

        then: "It handles the error and returns an Internal Server Error"
        StepVerifier.create(errorActualResponse)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnums.API_SERVER_ERROR == actualError.getErrorTypeEnums() &&
                            actualError.getMessage() == "An unexpected error occurred in getListOfDerivativesTickers"
                }
                .verify()
    }

    def "GetListDerivativesOfExchangesOrderedAndPaginated return successfully a ServerRequest with HttpStatus Ok"() {
        given: "Mocked ServerRequest and Mock DerivativesGeckoApiService and DerivativesApiHandler return HttStatus Ok"
        def expectedListDerivativeExchangeMock = Instancio.ofList(DerivativeExchange.class)
                .size(3).create()
        serverRequestMock.queryParam(_ as String) >> Optional.of("10")
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        serverRequestMock.queryParam(_ as String) >> Optional.of("name_asc")
        derivativesGeckoApiServiceMock.getListDerivativeExchangedOrderedAndPaginated(_ as DerivativeExchangeDTO)
                >> Flux.fromIterable(expectedListDerivativeExchangeMock)

        when: "GetListDerivativesOfExchangesOrderedAndPaginated is called and return successfully ServerRequest"
        def actualResponseObject = derivativesApiHandler
                .getListDerivativesOfExchangesOrderedAndPaginated(serverRequestMock)

        then: "It returns a ServerResponse with the List of DerivativeExchange"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetListDerivativesOfExchangesOrderedAndPaginated handles and error gracefully"() {
        given: "Mocked ServerRequest and Mock DerivativesGeckoApiService and DerivativesApiHandler return Error scenario"
        serverRequestMock.queryParam(_ as String) >> Optional.of("10")
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        serverRequestMock.queryParam(_ as String) >> Optional.of("name_asc")
        derivativesGeckoApiServiceMock.getListDerivativeExchangedOrderedAndPaginated(_ as DerivativeExchangeDTO)
                >> Flux.error(new RuntimeException("An error occurred"))

        when: "GetListDerivativesOfExchangesOrderedAndPaginated called and expected a Internal Server Error"
        def actualResponseError = derivativesApiHandler
                .getListDerivativesOfExchangesOrderedAndPaginated(serverRequestMock)

        then: "It handles the error and returns an Internal Server Error"
        StepVerifier.create(actualResponseError)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnums.API_SERVER_ERROR == actualError.getErrorTypeEnums() &&
                            actualError.getMessage() == "An unexpected error occurred in getListDerivativesOfExchangesOrderedAndPaginated"
                }
                .verify()
    }

    def "GetShowDerivativeExchangeData return successfully a ServerRequest with HttpStatus Ok"() {
        given: "Mocked ServerRequest and Mock DerivativesGeckoApiService and DerivativesApiHandler return HttStatus Ok"
        def expectedObject = Instancio.create(DerivativeData.class)
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("true")
        validatorOfCTOComponentMock.validation(_) >> Mono.just(filterDTO)
        derivativesGeckoApiServiceMock.getShowDerivativeExchangeData(_ as ExchangeIdDTO)
                >> Mono.just(expectedObject)

        when: "GetShowDerivativeExchangeData is called and return successfully ServerRequest"
        def actualResponseObject = derivativesApiHandler
                .getShowDerivativeExchangeData(serverRequestMock)

        then: "It returns a ServerResponse with HttpStatus 200"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { actualResponse ->
                    actualResponse.statusCode().is2xxSuccessful()
                }
                .verifyComplete()
    }

    def "GetShowDerivativeExchangeData returns not found when no exchanges return form the API Service"() {
        given: "Mocked ServerRequest and Mock DerivativesGeckoApiService and DerivativesApiHandler return Not Found"
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("true")
        validatorOfCTOComponentMock.validation(_) >> Mono.just(filterDTO)
        derivativesGeckoApiServiceMock.getShowDerivativeExchangeData(_ as ExchangeIdDTO)
                >> Mono.empty()

        when: "GetShowDerivativeExchangeData is called and return Not Found"
        def actualResponseObject = derivativesApiHandler
                .getShowDerivativeExchangeData(serverRequestMock)

        then: "It return a not found response"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { response ->
                    response.statusCode() == HttpStatus.NOT_FOUND
                }
                .verifyComplete()
    }

    def "GetShowDerivativeExchangeData handles and error gracefully"() {
        given: "Mocked ServerRequest and Mock DerivativesGeckoApiService and DerivativesApiHandler return Error scenario"
        def filterDTO = Instancio.create(ExchangeIdDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("true")
        validatorOfCTOComponentMock.validation(_) >> Mono.just(filterDTO)
        derivativesGeckoApiServiceMock.getShowDerivativeExchangeData(_ as ExchangeIdDTO)
                >> Mono.error(new RuntimeException("An error occurred"))

        when: "GetShowDerivativeExchangeData is called and return successfully ServerRequest"
        def actualResponseObject = derivativesApiHandler
                .getShowDerivativeExchangeData(serverRequestMock)

        then: "It handles the error and returns an Internal Server Error"
        StepVerifier.create(actualResponseObject)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnums.API_SERVER_ERROR == actualError.getErrorTypeEnums() &&
                            actualError.getMessage() == "An unexpected error occurred in getShowDerivativeExchangeData"
                }
                .verify()
    }

    def "GetAllDerivativesExchanges return successfully a ServerRequest with HttpStatus Ok"() {
        given: "Mocked ServerRequest and DerivativesGeckoApiService and DerivativesApiHandler return HttStatus Ok"
        def expectedListExchanges = Instancio.ofList(Exchange.class).size(7).create()
        derivativesGeckoApiServiceMock.getListOfDerivativesExchanges() >>
                Flux.fromIterable(expectedListExchanges)

        when: "GetAllDerivativesExchanges is called and return successfully ServerResponse"
        def actualResponse = derivativesApiHandler.getAllDerivativesExchanges(serverRequestMock)

        then: "It returns a ServerResponse with HttpResponse 200 Ok"
        StepVerifier.create(actualResponse)
                .expectNextMatches {response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetAllDerivativesExchanges handles and error gracefully"() {
        given: "A mock ServerRequest and an Internal Error Server"
        derivativesGeckoApiServiceMock.getListOfDerivativesExchanges() >>
                Flux.error(new RuntimeException("An error occurred"))

        when: "GetListOfDerivativesTickers called and expected a Internal Server Error"
        def errorActualResponse = derivativesApiHandler
                .getAllDerivativesExchanges(serverRequestMock)

        then: "It handles the error and returns an Internal Server Error"
        StepVerifier.create(errorActualResponse)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnums.API_SERVER_ERROR == actualError.getErrorTypeEnums() &&
                            actualError.getMessage() == "An unexpected error occurred in getAllDerivativesExchanges"
                }
                .verify()
    }

}