package server.transport;

import common.exception.DeserializationException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import common.util.BinaryProtocol;
import server.command.CollectionCommandContext;
import server.command.ServerCommandRegistry;

import java.net.SocketAddress;


public final class IncomingDatagramProcessor {

    private final ServerCommandRegistry commandRegistry;

    public IncomingDatagramProcessor(ServerCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public AbstractResponse process(byte[] payload, SocketAddress clientAddress) {
        CollectionCommandContext ctx = commandRegistry.context();
        ctx.setCurrentClient(clientAddress);
        try {
            return processWithoutClientBinding(payload);
        } finally {
            ctx.clearCurrentClient();
        }
    }

    private AbstractResponse processWithoutClientBinding(byte[] payload) {
        long fallbackRequestId = 0;
        try {
            AbstractRequest request = BinaryProtocol.deserialize(payload, AbstractRequest.class);
            fallbackRequestId = request.getRequestId();
            return commandRegistry.dispatch(request);
        } catch (DeserializationException e) {
            return new ErrorResponse(fallbackRequestId, "Некорректный запрос: " + e.getMessage());
        } catch (Exception e) {
            return new ErrorResponse(fallbackRequestId, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}
