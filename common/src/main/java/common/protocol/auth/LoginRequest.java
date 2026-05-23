package common.protocol.auth;

import common.protocol.AbstractRequest;

public class LoginRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;

    public LoginRequest(long requestId) {
        super(requestId);
    }
}
