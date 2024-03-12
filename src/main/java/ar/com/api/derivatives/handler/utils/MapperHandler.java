package ar.com.api.derivatives.handler.utils;

import ar.com.api.derivatives.dto.DerivativeExchangeDTO;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class MapperHandler {

    public static Mono<DerivativeExchangeDTO> createDerivativeExchangeDTOFromRequest(ServerRequest sRequest) {
        Optional<Integer> optPage = sRequest.queryParam("page").map(Integer::valueOf);
        Optional<Integer> optPerPage = sRequest.queryParam("perPage").map(Integer::valueOf);

        return Mono.just(DerivativeExchangeDTO.builder()
                .order(sRequest.queryParam("order"))
                .page(optPage)
                .perPage(optPerPage)
                .build());
    }

}
