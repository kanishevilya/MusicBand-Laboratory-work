package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByKeyRequest;
import common.protocol.response.GetByKeyResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class GetByKeyCommandHandler extends AbstractServerCommandHandler<GetByKeyRequest> {

    public GetByKeyCommandHandler() {
        super(GetByKeyRequest.class, "get_by_key", "служебный запрос: элемент по ключу");
    }

    @Override
    public boolean includeInHelp() {
        return false;
    }

    @Override
    public AbstractResponse handle(GetByKeyRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        MusicBand b = context.collectionManager().getByKey(request.getKey());
        if (b == null) {
            return new GetByKeyResponse(rid, false, "Элемент с ключом " + request.getKey() + " не найден.", null);
        }
        return new GetByKeyResponse(rid, true, "OK", b);
    }
}
