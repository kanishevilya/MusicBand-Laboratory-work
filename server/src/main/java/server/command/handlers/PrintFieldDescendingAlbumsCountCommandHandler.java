package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.PrintFieldDescendingAlbumsCountRequest;
import common.protocol.response.PrintFieldDescendingAlbumsCountResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;

import java.util.List;

public final class PrintFieldDescendingAlbumsCountCommandHandler
        extends AbstractServerCommandHandler<PrintFieldDescendingAlbumsCountRequest> {

    public PrintFieldDescendingAlbumsCountCommandHandler() {
        super(PrintFieldDescendingAlbumsCountRequest.class, "print_field_descending_albums_count",
                "значения поля albumsCount всех элементов в порядке убывания");
    }

    @Override
    public AbstractResponse handle(PrintFieldDescendingAlbumsCountRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        List<Integer> counts = context.collectionManager().getAlbumsCountDescending();
        return new PrintFieldDescendingAlbumsCountResponse(rid, true, "Значения albumsCount (по убыванию):", counts);
    }
}
