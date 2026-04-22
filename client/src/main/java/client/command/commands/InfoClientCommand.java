package client.command.commands;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.InfoRequest;

public final class InfoClientCommand extends AbstractRemoteClientCommand {

    public InfoClientCommand() {
        super("info");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new InfoRequest(context.nextId());
    }
}
