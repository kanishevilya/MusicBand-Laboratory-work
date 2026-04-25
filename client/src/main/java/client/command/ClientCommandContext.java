package client.command;

import client.network.UdpClient;
import client.util.InputHandler;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public final class ClientCommandContext {

    private final UdpClient client;
    private InputHandler inputHandler;
    private final AtomicLong requestIds;
    private LineDispatchHandler lineDispatcher;
    private final Deque<Path> scriptDirectoryStack = new ArrayDeque<>();

    public ClientCommandContext(UdpClient client, InputHandler inputHandler, AtomicLong requestIds) {
        this.client = client;
        this.inputHandler = inputHandler;
        this.requestIds = requestIds;
    }

    public long nextId() {
        return requestIds.incrementAndGet();
    }

    public AbstractResponse send(AbstractRequest request)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        return client.sendAndReceive(request);
    }

    public void sendAndPrint(AbstractRequest request)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        System.out.println(send(request).getMessage());
    }

    public InputHandler inputHandler() {
        return inputHandler;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setLineDispatcher(LineDispatchHandler lineDispatcher) {
        this.lineDispatcher = lineDispatcher;
    }

    public LineDispatchHandler lineDispatcher() {
        return lineDispatcher;
    }

    /**
     * Каталог для разрешения относительных путей во вложенных
     * {@code execute_script} (родительский каталог текущего файла).
     */
    public Path currentScriptBaseDirectory() {
        Path top = scriptDirectoryStack.peek();
        return top != null ? top : Path.of("").toAbsolutePath();
    }

    public void pushScriptBaseDirectory(Path directory) {
        scriptDirectoryStack.push(directory);
    }

    public void popScriptBaseDirectory() {
        if (!scriptDirectoryStack.isEmpty()) {
            scriptDirectoryStack.pop();
        }
    }

}
