package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.UpdateRequest;
import common.protocol.response.UpdateResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class UpdateCommandHandler extends AbstractServerCommandHandler<UpdateRequest> {

    public UpdateCommandHandler() {
        super(UpdateRequest.class, "update <id>", "обновить элемент коллекции по id");
    }

    @Override
    public AbstractResponse handle(UpdateRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();

        boolean dbSuccess = context.databaseManager().updateMusicBand(
                request.getId(), request.getBand(), request.getLogin()
        );

        if (dbSuccess) {
            context.collectionManager().updateById(request.getId(), request.getBand());
            return new UpdateResponse(rid, true, "Элемент с id " + request.getId() + " успешно обновлён.");
        }
        return new UpdateResponse(rid, false, "Ошибка: Элемент не найден или у вас нет прав на его изменение.");
    }
}