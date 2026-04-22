package server.command;

import server.manager.CollectionManager;

import java.net.SocketAddress;

/**
 * Контекст выполнения серверной команды: доступ к коллекции и связанным сервисам.
 */
public final class CollectionCommandContext {

    private final CollectionManager collectionManager;
    private ServerCommandRegistry commandRegistry;
    private volatile SocketAddress currentClient;

    public CollectionCommandContext(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public CollectionManager collectionManager() {
        return collectionManager;
    }

    public ServerCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    public void setCommandRegistry(ServerCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Адрес клиента текущего UDP-запроса (для стека скриптов и т.п.).
     */
    public void setCurrentClient(SocketAddress client) {
        this.currentClient = client;
    }

    public void clearCurrentClient() {
        this.currentClient = null;
    }

    public SocketAddress currentClient() {
        return currentClient;
    }
}
