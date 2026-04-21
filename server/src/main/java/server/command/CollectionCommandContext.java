package server.command;

import server.manager.CollectionManager;

/**
 * Контекст выполнения серверной команды: доступ к коллекции и связанным сервисам.
 */
public final class CollectionCommandContext {

    private final CollectionManager collectionManager;
    private ServerCommandRegistry commandRegistry;

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
}
