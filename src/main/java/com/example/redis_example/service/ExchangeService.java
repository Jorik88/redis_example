package com.example.redis_example.service;

import com.example.redis_example.configuration.ExchangeConfiguration;
import com.example.redis_example.model.ExchangeRate;
import com.example.redis_example.model.ExchangeResponse;
import com.example.redis_example.utils.ExchangeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.DoubleStream;

@CacheConfig(cacheManager = "secondManager")
@Service
@Slf4j
public class ExchangeService implements IExchangeService{

    private static final String EXCHANGE_REQUEST_URL = "https://apiv2.bitcoinaverage.com/indices/local/ticker/%s";
    private static final String AUTH_SIGNATURE = "X-signature";

    private final RestTemplate restTemplate;
    private final ExchangeConfiguration exchangeConfiguration;
    private ConcurrentMap<String, ExchangeRate> localStorage = new ConcurrentHashMap<>();

    @Autowired
    public ExchangeService(RestTemplate restTemplate, ExchangeConfiguration exchangeConfiguration) {
        this.restTemplate = restTemplate;
        this.exchangeConfiguration = exchangeConfiguration;
    }

    @Cacheable(value = "exchangeRate")
    @Override
    public ExchangeRate getExchangeRate(String currencyPair) {

        try {
            log.info("Try get exchange rate for currencyPair={} in service", currencyPair);
            return uploadExchangeRate(currencyPair);
        } catch (Exception e) {
            log.info("Try get exchange rate for currencyPair={} in service from localStorage", currencyPair);
            return getExchangeRateFromLocalStorage(currencyPair);
        }
    }

    private ExchangeRate getExchangeRateFromLocalStorage(String currencyPair) {
        ExchangeRate exchangeRate = localStorage.get(currencyPair);
        if (exchangeRate != null) {
            return exchangeRate;
        }
        throw new IllegalStateException("Can't load exchange rate");
    }

    private ExchangeRate uploadExchangeRate(String currencyPair) throws InvalidKeyException, NoSuchAlgorithmException {
        String requestUrl = String.format(EXCHANGE_REQUEST_URL, currencyPair);

        HttpEntity httpEntity = getHttpEntity(ExchangeUtils.createAuthSignature(exchangeConfiguration.getPublicKey(), exchangeConfiguration.getPrivateKey()));
        ResponseEntity<ExchangeResponse> responseEntity =
                restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<ExchangeResponse>() {
                });

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null
                && responseEntity.getBody().getAsk() != null && responseEntity.getBody().getBid() != null) {
            ExchangeRate exchangeRate = convert(responseEntity.getBody(), currencyPair);
            localStorage.put(currencyPair, exchangeRate);
            return exchangeRate;
        }

        throw new IllegalStateException("Can't load exchange rate");
    }

    private ExchangeRate convert(ExchangeResponse response, String currencyPair) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyPair(currencyPair);
        DoubleStream average = DoubleStream.of(response.getAsk(), response.getBid());
        exchangeRate.setPrice(new BigDecimal(average.average().getAsDouble()));
        exchangeRate.setCreatedDate(LocalDateTime.now());
        return exchangeRate;
    }

    private HttpEntity getHttpEntity(String authSignature) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_SIGNATURE, authSignature);
        return new HttpEntity<>(headers);
    }
}
