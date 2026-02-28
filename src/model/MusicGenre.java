package model;

/**
 * Перечисление, представляющее жанры музыки
 */
public enum MusicGenre {
    RAP,
    HIP_HOP,
    PSYCHEDELIC_CLOUD_RAP,
    SOUL,
    POST_ROCK;

    public static String valuesString() {
        StringBuilder sb = new StringBuilder();
        for (MusicGenre g : values()) {
            sb.append(" (").append(g.ordinal()+1).append(")").append(g.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    public static MusicGenre fromOrdinal(int ordinal) {
        MusicGenre[] genres = MusicGenre.values();
        if (ordinal >= 0 && ordinal < genres.length) {
            return genres[ordinal];
        }
        throw new IllegalArgumentException("Неверный индекс: " + ordinal);
    }
}
