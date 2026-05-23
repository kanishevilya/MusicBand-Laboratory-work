package common.protocol.auth;

import common.protocol.AbstractRequest;

public class RegisterRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;


    public RegisterRequest(long requestId) {
        super(requestId);
    }
}
