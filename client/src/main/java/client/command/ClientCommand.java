package client.command;

import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Клиентская команда (паттерн Command): имя в консоли и выполнение.
 */
public interface ClientCommand {

    String name();

    void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException;
}
