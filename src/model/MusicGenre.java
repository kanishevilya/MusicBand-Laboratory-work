package model;

public enum MusicGenre {
    RAP,
    HIP_HOP,
    PSYCHEDELIC_CLOUD_RAP,
    SOUL,
    POST_ROCK;

    public static String valuesString() {
        StringBuilder sb = new StringBuilder();
        for (MusicGenre g : values()) {
            sb.append(g.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
