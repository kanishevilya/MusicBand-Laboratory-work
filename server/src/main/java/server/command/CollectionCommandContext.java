package server.command;

import server.database.DatabaseManager;
import server.manager.CollectionManager;

import java.net.SocketAddress;


public final class CollectionCommandContext {

    private final CollectionManager collectionManager;
    private ServerCommandRegistry commandRegistry;
    private volatile SocketAddress currentClient;
    private final DatabaseManager databaseManager;

    public CollectionCommandContext(CollectionManager collectionManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.databaseManager = databaseManager;
    }

    public DatabaseManager databaseManager() {
        return databaseManager;
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
