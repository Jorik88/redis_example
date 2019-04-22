package com.example.redis_example.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.exchange.config")
public class ExchangeConfiguration {

    private String privateKey;
    private String publicKey;
}
