package ar.com.api.derivatives.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exchange implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

}
