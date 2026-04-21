package server.transport;

import common.exception.DeserializationException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import common.util.BinaryProtocol;
import server.command.ServerCommandRegistry;

/**
 * Разбор сырых байтов UDP в объект запроса и делегирование реестру команд.
 * Отвечает только за транспортный слой (десериализация + dispatch).
 */
public final class IncomingDatagramProcessor {

    private final ServerCommandRegistry commandRegistry;

    public IncomingDatagramProcessor(ServerCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public AbstractResponse process(byte[] payload) {
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
