package ar.com.api.derivatives.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Derivative {
 
 @JsonProperty("market")
 private String market;

 @JsonProperty("symbol")
 private String symbol;

 @JsonProperty("index_id")
 private String indexId;

 @JsonProperty("price")
 private String price;

 @JsonProperty("price_percentage_change_24h")
 private double pricePercentageChange24h;

 @JsonProperty("contract_type")
 private String contractType;

 @JsonProperty("index")
 private double index;

 @JsonProperty("basis")
 private double basis;

 @JsonProperty("spread")
 private double spread;

 @JsonProperty("funding_rate")
 private double fundingRate;

 @JsonProperty("open_interest")
 private double openInterest;

 @JsonProperty("volume_24h")
 private double volume24h;

 @JsonProperty("last_traded_at")
 private long lastTradedAt;

 @JsonProperty("expired_at")
 private long expiredAt;

}