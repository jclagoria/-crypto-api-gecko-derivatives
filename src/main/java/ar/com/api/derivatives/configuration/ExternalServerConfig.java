package ar.com.api.derivatives.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api")
@Getter
@Setter
public class ExternalServerConfig {

    private String urlCoinGecko;
    private String ping;
    private String derivativesExchangesList;
    private String derivativesGecko;
    private String derivativesExchangesGecko;
    private String derivativesExchangesByIdGecko;

}
