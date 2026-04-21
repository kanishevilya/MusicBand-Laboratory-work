package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.InfoRequest;
import common.protocol.response.InfoResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;
import server.manager.CollectionManager;

public final class InfoCommandHandler extends AbstractServerCommandHandler<InfoRequest> {

    public InfoCommandHandler() {
        super(InfoRequest.class, "info", "вывести информацию о коллекции");
    }

    @Override
    public AbstractResponse handle(InfoRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        var cm = context.collectionManager();
        String message = "Информация о коллекции:" + System.lineSeparator()
                + "  Тип коллекции   : java.util.TreeMap" + System.lineSeparator()
                + "  Тип элементов   : MusicBand" + System.lineSeparator()
                + "  Дата инициализации: "
                + cm.getInitializationDate().format(CollectionManager.DATE_FORMATTER)
                + System.lineSeparator()
                + "  Количество элементов: " + cm.size();
        return new InfoResponse(rid, true, message);
    }
}
