package common.protocol.request;

import common.protocol.AbstractRequest;

/**
 * Клиент выходит из файла скрипта: сервер снимает вершину стека для этого клиента.
 */
public final class ScriptSessionEndRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    private final String scriptPathToken;

    public ScriptSessionEndRequest(long requestId, String scriptPathToken) {
        super(requestId);
        this.scriptPathToken = scriptPathToken == null ? "" : scriptPathToken;
    }

    public String getScriptPathToken() {
        return scriptPathToken;
    }
}
