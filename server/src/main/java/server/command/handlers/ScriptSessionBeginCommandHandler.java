package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ScriptSessionBeginRequest;
import common.protocol.response.ErrorResponse;
import common.protocol.response.HelpResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;
import server.script.ScriptRecursionGuard;

import java.net.SocketAddress;

public final class ScriptSessionBeginCommandHandler extends AbstractServerCommandHandler<ScriptSessionBeginRequest> {

    public ScriptSessionBeginCommandHandler() {
        super(ScriptSessionBeginRequest.class, "script_begin", "служебно: вход в файл скрипта на клиенте");
    }

    @Override
    public boolean includeInHelp() {
        return false;
    }

    @Override
    public AbstractResponse handle(ScriptSessionBeginRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        String token = request.getScriptPathToken().trim();
        if (token.isEmpty()) {
            return new ErrorResponse(rid, "Пустой токен пути скрипта.");
        }
        SocketAddress client = context.currentClient();
        if (client == null) {
            return new ErrorResponse(rid, "Внутренняя ошибка: адрес клиента не задан.");
        }
        if (ScriptRecursionGuard.contains(client, token)) {
            return new ErrorResponse(rid, "Рекурсивный вызов скрипта: файл уже в стеке исполнения - " + token);
        }
        ScriptRecursionGuard.push(client, token);
        return new HelpResponse(rid, true, "OK");
    }
}
