package ar.com.api.derivatives.handler

import ar.com.api.derivatives.dto.DerivativeExchangeDTO
import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiClientErrorException
import ar.com.api.derivatives.model.Derivative
import ar.com.api.derivatives.model.DerivativeExchange
import ar.com.api.derivatives.services.DerivativesGeckoApiService
import org.instancio.Instancio
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class DerivativesApiHandlerTest extends Specification {

    DerivativesGeckoApiService derivativesGeckoApiServiceMock
    ServerRequest serverRequestMock

    DerivativesApiHandler derivativesApiHandler

    def setup() {
        derivativesGeckoApiServiceMock = Mock(DerivativesGeckoApiService)
        serverRequestMock = Mock(ServerRequest)

        derivativesApiHandler = new DerivativesApiHandler(derivativesGeckoApiServiceMock)
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
                .expectNextMatches {response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON}
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
                .expectErrorMatches {actualError ->
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
                .expectNextMatches {response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON}
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

        then: "t handles the error and returns an Internal Server Error"
        StepVerifier.create(actualResponseError)
                .expectErrorMatches {actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnums.API_SERVER_ERROR == actualError.getErrorTypeEnums() &&
                            actualError.getMessage() == "An unexpected error occurred in getListDerivativesOfExchangesOrderedAndPaginated"
                }
                .verify()
    }

}