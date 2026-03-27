package command;

import manager.CollectionManager;
import model.Currency;
import model.MusicBand;
import util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Команда show_prices для вывода стоимости в исходной валюте и в RUB.
 *
 * Использование:
 * - show_prices nocache
 * - show_prices redis [ttlSeconds]
 */
public class ShowPricesCommand implements Command {
    private static final Logger LOGGER = Logger.getLogger(ShowPricesCommand.class.getName());

    private final CollectionManager collectionManager;

    public ShowPricesCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show_prices";
    }

    @Override
    public String getDescription() {
        return "вывести стоимость каждого объекта в валюте и в RUB (show_prices nocache|redis [ttlSeconds])";
    }

    @Override
    public void execute(String[] args) {
        if (collectionManager.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }

        String mode = args.length >= 2 ? args[1].toLowerCase() : "redis";
        int ttl = 3600;
        if (args.length >= 3) {
            try {
                ttl = Integer.parseInt(args[2]);
                if (ttl <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ttlSeconds должен быть положительным целым числом.");
                return;
            }
        }

        ExchangeRateClient client = new ExchangeRateClient();
        RateProvider provider;
        ApiRateProviderRedisCache redisProvider = null;

        if (mode.equals("nocache")) {
            provider = new ApiRateProviderNoCache(client);
        } else if (mode.equals("redis")) {
            redisProvider = new ApiRateProviderRedisCache(client, "127.0.0.1", 6379, Duration.ofSeconds(2), ttl);
            provider = redisProvider;
            if (!redisProvider.isRedisAvailable()) {
                LOGGER.warning("Redis is unavailable in show_prices; falling back to API requests.");
                System.out.println("Предупреждение: Redis недоступен, будет использован API без кэша.");
            }
        } else {
            System.out.println("Использование: show_prices nocache | show_prices redis [ttlSeconds]");
            return;
        }

        System.out.println("Стоимость объектов:");
        for (Map.Entry<Long, MusicBand> entry : collectionManager.getCollection().entrySet()) {
            MusicBand band = entry.getValue();
            BigDecimal price = band.getPrice();
            Currency currency = band.getCurrency();
            try {
                BigDecimal rate = provider.rateToRub(currency);
                BigDecimal rub = price.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                System.out.println("Ключ " + entry.getKey() + ":");
                System.out.println("  " + price + " " + currency);
                System.out.println("  " + rub + " RUB");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to calculate RUB value for collection key " + entry.getKey(), e);
                System.out.println("Ключ " + entry.getKey() + ": ошибка получения курса: " + e.getMessage());
            }
        }

        if (redisProvider != null) {
            redisProvider.close();
        }
    }
}

