package ar.com.api.derivatives.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class ExchangeIdDTO implements IFilterDTO {

    @NotBlank(message = "Exchange ID cannot be blanc.")
    @NotEmpty(message = "Exchange ID cannot be empty.")
    private String idExchange;
    private Optional<String> includeTickers;

    @Override
    public String getUrlFilterString() {

        StringBuilder urlBuilder = new StringBuilder();
        this.includeTickers.ifPresent(inclTickers ->
                urlBuilder.append("?include_tickers=").append(inclTickers));

        return urlBuilder.toString();
    }


}
