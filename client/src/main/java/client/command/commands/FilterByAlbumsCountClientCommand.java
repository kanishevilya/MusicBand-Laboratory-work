package client.command.commands;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.request.FilterByAlbumsCountRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class FilterByAlbumsCountClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "filter_by_albums_count";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: filter_by_albums_count <число>");
            return;
        }
        int n = (int) ClientCommandArgs.parseLong(args[1], "albumsCount");
        context.sendAndPrint(new FilterByAlbumsCountRequest(context.nextId(), n));
    }
}
