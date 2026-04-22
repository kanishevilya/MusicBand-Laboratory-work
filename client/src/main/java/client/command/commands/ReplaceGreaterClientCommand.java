package client.command.commands;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import client.util.MusicBandReader;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByKeyRequest;
import common.protocol.request.ReplaceGreaterRequest;
import common.protocol.response.GetByKeyResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class ReplaceGreaterClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "replace_if_greater";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: replace_if_greater <ключ>");
            return;
        }
        long key = ClientCommandArgs.parseLong(args[1], "ключ");
        AbstractResponse getByKeyResponse = context.send(new GetByKeyRequest(context.nextId(), key));
        if (!(getByKeyResponse instanceof GetByKeyResponse gkr) || !gkr.isSuccess() || gkr.getBand() == null) {
            System.out.println(getByKeyResponse.getMessage());
            return;
        }
        MusicBand newBand = MusicBandReader.readWithNameCheck(context.inputHandler(), gkr.getBand(), false);
        if (newBand == null) {
            return;
        }
        newBand.setId(1L);
        context.sendAndPrint(new ReplaceGreaterRequest(context.nextId(), key, newBand));
    }
}
