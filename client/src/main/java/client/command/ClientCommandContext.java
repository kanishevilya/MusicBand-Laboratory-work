package client.command;

import client.network.UdpClient;
import client.util.InputHandler;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Контекст выполнения клиентской команды: сеть, id запросов, ввод, скрипты.
 */
public final class ClientCommandContext {

    private final UdpClient client;
    private InputHandler inputHandler;
    private final AtomicLong requestIds;

    public ClientCommandContext(UdpClient client, InputHandler inputHandler, AtomicLong requestIds) {
        this.client = client;
        this.inputHandler = inputHandler;
        this.requestIds = requestIds;
    }

    public long nextId() {
        return requestIds.incrementAndGet();
    }

    public AbstractResponse send(AbstractRequest request) throws IOException, TimeoutException, ProtocolException, DeserializationException {
        return client.sendAndReceive(request);
    }

    public void sendAndPrint(AbstractRequest request) throws IOException, TimeoutException, ProtocolException, DeserializationException {
        System.out.println(send(request).getMessage());
    }


    public InputHandler inputHandler() {
        return inputHandler;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

}
