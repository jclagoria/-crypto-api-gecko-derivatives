package ar.com.api.derivatives.router;

import ar.com.api.derivatives.exception.CoinGeckoDataNotFoudException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

public class AbstractRouterConfig {

    public WebExceptionHandler exceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof CoinGeckoDataNotFoudException) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            }
            return Mono.error(ex);
        };

    }
}
