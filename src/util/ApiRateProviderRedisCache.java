package util;

import model.Currency;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiRateProviderRedisCache implements RateProvider {
    private static final Logger LOGGER = Logger.getLogger(ApiRateProviderRedisCache.class.getName());

    private final ExchangeRateClient client;
    private final RedisClient redis;
    private final int ttlSeconds;
    private volatile boolean redisAvailable;

    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong apiCalls = new AtomicLong();

    public ApiRateProviderRedisCache(ExchangeRateClient client, String host, int port, Duration timeout,
            int ttlSeconds) {
        this.client = client;
        int timeoutMs = (int) Math.min(Integer.MAX_VALUE, Math.max(1, timeout.toMillis()));
        this.redis = RedisClient.builder()
                .hostAndPort(new HostAndPort(host, port))
                .clientConfig(DefaultJedisClientConfig.builder()
                        .socketTimeoutMillis(timeoutMs)
                        .connectionTimeoutMillis(timeoutMs)
                        .build())
                .build();
        this.ttlSeconds = ttlSeconds;
        this.redisAvailable = true;

        try {
            String pong = redis.ping();
            if (!"PONG".equalsIgnoreCase(pong)) {
                redisAvailable = false;
                LOGGER.warning("Redis ping returned unexpected response: " + pong);
            }
        } catch (Exception e) {
            redisAvailable = false;
            LOGGER.log(Level.WARNING, "Redis is unavailable on startup. Cache is disabled, API fallback will be used.",
                    e);
        }
    }

    @Override
    public BigDecimal rateToRub(Currency from) throws Exception {
        if (from == Currency.RUB) {
            return BigDecimal.ONE;
        }

        String key = "rate:" + from.name() + ":RUB";

        String cached = null;
        if (redisAvailable) {
            try {
                cached = redis.get(key);
            } catch (Exception e) {
                redisAvailable = false;
                LOGGER.log(Level.WARNING, "Redis GET failed. Disabling cache and falling back to API.", e);
            }
        }

        if (cached != null) {
            cacheHits.incrementAndGet();
            return new BigDecimal(cached);
        }

        cacheMisses.incrementAndGet();
        apiCalls.incrementAndGet();
        BigDecimal rate = client.fetchRateToRub(from);

        if (redisAvailable) {
            try {
                redis.set(key, rate.toPlainString(), SetParams.setParams().ex((long) ttlSeconds));
            } catch (Exception e) {
                redisAvailable = false;
                LOGGER.log(Level.WARNING, "Redis SETEX failed. Disabling cache and continuing with API.", e);
            }
        }

        return rate;
    }

    public long getCacheHits() {
        return cacheHits.get();
    }

    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public long getApiCalls() {
        return apiCalls.get();
    }

    public boolean isRedisAvailable() {
        return redisAvailable;
    }

    public void close() {
        try {
            redis.close();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to close Redis client.", e);
        }
    }
}
