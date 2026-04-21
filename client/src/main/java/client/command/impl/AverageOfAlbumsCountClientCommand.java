package client.command.impl;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.AverageOfAlbumsCountRequest;

public final class AverageOfAlbumsCountClientCommand extends AbstractRemoteClientCommand {

    public AverageOfAlbumsCountClientCommand() {
        super("average_of_albums_count");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new AverageOfAlbumsCountRequest(context.nextId());
    }
}
