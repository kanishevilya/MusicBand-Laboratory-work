package client.command.commands;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import client.util.MusicBandReader;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByIdRequest;
import common.protocol.request.UpdateRequest;
import common.protocol.response.GetByIdResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class UpdateClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "update";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: update <id>");
            return;
        }
        long id = ClientCommandArgs.parseLong(args[1], "id");
        AbstractResponse getByIdResponse = context.send(new GetByIdRequest(context.nextId(), id));
        if (!(getByIdResponse instanceof GetByIdResponse gir) || !gir.isSuccess() || gir.getBand() == null) {
            System.out.println(getByIdResponse.getMessage());
            return;
        }
        System.out.println("Обновление элемента id=" + id + ":");
        MusicBand updated = MusicBandReader.readForUpdate(context.inputHandler(), gir.getBand());
        context.sendAndPrint(new UpdateRequest(context.nextId(), id, updated));
    }
}
