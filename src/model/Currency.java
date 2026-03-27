package model;

/**
 * Поддерживаемые валюты.
 */
public enum Currency {
    RUB,
    USD,
    EUR,
    CNY,
    GBP;

    public static String valuesString() {
        StringBuilder sb = new StringBuilder();
        for (Currency c : values()) {
            sb.append(" (").append(c.ordinal() + 1).append(")").append(c.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public static Currency fromOrdinal(int ordinal) {
        Currency[] all = Currency.values();
        if (ordinal >= 0 && ordinal < all.length) {
            return all[ordinal];
        }
        throw new IllegalArgumentException("Неверный индекс: " + ordinal);
    }
}

