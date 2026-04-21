package client.command.impl;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import client.util.MusicBandReader;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.model.MusicBand;
import common.protocol.request.InsertRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class InsertClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "insert";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: insert <ключ>");
            return;
        }
        long key = ClientCommandArgs.parseLong(args[1], "ключ");
        System.out.println("Ввод нового элемента:");
        MusicBand band = MusicBandReader.read(context.inputHandler());
        context.sendAndPrint(new InsertRequest(context.nextId(), key, band));
    }
}
