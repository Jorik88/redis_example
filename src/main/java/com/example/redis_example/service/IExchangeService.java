package com.example.redis_example.service;

import com.example.redis_example.model.ExchangeRate;

public interface IExchangeService {

    ExchangeRate getExchangeRate(String currencyPair);
}
