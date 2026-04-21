package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.InsertRequest;
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
        var cm = context.collectionManager();
        if (cm.containsKey(request.getKey())) {
            return new InsertResponse(rid, false, "Элемент с ключом " + request.getKey() + " уже существует.");
        }
        MusicBand band = request.getBand();
        band.setId(cm.generateId());
        band.setCreationDate(ZonedDateTime.now());
        cm.insert(request.getKey(), band);
        return new InsertResponse(rid, true, "Элемент добавлен с ключом " + request.getKey() + ".");
    }
}
