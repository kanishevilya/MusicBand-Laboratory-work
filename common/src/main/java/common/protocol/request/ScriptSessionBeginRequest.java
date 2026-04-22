package common.protocol.request;

import common.protocol.AbstractRequest;

/**
 * Клиент входит в файл скрипта: сервер проверяет рекурсию и кладёт токен в стек.
 * Токен — нормализованный абсолютный путь к файлу на стороне клиента (строка).
 */
public final class ScriptSessionBeginRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final String scriptPathToken;

    public ScriptSessionBeginRequest(long requestId, String scriptPathToken) {
        super(requestId);
        this.scriptPathToken = scriptPathToken == null ? "" : scriptPathToken;
    }

    public String getScriptPathToken() {
        return scriptPathToken;
    }
}
