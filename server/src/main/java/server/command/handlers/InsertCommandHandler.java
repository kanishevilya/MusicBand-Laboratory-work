package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.InsertRequest;
import common.protocol.response.ErrorResponse;
import common.protocol.response.InsertResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.time.ZonedDateTime;

public final class InsertCommandHandler extends AbstractServerCommandHandler<InsertRequest> {

    public InsertCommandHandler() {
        super(InsertRequest.class, "insert <key>", "добавить новый элемент с заданным ключом");
    }

    @Override
    public AbstractResponse handle(InsertRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        MusicBand band = request.getBand();
        String login = request.getLogin();

        long generatedId = context.databaseManager().insertMusicBand(band, login);

        if (generatedId != -1) {
            band.setId(generatedId);
            band.setOwnerLogin(login);

            context.collectionManager().insert(request.getKey(), band);

            return new InsertResponse(rid, true, "Группа успешно добавлена с ID " + generatedId);
        } else {
            return new ErrorResponse(rid, "Ошибка БД: Не удалось добавить группу.");
        }
    }
}
