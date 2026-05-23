package common.protocol;

import java.io.Serializable;

/**
 * Базовый тип запроса клиента к серверу.
 */
public abstract class AbstractRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long requestId;

    private String login;
    private String password;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    protected AbstractRequest(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "AbstractRequest{login='" + login + "', password='" + password + "'}";
    }
}
