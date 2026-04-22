package client.command;

import client.command.impl.*;
import client.network.UdpClient;
import client.util.InputHandler;
import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Разбор строки консоли и вызов зарегистрированной {@link ClientCommand}.
 */
public final class ClientLineDispatcher implements LineDispatchHandler {

    private final ClientCommandContext context;
    private final ClientCommandRegistry registry = new ClientCommandRegistry();

    public ClientLineDispatcher(UdpClient client, InputHandler inputHandler, AtomicLong requestIds) {
        this.context = new ClientCommandContext(client, inputHandler, requestIds);
        registerCommands();
    }

    private void registerCommands() {
        registry.register(new HelpClientCommand());
        registry.register(new InfoClientCommand());
        registry.register(new ShowClientCommand());
        registry.register(new ClearClientCommand());
        registry.register(new InsertClientCommand());
        registry.register(new RemoveClientCommand());
        registry.register(new UpdateClientCommand());
        registry.register(new RemoveGreaterClientCommand());
        registry.register(new ReplaceGreaterClientCommand());
        registry.register(new ReplaceLowerClientCommand());
        registry.register(new AverageOfAlbumsCountClientCommand());
        registry.register(new FilterByAlbumsCountClientCommand());
        registry.register(new PrintFieldDescendingAlbumsCountClientCommand());
        registry.register(new ExecuteScriptClientCommand());
        context.setLineDispatcher(this);
    }

    public ClientCommandContext getContext() {
        return context;
    }

    public void setInputHandler(InputHandler inputHandler) {
        context.setInputHandler(inputHandler);
    }

    public void handleLine(String line)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        dispatchLine(line);
    }

    @Override
    public void dispatch(String line)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        dispatchLine(line);
    }

    private void dispatchLine(String line)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (line == null || line.trim().isEmpty()) {
            return;
        }
        String[] parts = line.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();
        try {
            ClientCommand command = registry.get(cmd);
            if (command == null) {
                System.out.println("Неизвестная команда: '" + cmd + "'. Введите help.");
                return;
            }
            command.execute(parts, context);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
