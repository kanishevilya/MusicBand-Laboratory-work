package util;

import model.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Получает курсы валют через стороннее API.
 *
 * Используется открытый endpoint без ключа: https://open.er-api.com/v6/latest/<BASE>
 * Пример: https://open.er-api.com/v6/latest/USD
 */
public class ExchangeRateClient {

    private static final String BASE_URL = "https://open.er-api.com/v6/latest/";

    private final HttpClient httpClient;

    public ExchangeRateClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public BigDecimal fetchRateToRub(Currency from) throws IOException, InterruptedException {
        if (from == Currency.RUB) {
            return BigDecimal.ONE;
        }

        String url = BASE_URL + from.name();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " от сервиса курсов валют");
        }

        BigDecimal rub = extractRate(response.body(), "RUB");
        if (rub == null) {
            throw new IOException("Не удалось извлечь курс RUB из ответа сервиса");
        }
        return rub;
    }

    private BigDecimal extractRate(String json, String code) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(code) + "\"\\s*:\\s*([-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return null;
        }
        return new BigDecimal(m.group(1));
    }
}

