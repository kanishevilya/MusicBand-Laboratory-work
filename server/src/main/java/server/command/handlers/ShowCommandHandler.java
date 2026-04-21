package server.command.handlers;

import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.ShowRequest;
import common.protocol.response.ShowResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.util.Map;
import java.util.TreeMap;

public final class ShowCommandHandler extends AbstractServerCommandHandler<ShowRequest> {

    public ShowCommandHandler() {
        super(ShowRequest.class, "show", "вывести все элементы коллекции");
    }

    @Override
    public AbstractResponse handle(ShowRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        TreeMap<Long, MusicBand> bands = context.collectionManager().bandsSortedForClient()
                .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
        return new ShowResponse(rid, true, "Элементов: " + bands.size(), bands);
    }
}
