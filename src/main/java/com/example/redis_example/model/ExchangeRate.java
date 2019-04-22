package com.example.redis_example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate implements Serializable {

    private String currencyPair;
    private BigDecimal price;
    private LocalDateTime createdDate;
}
