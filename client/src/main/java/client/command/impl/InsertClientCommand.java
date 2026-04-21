package client.command.impl;

import client.command.ClientCommand;
import client.command.ClientCommandArgs;
import client.command.ClientCommandContext;
import client.util.MusicBandReader;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.model.MusicBand;
import common.protocol.AbstractResponse;
import common.protocol.request.GetByIdRequest;
import common.protocol.request.GetByKeyRequest;
import common.protocol.request.InsertRequest;
import common.protocol.response.GetByIdResponse;
import common.protocol.response.GetByKeyResponse;

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
        AbstractResponse getByKeyResponse = context.send(new GetByKeyRequest(context.nextId(), key));
        if (!(getByKeyResponse instanceof GetByKeyResponse gkr)) {
            System.out.println(getByKeyResponse.getMessage());
            return;
        }
        if(gkr.getBand()!=null){
            System.out.println("Элемент с данным ключем уже существует");
            return;
        }
        System.out.println("Ввод нового элемента:");
        MusicBand band = MusicBandReader.read(context.inputHandler());
        context.sendAndPrint(new InsertRequest(context.nextId(), key, band));
    }
}
