package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.ReplaceGreaterRequest;
import common.protocol.response.ReplaceGreaterResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class ReplaceGreaterCommandHandler extends AbstractServerCommandHandler<ReplaceGreaterRequest> {

    public ReplaceGreaterCommandHandler() {
        super(ReplaceGreaterRequest.class, "replace_if_greater <key>",
                "заменить по ключу, если новый элемент 'больше' старого (по названию)");
    }

    @Override
    public AbstractResponse handle(ReplaceGreaterRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();

        MusicBand existingBand = context.collectionManager().getByKey(request.getKey());
        if (existingBand == null) {
            return new ReplaceGreaterResponse(rid, false, "Элемент с ключом " + request.getKey() + " не найден.");
        }

        if (request.getBand().compareTo(existingBand) > 0) {

            boolean dbSuccess = context.databaseManager().updateMusicBand(
                    existingBand.getId(), request.getBand(), request.getLogin()
            );

            if (dbSuccess) {
                request.getBand().setId(existingBand.getId());
                request.getBand().setCreationDate(existingBand.getCreationDate());
                request.getBand().setOwnerLogin(request.getLogin());

                context.collectionManager().insert(request.getKey(), request.getBand());
                return new ReplaceGreaterResponse(rid, true, "Элемент успешно заменён (новое значение больше старого).");
            } else {
                return new ReplaceGreaterResponse(rid, false, "Ошибка: У вас нет прав на изменение этого элемента.");
            }
        }

        return new ReplaceGreaterResponse(rid, true, "Замена не выполнена: новое значение не превышает старое.");
    }
}