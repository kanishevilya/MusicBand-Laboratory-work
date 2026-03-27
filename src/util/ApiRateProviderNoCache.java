package util;

import model.Currency;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class ApiRateProviderNoCache implements RateProvider {
    private final ExchangeRateClient client;
    private final AtomicLong apiCalls = new AtomicLong();

    public ApiRateProviderNoCache(ExchangeRateClient client) {
        this.client = client;
    }

    @Override
    public BigDecimal rateToRub(Currency from) throws Exception {
        apiCalls.incrementAndGet();
        return client.fetchRateToRub(from);
    }

    public long getApiCalls() {
        return apiCalls.get();
    }
}

