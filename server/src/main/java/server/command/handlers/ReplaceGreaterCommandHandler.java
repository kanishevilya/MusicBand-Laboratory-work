package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ReplaceGreaterRequest;
import common.protocol.response.ReplaceGreaterResponse;
import server.manager.CollectionManager;
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
        CollectionManager cm = context.collectionManager();
        if (!cm.containsKey(request.getKey())) {
            return new ReplaceGreaterResponse(rid, false, "Элемент с ключом " + request.getKey() + " не найден.");
        }
        boolean replaceIfGreater = cm.replaceIfGreater(request.getKey(), request.getBand(), true);
        if (replaceIfGreater) {
            return new ReplaceGreaterResponse(rid, true, "Элемент заменён (новое значение больше старого).");
        }
        return new ReplaceGreaterResponse(rid, true, "Замена не выполнена: новое значение не превышает старое.");
    }
}
