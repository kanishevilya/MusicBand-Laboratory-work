package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.RemoveRequest;
import common.protocol.response.RemoveResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class RemoveCommandHandler extends AbstractServerCommandHandler<RemoveRequest> {

    public RemoveCommandHandler() {
        super(RemoveRequest.class, "remove <key>", "удалить элемент по ключу");
    }

    @Override
    public AbstractResponse handle(RemoveRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();

        MusicBand band = context.collectionManager().getByKey(request.getKey());
        if (band == null) {
            return new RemoveResponse(rid, false, "Элемент с таким ключом не найден.");
        }

        if (context.databaseManager().deleteMusicBand(band.getId(), request.getLogin())) {
            context.collectionManager().removeByKey(request.getKey());
            return new RemoveResponse(rid, true, "Элемент с ключом " + request.getKey() + " удалён.");
        }

        return new RemoveResponse(rid, false, "Ошибка: У вас нет прав на удаление этого элемента.");
    }
}