package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.RemoveRequest;
import common.protocol.response.RemoveResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class RemoveCommandHandler extends AbstractServerCommandHandler<RemoveRequest> {

    public RemoveCommandHandler() {
        super(RemoveRequest.class, "remove <key>", "удалить элемент из коллекции по ключу");
    }

    @Override
    public AbstractResponse handle(RemoveRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        if (context.collectionManager().removeByKey(request.getKey())) {
            return new RemoveResponse(rid, true, "Элемент с ключом " + request.getKey() + " удалён.");
        }
        return new RemoveResponse(rid, false, "Элемент с ключом " + request.getKey() + " не найден.");
    }
}
