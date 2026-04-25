package client.command;

public final class ClientCommandArgs {

    private ClientCommandArgs() {
    }

    public static long parseLong(String s, String label) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный " + label + ": " + s);
        }
    }
}
