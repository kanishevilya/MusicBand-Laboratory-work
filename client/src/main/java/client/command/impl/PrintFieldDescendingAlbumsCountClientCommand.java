package client.command.impl;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.PrintFieldDescendingAlbumsCountRequest;

public final class PrintFieldDescendingAlbumsCountClientCommand extends AbstractRemoteClientCommand {

    public PrintFieldDescendingAlbumsCountClientCommand() {
        super("print_field_descending_albums_count");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new PrintFieldDescendingAlbumsCountRequest(context.nextId());
    }
}
