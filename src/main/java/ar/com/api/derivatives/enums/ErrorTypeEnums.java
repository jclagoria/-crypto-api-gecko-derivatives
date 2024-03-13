package ar.com.api.derivatives.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorTypeEnums {

    API_CLIENT_ERROR("Api Client Error"),
    API_SERVER_ERROR("Api Server Error"),
    GECKO_CLIENT_ERROR("Gecko Client Error"),
    GECKO_SERVER_ERROR("Gecko Server Error");

    private String description;

}
