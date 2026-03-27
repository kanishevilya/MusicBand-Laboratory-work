package util;

import model.Currency;

import java.math.BigDecimal;

public interface RateProvider {
    /**
     * @return курс 1 unit валюты {@code from} в RUB.
     */
    BigDecimal rateToRub(Currency from) throws Exception;
}

