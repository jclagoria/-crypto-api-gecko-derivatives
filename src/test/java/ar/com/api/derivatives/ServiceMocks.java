package ar.com.api.derivatives;

import ar.com.api.derivatives.services.DerivativesGeckoApiService;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ServiceMocks {

    public static DerivativesGeckoApiService mockDerivativeGeckoApiService() {
        return Mockito.mock(DerivativesGeckoApiService.class);
    }

}
