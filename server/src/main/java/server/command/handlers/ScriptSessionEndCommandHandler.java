package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.ScriptSessionEndRequest;
import common.protocol.response.ErrorResponse;
import common.protocol.response.HelpResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CollectionCommandContext;
import server.script.ScriptRecursionGuard;

import java.net.SocketAddress;

public final class ScriptSessionEndCommandHandler extends AbstractServerCommandHandler<ScriptSessionEndRequest> {

    public ScriptSessionEndCommandHandler() {
        super(ScriptSessionEndRequest.class, "script_end", "служебно: выход из файла скрипта на клиенте");
    }

    @Override
    public boolean includeInHelp() {
        return false;
    }

    @Override
    public AbstractResponse handle(ScriptSessionEndRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        String token = request.getScriptPathToken().trim();
        SocketAddress client = context.currentClient();
        if (client == null) {
            return new ErrorResponse(rid, "Внутренняя ошибка: адрес клиента не задан.");
        }
        String err = ScriptRecursionGuard.tryPop(client, token);
        if (err != null) {
            return new ErrorResponse(rid, err);
        }
        return new HelpResponse(rid, true, "OK");
    }
}
