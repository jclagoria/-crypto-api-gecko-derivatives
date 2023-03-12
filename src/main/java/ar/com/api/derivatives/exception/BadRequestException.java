package ar.com.api.derivatives.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadRequestException extends Exception {
 
 public BadRequestException(String message) {
  super(message);
 }

}
