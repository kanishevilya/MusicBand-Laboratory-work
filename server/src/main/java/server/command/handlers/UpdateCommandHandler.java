package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.UpdateRequest;
import common.protocol.response.UpdateResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class UpdateCommandHandler extends AbstractServerCommandHandler<UpdateRequest> {

    public UpdateCommandHandler() {
        super(UpdateRequest.class, "update <id>", "обновить элемент коллекции по id элемента (MusicBand)");
    }

    @Override
    public AbstractResponse handle(UpdateRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        if (!context.collectionManager().updateById(request.getId(), request.getBand())) {
            return new UpdateResponse(rid, false, "Элемент с id " + request.getId() + " не найден.");
        }
        return new UpdateResponse(rid, true, "Элемент с id " + request.getId() + " обновлён.");
    }
}
