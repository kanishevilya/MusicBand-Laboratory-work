package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.FilterByAlbumsCountRequest;
import common.protocol.response.FilterByAlbumsCountResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.util.List;

public final class FilterByAlbumsCountCommandHandler extends AbstractServerCommandHandler<FilterByAlbumsCountRequest> {

    public FilterByAlbumsCountCommandHandler() {
        super(FilterByAlbumsCountRequest.class, "filter_by_albums_count <n>",
                "элементы, у которых albumsCount равен заданному (список отсортирован по имени, затем id)");
    }

    @Override
    public AbstractResponse handle(FilterByAlbumsCountRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        List<MusicBand> list = context.collectionManager().filterByAlbumsCount(request.getAlbumsCount());
        return new FilterByAlbumsCountResponse(rid, true, "Найдено: " + list.size(), list);
    }
}
