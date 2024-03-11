package ar.com.api.derivatives.router;

import ar.com.api.derivatives.configuration.ApiServiceConfig;
import ar.com.api.derivatives.handler.HealthApiHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HealthApiRouter {

    private final ApiServiceConfig apiServiceConfig;

    public HealthApiRouter(ApiServiceConfig serviceConfig) {
        this.apiServiceConfig = serviceConfig;
    }

    @Bean
    public RouterFunction<ServerResponse> routeHealth(HealthApiHandler handler) {

        return RouterFunctions
                .route()
                .GET(apiServiceConfig.getBaseURL() + apiServiceConfig.getHealthAPI(),
                        handler::getStatusServiceCoinGecko)
                .build();
    }
}
