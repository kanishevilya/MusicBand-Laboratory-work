package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ClearRequest;
import common.protocol.response.ClearResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ClearCommandHandler extends AbstractServerCommandHandler<ClearRequest> {

    public ClearCommandHandler() {
        super(ClearRequest.class, "clear", "очистить коллекцию (только свои элементы)");
    }

    @Override
    public AbstractResponse handle(ClearRequest request, CollectionCommandContext context) {
        boolean dbSuccess = context.databaseManager().clearUserBands(request.getLogin());

        if (dbSuccess) {
            List<Long> keysToRemove = context.collectionManager().getCollection().entrySet().stream()
                    .filter(entry -> entry.getValue().getOwnerLogin().equals(request.getLogin()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            keysToRemove.forEach(key -> context.collectionManager().removeByKey(key));

            return new ClearResponse(request.getRequestId(), true, "Ваши элементы успешно удалены. Очищено: " + keysToRemove.size());
        }

        return new ClearResponse(request.getRequestId(), false, "Ошибка базы данных при очистке коллекции.");
    }
}