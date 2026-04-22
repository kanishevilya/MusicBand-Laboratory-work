package client.command;

import client.util.InputHandler;
import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.AbstractResponse;
import common.protocol.request.ScriptSessionBeginRequest;
import common.protocol.request.ScriptSessionEndRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Чтение файла скрипта на клиенте: временно подменяет {@link InputHandler}, чтобы многострочные команды
 * (insert и т.д.) читали поля из того же файла; каждая строка-команда уходит на сервер через {@link LineDispatchHandler}.
 * Границы файла — {@link ScriptSessionBeginRequest} / {@link ScriptSessionEndRequest} для проверки рекурсии на сервере.
 */
public final class ExecuteScriptRunner {

    private ExecuteScriptRunner() {
    }

    public static void runScriptFile(Path scriptFile, ClientCommandContext context, LineDispatchHandler dispatch)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        String pathToken = scriptFile.toAbsolutePath().normalize().toString();
        AbstractResponse begin = context.send(new ScriptSessionBeginRequest(context.nextId(), pathToken));
        if (!begin.isSuccess()) {
            System.out.println(begin.getMessage());
            return;
        }
        InputHandler savedInput = context.inputHandler();
        try (Scanner fileScanner = new Scanner(scriptFile, StandardCharsets.UTF_8)) {
            context.setInputHandler(new InputHandler(fileScanner, false));
            try {
                while (fileScanner.hasNextLine()) {
                    String raw = fileScanner.nextLine();
                    String trimmed = raw.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    dispatch.dispatch(trimmed);
                }
            } finally {
                AbstractResponse end = context.send(new ScriptSessionEndRequest(context.nextId(), pathToken));
                if (!end.isSuccess()) {
                    System.out.println(end.getMessage());
                }
            }
        } finally {
            context.setInputHandler(savedInput);
        }
    }
}
