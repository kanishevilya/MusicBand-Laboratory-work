package client.command;

import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.AbstractRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Команда, которая только отправляет один запрос на сервер без дополнительного ввода.
 */
public abstract class AbstractRemoteClientCommand implements ClientCommand {

    private final String name;

    protected AbstractRemoteClientCommand(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public String name() {
        return name;
    }

    protected abstract AbstractRequest createRequest(ClientCommandContext context);

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        context.sendAndPrint(createRequest(context));
    }
}
