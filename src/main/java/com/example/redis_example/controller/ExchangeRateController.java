package com.example.redis_example.controller;

import com.example.redis_example.model.ExchangeRate;
import com.example.redis_example.service.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExchangeRateController {

    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeRateController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/exchange/{currencyPair}")
    public ExchangeRate getExchangeRate(@PathVariable String currencyPair) {
        log.info("Try get exchange rate for currencyPair={}", currencyPair);
        return exchangeService.getExchangeRate(currencyPair);
    }
}
