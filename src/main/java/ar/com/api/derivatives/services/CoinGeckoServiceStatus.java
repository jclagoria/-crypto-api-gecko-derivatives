package ar.com.api.derivatives.services;

import ar.com.api.derivatives.configuration.ExternalServerConfig;
import ar.com.api.derivatives.configuration.HttpServiceCall;
import ar.com.api.derivatives.model.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CoinGeckoServiceStatus {

    private final ExternalServerConfig externalServerConfig;
    private final HttpServiceCall httpServiceCall;

    public CoinGeckoServiceStatus(HttpServiceCall serviceCall, ExternalServerConfig eServerConfig) {
        this.httpServiceCall = serviceCall;
        this.externalServerConfig = eServerConfig;
    }

    public Mono<Ping> getStatusCoinGeckoService() {
        log.info("Calling EndPoint on GeckoApi: {}", externalServerConfig.getPing());

        return httpServiceCall.getMonoObject(externalServerConfig.getPing(), Ping.class);
    }

}
