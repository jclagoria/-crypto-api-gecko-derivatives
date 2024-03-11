package ar.com.api.derivatives.services

import ar.com.api.derivatives.configuration.ExternalServerConfig
import org.springframework.web.reactive.function.client.WebClient
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
        externalServerConfig.getDerivativesGecko() >> "mockUriDerivativesGecko"
        externalServerConfig.getDerivativesExchangesByIdGecko() >> "mockUriDerivativeData"
        externalServerConfig.getDerivativesExchangesGecko() >> "mockUriDerivativesExchange"

        webClientMock.get() >> requestHeaderUriMock
        requestHeaderUriMock.uri(_ as String) >> requestHeadersMock
        requestHeadersMock.retrieve() >> responseSpecMock
        responseSpecMock.onStatus(_ as Predicate, _ as Function) >> responseSpecMock

        derivativesGeckoApiServiceMock = new DerivativesGeckoApiService(webClientMock, externalServerConfig)
    }

}