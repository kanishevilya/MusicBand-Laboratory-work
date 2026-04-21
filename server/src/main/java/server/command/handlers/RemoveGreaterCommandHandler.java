package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.RemoveGreaterRequest;
import common.protocol.response.RemoveGreaterResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class RemoveGreaterCommandHandler extends AbstractServerCommandHandler<RemoveGreaterRequest> {

    public RemoveGreaterCommandHandler() {
        super(RemoveGreaterRequest.class, "remove_greater",
                "удалить все элементы 'больше' эталона (сравнение по названию)");
    }

    @Override
    public AbstractResponse handle(RemoveGreaterRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        int n = context.collectionManager().removeGreater(request.getReferenceBand());
        return new RemoveGreaterResponse(rid, true, "Удалено элементов: " + n);
    }
}
