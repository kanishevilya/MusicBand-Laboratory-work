package command;

import manager.CollectionManager;
import model.Coordinates;
import model.Currency;
import model.MusicBand;
import util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Команда benchmark_rates для сравнения производительности пересчёта курсов.
 *
 * Использование:
 * - benchmark_rates [count]
 *
 * Замеряет:
 * - NoCache: каждый пересчёт делает запрос к API
 * - RedisCache: курсы кэшируются в Redis (SETEX)
 */
public class BenchmarkRatesCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(BenchmarkRatesCommand.class.getName());

    private final CollectionManager collectionManager;

    public BenchmarkRatesCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "benchmark_rates";
    }

    @Override
    public String getDescription() {
        return "сравнить производительность курсов (без кэша vs Redis), по умолчанию 10000 объектов";
    }

    @Override
    public void execute(String[] args) {
        int count = 10_000;
        if (args.length >= 2) {
            try {
                count = Integer.parseInt(args[1]);
                if (count <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: count должен быть положительным целым числом.");
                return;
            }
        }

        List<MusicBand> items = buildDataset(count);
        System.out.println("Dataset: " + items.size() + " объектов.");

        ExchangeRateClient client = new ExchangeRateClient();

        ApiRateProviderNoCache noCache = new ApiRateProviderNoCache(client);
        BenchmarkResult r1 = run(items, noCache);

        ApiRateProviderRedisCache redisCache = new ApiRateProviderRedisCache(client, "127.0.0.1", 6379, Duration.ofSeconds(2), 3600);
        BenchmarkResult r2 = null;
        if (redisCache.isRedisAvailable()) {
            r2 = run(items, redisCache);
        } else {
            LOGGER.warning("Redis benchmark step is skipped because Redis is unavailable.");
            System.out.println("Redis недоступен. Шаг RedisCache будет пропущен.");
        }
        redisCache.close();

        System.out.println();
        print("NoCache", r1, noCache.getApiCalls(), 0, 0);
        if (r2 != null) {
            print("RedisCache", r2, redisCache.getApiCalls(), redisCache.getCacheHits(), redisCache.getCacheMisses());
        } else {
            System.out.println("RedisCache: пропущено (Redis недоступен).");
        }
    }

    private static class BenchmarkResult {
        long nanos;
        int operations;
        BigDecimal checksum;
    }

    private BenchmarkResult run(List<MusicBand> items, RateProvider provider) {
        BenchmarkResult res = new BenchmarkResult();
        BigDecimal checksum = BigDecimal.ZERO;

        long start = System.nanoTime();
        for (MusicBand b : items) {
            try {
                BigDecimal rate = provider.rateToRub(b.getCurrency());
                BigDecimal rub = b.getPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP);
                checksum = checksum.add(rub);
            } catch (Exception e) {
//                System.out.println(b.getCurrency());
//                System.out.println(b.getPrice());
//                LOGGER.log(Level.WARNING, "Benchmark operation failed for one item.", e);
            }
        }
        long end = System.nanoTime();

        res.nanos = end - start;
        res.operations = items.size();
        res.checksum = checksum;
        return res;
    }

    private List<MusicBand> buildDataset(int count) {
        List<MusicBand> list = new ArrayList<>(count);
        Random rnd = new Random(42);
        Currency[] currencies = Currency.values();
        for (int i = 0; i < count; i++) {
            MusicBand b = new MusicBand();
            b.setId(i + 1L);
            b.setName("Band_" + i);
            b.setCoordinates(new Coordinates(rnd.nextDouble() * 1000, (float) (rnd.nextDouble() * 900)));
            b.setCreationDate(ZonedDateTime.now());
            b.setNumberOfParticipants(null);
            b.setAlbumsCount(1);
            b.setGenre(null);
            b.setFrontMan(null);
            b.setPrice(new BigDecimal(1 + rnd.nextInt(10_000)));
            b.setCurrency(currencies[rnd.nextInt(currencies.length)]);
            list.add(b);
        }
        return list;
    }

    private void print(String label, BenchmarkResult r, long apiCalls, long hits, long misses) {
        double ms = r.nanos / 1_000_000.0;
        double avgMs = ms / Math.max(1, r.operations);
        System.out.println(label + ":");
        System.out.printf("  total: %.2f ms%n", ms);
        System.out.printf("  avg/op: %.6f ms%n", avgMs);
        System.out.println("  apiCalls: " + apiCalls);
        if (hits + misses > 0) {
            System.out.println("  cacheHits: " + hits);
            System.out.println("  cacheMisses: " + misses);
        }
        System.out.println("  checksum: " + r.checksum);
    }
}

