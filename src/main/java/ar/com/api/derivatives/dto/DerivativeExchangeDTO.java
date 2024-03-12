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
        urlBuilder.append("?order=").append(order.orElse("name_asc"));

        this.perPage.ifPresent(perPage -> urlBuilder.append("&per_page=").append(perPage));
        this.page.ifPresent(pageResp -> urlBuilder.append("&page=").append(pageResp));

        return urlBuilder.toString();
    }

}
