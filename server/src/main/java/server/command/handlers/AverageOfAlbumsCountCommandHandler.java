package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.AverageOfAlbumsCountRequest;
import common.protocol.response.AverageOfAlbumsCountResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

public final class AverageOfAlbumsCountCommandHandler
        extends AbstractServerCommandHandler<AverageOfAlbumsCountRequest> {

    public AverageOfAlbumsCountCommandHandler() {
        super(AverageOfAlbumsCountRequest.class, "average_of_albums_count",
                "среднее значение поля albumsCount по коллекции");
    }

    @Override
    public AbstractResponse handle(AverageOfAlbumsCountRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        double avg = context.collectionManager().averageOfAlbumsCount();
        return new AverageOfAlbumsCountResponse(rid, true, "Результат по коллекции:", avg);
    }
}
