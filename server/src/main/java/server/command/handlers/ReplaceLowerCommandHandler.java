package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ReplaceLowerRequest;
import common.protocol.response.ReplaceLowerResponse;
import server.manager.CollectionManager;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class ReplaceLowerCommandHandler extends AbstractServerCommandHandler<ReplaceLowerRequest> {

    public ReplaceLowerCommandHandler() {
        super(ReplaceLowerRequest.class, "replace_if_lower <key>",
                "заменить по ключу, если новый элемент 'меньше' старого (по названию)");
    }

    @Override
    public AbstractResponse handle(ReplaceLowerRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        CollectionManager cm = context.collectionManager();
        if (!cm.containsKey(request.getKey())) {
            return new ReplaceLowerResponse(rid, false, "Элемент с ключом " + request.getKey() + " не найден.");
        }
        boolean replaceIfLower = cm.replaceIfLower(request.getKey(), request.getBand(), true);
        if (replaceIfLower) {
            return new ReplaceLowerResponse(rid, true, "Элемент заменён (новое значение меньше старого).");
        }
        return new ReplaceLowerResponse(rid, true, "Замена не выполнена: новое значение не меньше старого.");
    }
}
