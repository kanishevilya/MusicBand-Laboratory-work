package client.command.impl;

import client.command.AbstractRemoteClientCommand;
import client.command.ClientCommandContext;
import common.protocol.AbstractRequest;
import common.protocol.request.HelpRequest;

public final class HelpClientCommand extends AbstractRemoteClientCommand {

    public HelpClientCommand() {
        super("help");
    }

    @Override
    protected AbstractRequest createRequest(ClientCommandContext context) {
        return new HelpRequest(context.nextId());
    }
}
