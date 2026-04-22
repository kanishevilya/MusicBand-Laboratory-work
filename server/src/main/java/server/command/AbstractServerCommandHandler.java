package server.command;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;

public abstract class AbstractServerCommandHandler<T extends AbstractRequest> implements ServerCommandHandler<T> {

    private final Class<T> requestType;
    private final String commandName;
    private final String commandDescription;

    protected AbstractServerCommandHandler(Class<T> requestType, String commandName, String commandDescription) {
        this.requestType = requestType;
        this.commandName = commandName;
        this.commandDescription = commandDescription;
    }

    @Override
    public Class<T> supportedRequestType() {
        return requestType;
    }

    @Override
    public String commandName() {
        return commandName;
    }

    @Override
    public String commandDescription() {
        return commandDescription;
    }

    @Override
    public abstract AbstractResponse handle(T request, CollectionCommandContext context);
}
