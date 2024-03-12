package ar.com.api.derivatives.handler

import ar.com.api.derivatives.enums.ErrorTypeEnums
import ar.com.api.derivatives.exception.ApiClientErrorException
import ar.com.api.derivatives.model.Derivative
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

}
