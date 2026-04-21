package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByIdRequest;
import common.protocol.response.GetByIdResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class GetByIdCommandHandler extends AbstractServerCommandHandler<GetByIdRequest> {

    public GetByIdCommandHandler() {
        super(GetByIdRequest.class, "get_by_id", "служебный запрос: элемент по id");
    }

    @Override
    public boolean includeInHelp() {
        return false;
    }

    @Override
    public AbstractResponse handle(GetByIdRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        MusicBand b = context.collectionManager().getById(request.getBandId());
        if (b == null) {
            return new GetByIdResponse(rid, false, "Элемент с id " + request.getBandId() + " не найден.", null);
        }
        return new GetByIdResponse(rid, true, "OK", b);
    }
}
