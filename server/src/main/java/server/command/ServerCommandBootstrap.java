package server.command;

import server.command.handlers.*;
import server.manager.CollectionManager;

/**
 * Собирает реестр всех серверных команд.
 */
public final class ServerCommandBootstrap {

    private ServerCommandBootstrap() {
    }

    public static ServerCommandRegistry createRegistry(CollectionManager collectionManager) {
        CollectionCommandContext ctx = new CollectionCommandContext(collectionManager);
        ServerCommandRegistry registry = new ServerCommandRegistry(ctx);
        ctx.setCommandRegistry(registry);

        registry.register(new HelpCommandHandler());
        registry.register(new InfoCommandHandler());
        registry.register(new ShowCommandHandler());
        registry.register(new InsertCommandHandler());
        registry.register(new RemoveCommandHandler());
        registry.register(new ClearCommandHandler());
        return registry;
    }
}
