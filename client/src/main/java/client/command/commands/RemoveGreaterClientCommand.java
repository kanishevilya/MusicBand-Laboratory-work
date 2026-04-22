package client.command.commands;

import client.command.ClientCommand;
import client.command.ClientCommandContext;
import client.util.MusicBandReader;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.model.MusicBand;
import common.protocol.request.RemoveGreaterRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class RemoveGreaterClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "remove_greater";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        System.out.println("Введите эталонный элемент для сравнения (по названию):");
        MusicBand reference = MusicBandReader.read(context.inputHandler());
        reference.setId(1L);
        context.sendAndPrint(new RemoveGreaterRequest(context.nextId(), reference));
    }
}
