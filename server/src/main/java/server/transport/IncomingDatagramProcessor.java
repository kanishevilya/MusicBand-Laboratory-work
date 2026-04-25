package server.transport;

import common.exception.DeserializationException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import common.util.BinaryProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.command.CollectionCommandContext;
import server.command.ServerCommandRegistry;

import java.net.SocketAddress;

public final class IncomingDatagramProcessor {

    private static final Logger log = LogManager.getLogger(IncomingDatagramProcessor.class);

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
            log.debug("Обработка запроса: {} (id={})", request.getClass().getSimpleName(), fallbackRequestId);
            return commandRegistry.dispatch(request);
        } catch (DeserializationException e) {
            log.warn("Некорректный запрос от клиента: {}", e.getMessage());
            return new ErrorResponse(fallbackRequestId, "Некорректный запрос: " + e.getMessage());
        } catch (Exception e) {
            log.error("Внутренняя ошибка при обработке запроса: {}", e.getMessage(), e);
            return new ErrorResponse(fallbackRequestId, "Внутренняя ошибка: " + e.getMessage());
        }
    }
}
