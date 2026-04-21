package client.command.impl;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.request.RemoveRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class RemoveClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: remove <ключ>");
            return;
        }
        long key = ClientCommandArgs.parseLong(args[1], "ключ");
        context.sendAndPrint(new RemoveRequest(context.nextId(), key));
    }
}
