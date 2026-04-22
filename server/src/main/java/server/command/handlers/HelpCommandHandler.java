package server.command.handlers;

import common.protocol.AbstractResponse;
import common.protocol.request.HelpRequest;
import common.protocol.response.HelpResponse;
import server.command.AbstractServerCommandHandler;
import server.command.CommandInfo;
import server.command.CollectionCommandContext;

import java.util.Comparator;
import java.util.stream.Collectors;

public final class HelpCommandHandler extends AbstractServerCommandHandler<HelpRequest> {

    public HelpCommandHandler() {
        super(HelpRequest.class, "help", "вывести справку по доступным командам");
    }

    @Override
    public AbstractResponse handle(HelpRequest request, CollectionCommandContext context) {
        long rid = request.getRequestId();
        String message = "Доступные команды:\n"
                + context.commandRegistry().commandInfos().stream()
                        .sorted(Comparator.comparing(CommandInfo::name, String.CASE_INSENSITIVE_ORDER))
                        .map(info -> String.format("  %-37s : %s", info.name(), info.description()))
                        .collect(Collectors.joining(System.lineSeparator()));
        return new HelpResponse(rid, true, message);
    }
}
