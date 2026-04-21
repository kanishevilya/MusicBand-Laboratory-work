package client.command.impl;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.ShowRequest;

public final class ShowClientCommand extends AbstractRemoteClientCommand {

    public ShowClientCommand() {
        super("show");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new ShowRequest(context.nextId());
    }
}
