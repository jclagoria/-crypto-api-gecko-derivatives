package ar.com.api.derivatives.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DerivativeData implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonProperty("open_interest_btc")
    private double openInterestBtc;

    @JsonProperty("trade_volume_24h_btc")
    private String tradeVolume24hBtc;

    @JsonProperty("number_of_perpetual_pairs")
    private long numberOfPerpetualPairs;

    @JsonProperty("number_of_futures_pairs")
    private long numberOfFuturesPairs;

    @JsonProperty("image")
    private String image;

    @JsonProperty("year_established")
    private long yearEstablished;

    @JsonProperty("country")
    private String country;

    @JsonProperty("description")
    private String description;

    @JsonProperty("url")
    private String url;

    @JsonProperty("tickers")
    private List<Ticker> ticker;

}
