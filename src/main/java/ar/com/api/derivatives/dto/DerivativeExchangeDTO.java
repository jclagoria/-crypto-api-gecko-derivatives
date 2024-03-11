package ar.com.api.derivatives.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class DerivativeExchangeDTO implements IFilterDTO {

    private Optional<String> order;
    private Optional<Integer> perPage;
    private Optional<Integer> page;

    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("?per_page=").append(perPage.orElse(50))
                .append("&page=").append(page.orElse(1));

        if (order.isPresent()) {
            urlBuilder.append("&order=").append(perPage.get());
        }

        return urlBuilder.toString();
    }

}
