package server.command;

import server.command.handlers.*;
import server.manager.CollectionManager;

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
        registry.register(new GetByIdCommandHandler());
        registry.register(new GetByKeyCommandHandler());
        registry.register(new UpdateCommandHandler());
        registry.register(new RemoveGreaterCommandHandler());
        registry.register(new ReplaceGreaterCommandHandler());
        registry.register(new ReplaceLowerCommandHandler());
        registry.register(new AverageOfAlbumsCountCommandHandler());
        registry.register(new FilterByAlbumsCountCommandHandler());
        registry.register(new PrintFieldDescendingAlbumsCountCommandHandler());
        registry.register(new ScriptSessionBeginCommandHandler());
        registry.register(new ScriptSessionEndCommandHandler());
        return registry;
    }
}
