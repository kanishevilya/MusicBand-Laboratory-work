package client.command.commands;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.ClearRequest;

public final class ClearClientCommand extends AbstractRemoteClientCommand {

    public ClearClientCommand() {
        super("clear");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new ClearRequest(context.nextId());
    }
}
