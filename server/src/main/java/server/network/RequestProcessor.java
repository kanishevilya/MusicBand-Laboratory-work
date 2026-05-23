package server.network;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.protocol.response.ErrorResponse;
import common.protocol.response.HelpResponse;
import common.util.BinaryProtocol;
import common.exception.DeserializationException;
import server.command.ServerCommandRegistry;
import server.command.CollectionCommandContext;
import server.database.DatabaseManager;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс-задача для FixedThreadPool обработки запросов.
 * Работает строго на базе существующего ServerCommandRegistry и BinaryProtocol.
 */
public final class RequestProcessor implements Runnable {
    private static final Logger log = LogManager.getLogger(RequestProcessor.class);

    private final byte[] requestBytes;
    private final SocketAddress clientAddress;
    private final DatabaseManager databaseManager;
    private final ServerCommandRegistry commandRegistry;
    private final ExecutorService sendingPool;
    private final DatagramChannel channel;

    public RequestProcessor(byte[] requestBytes, SocketAddress clientAddress,
                            DatabaseManager databaseManager, ServerCommandRegistry commandRegistry,
                            ExecutorService sendingPool, DatagramChannel channel) {
        this.requestBytes = requestBytes;
        this.clientAddress = clientAddress;
        this.databaseManager = databaseManager;
        this.commandRegistry = commandRegistry;
        this.sendingPool = sendingPool;
        this.channel = channel;
    }

    @Override
    public void run() {
        long fallbackRequestId = 0;

        CollectionCommandContext ctx = commandRegistry.context();

        ctx.setCurrentClient(clientAddress);

        try {
            AbstractRequest request = BinaryProtocol.deserialize(requestBytes, AbstractRequest.class);
            log.info("DEBUG SERVER: Получил: " + request.toString());
            fallbackRequestId = request.getRequestId();

            log.info("Пул обработки принял запрос: {} (id={}) от клиента {}",
                    request.getClass().getSimpleName(), fallbackRequestId, clientAddress);

            AbstractResponse response;

            String requestClassName = request.getClass().getSimpleName().toLowerCase();

            if (requestClassName.contains("register")) {
                boolean success = databaseManager.registerUser(request.getLogin(), request.getPassword());
                if (success) {
                    response = new HelpResponse(fallbackRequestId, true, "Регистрация успешно завершена!");
                } else {
                    response = new ErrorResponse(fallbackRequestId, "Ошибка: Логин уже занят.");
                }
            } else if (requestClassName.contains("login")) {
                boolean valid = databaseManager.validateUser(request.getLogin(), request.getPassword());
                if (valid) {
                    response = new HelpResponse(fallbackRequestId, true, "Авторизация успешна!");
                } else {
                    response = new ErrorResponse(fallbackRequestId, "Ошибка: Неверный логин или пароль.");
                }
            } else {
                boolean isAuthenticated = databaseManager.validateUser(request.getLogin(), request.getPassword());

                if (!isAuthenticated) {
                    log.warn("Отказ в доступе для пользователя {} при попытке вызвать {}",
                            request.getLogin(), request.getClass().getSimpleName());
                    response = new ErrorResponse(fallbackRequestId, "Ошибка: Доступ запрещен. Требуется авторизация.");
                } else {
                    response = commandRegistry.dispatch(request);
                }
            }

            sendingPool.submit(new ResponseSender(response, clientAddress, channel));

        } catch (DeserializationException e) {
            log.warn("Некорректный запрос от клиента: {}", e.getMessage());
            AbstractResponse response = new ErrorResponse(fallbackRequestId, "Некорректный запрос: " + e.getMessage());
            sendingPool.submit(new ResponseSender(response, clientAddress, channel));
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера при обработке запроса (id={})", fallbackRequestId, e);
            AbstractResponse response = new ErrorResponse(fallbackRequestId, "Внутренняя ошибка сервера: " + e.getMessage());
            sendingPool.submit(new ResponseSender(response, clientAddress, channel));
        } finally {
            ctx.clearCurrentClient();
        }
    }
}