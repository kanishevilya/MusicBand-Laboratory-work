package server.command;

import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;


public interface ServerCommandHandler<T extends AbstractRequest> {

    Class<T> supportedRequestType();

    String commandName();

    String commandDescription();

    default boolean includeInHelp() {
        return true;
    }

    AbstractResponse handle(T request, CollectionCommandContext context);
}
