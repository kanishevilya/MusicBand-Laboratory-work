package client.command;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Реестр клиентских команд по имени.
 */
public final class ClientCommandRegistry {

    private final Map<String, ClientCommand> commands = new LinkedHashMap<>();

    public void register(ClientCommand command) {
        commands.put(command.name().toLowerCase(), command);
    }

    public ClientCommand get(String commandName) {
        return commands.get(commandName.toLowerCase());
    }
}
