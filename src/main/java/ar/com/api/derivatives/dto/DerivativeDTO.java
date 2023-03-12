package ar.com.api.derivatives.dto;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DerivativeDTO implements IFilterDTO {

 private Optional<String> includeTickers;

 @Override
 public String getUrlFilterString() {
  
  StringBuilder urlBuilder = new StringBuilder();

  if(includeTickers.isPresent()) {
   urlBuilder.append("?include_tickers=")
             .append(includeTickers.get());
  }   

  return urlBuilder.toString();
 }
 
}
