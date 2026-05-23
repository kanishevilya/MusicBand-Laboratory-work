package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.RemoveGreaterRequest;
import common.protocol.response.RemoveGreaterResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RemoveGreaterCommandHandler extends AbstractServerCommandHandler<RemoveGreaterRequest> {

    public RemoveGreaterCommandHandler() {
        super(RemoveGreaterRequest.class, "remove_greater",
                "удалить все элементы 'больше' эталона (только свои)");
    }

    @Override
    public AbstractResponse handle(RemoveGreaterRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();

        List<Map.Entry<Long, MusicBand>> targets = context.collectionManager().getCollection().entrySet().stream()
                .filter(entry -> entry.getValue().getOwnerLogin().equals(request.getLogin()))
                .filter(entry -> entry.getValue().compareTo(request.getReferenceBand()) > 0)
                .toList();

        if (targets.isEmpty()) {
            return new RemoveGreaterResponse(rid, true, "Нет ваших элементов, превышающих заданный.");
        }

        List<Long> dbIdsToRemove = targets.stream()
                .map(entry -> entry.getValue().getId())
                .collect(Collectors.toList());

        boolean dbSuccess = context.databaseManager().deleteMusicBands(dbIdsToRemove, request.getLogin());

        if (dbSuccess) {
            targets.forEach(entry -> context.collectionManager().removeByKey(entry.getKey()));
            return new RemoveGreaterResponse(rid, true, "Успешно удалено ваших элементов: " + targets.size());
        }

        return new RemoveGreaterResponse(rid, false, "Ошибка базы данных при массовом удалении.");
    }
}