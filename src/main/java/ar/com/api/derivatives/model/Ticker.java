package ar.com.api.derivatives.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("base")
    private String base;

    @JsonProperty("target")
    private String target;

    @JsonProperty("trade_url")
    private String tradeUrl;

    @JsonProperty("contract_type")
    private String contractType;

    @JsonProperty("last")
    private double last;

    @JsonProperty("h24_percentage_change")
    private double h24PercentageChange;

    @JsonProperty("index")
    private double index;

    @JsonProperty("index_basis_percentage")
    private double indexBasisPercentage;

    @JsonProperty("bid_ask_spread")
    private double bidAskSpread;

    @JsonProperty("funding_rate")
    private double fundingRate;

    @JsonProperty("open_interest_usd")
    private double openInterestUsd;

    @JsonProperty("h24_volume")
    private double h24Volume;

    @JsonProperty("converted_volume")
    private Map<String, String> convertedVolume;

    @JsonProperty("converted_last")
    private Map<String, String> convertedLast;

    @JsonProperty("last_traded")
    private long lastTraded;

    @JsonProperty("expired_at")
    private long expiredAt;
}
