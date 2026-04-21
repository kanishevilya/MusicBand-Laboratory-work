package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ClearRequest;
import common.protocol.response.ClearResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class ClearCommandHandler extends AbstractServerCommandHandler<ClearRequest> {

    public ClearCommandHandler() {
        super(ClearRequest.class, "clear", "очистить коллекцию");
    }

    @Override
    public AbstractResponse handle(ClearRequest request, CollectionCommandContext context) {
        context.collectionManager().clear();
        return new ClearResponse(request.getRequestId(), true, "Коллекция очищена.");
    }
}
