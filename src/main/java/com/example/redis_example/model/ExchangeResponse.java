package com.example.redis_example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeResponse {

    private Double ask;

    private Double bid;

    private Long timestamp;

    @JsonProperty(value = "display_symbol")
    private String displaySymbol;
}
