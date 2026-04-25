package server.command;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ServerCommandRegistry {

    private static final Logger log = LogManager.getLogger(ServerCommandRegistry.class);

    private final CollectionCommandContext context;
    private final Map<Class<? extends AbstractRequest>, ServerCommandHandler<?>> handlers = new HashMap<>();

    public ServerCommandRegistry(CollectionCommandContext context) {
        this.context = context;
    }

    public CollectionCommandContext context() {
        return context;
    }

    public <T extends AbstractRequest> void register(ServerCommandHandler<T> handler) {
        handlers.put(handler.supportedRequestType(), handler);
    }

    public List<CommandInfo> commandInfos() {
        List<CommandInfo> infos = new ArrayList<>();
        for (ServerCommandHandler<?> handler : handlers.values()) {
            if (!handler.includeInHelp()) {
                continue;
            }
            infos.add(new CommandInfo(handler.commandName(), handler.commandDescription()));
        }
        return infos;
    }

    public AbstractResponse dispatch(AbstractRequest request) {
        long rid = request.getRequestId();
        try {
            ServerCommandHandler<?> handler = handlers.get(request.getClass());
            if (handler == null) {
                log.warn("Неизвестный тип запроса: {}", request.getClass().getName());
                return new ErrorResponse(rid, "Неизвестный тип запроса: " + request.getClass().getName());
            }
            log.debug("Диспетчеризация команды: {} (requestId={})", handler.commandName(), rid);
            return invoke(handler, request);
        } catch (Exception e) {
            log.error("Ошибка выполнения команды (requestId={}): {}", rid, e.getMessage(), e);
            return new ErrorResponse(rid, "Ошибка выполнения: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractRequest> AbstractResponse invoke(ServerCommandHandler<T> handler,
            AbstractRequest request) {
        return handler.handle((T) request, context);
    }
}
