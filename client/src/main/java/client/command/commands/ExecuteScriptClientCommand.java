package client.command.commands;

import client.command.ClientCommand;
import client.command.ClientCommandContext;
import client.command.ExecuteScriptRunner;
import common.exception.DeserializationException;
import common.exception.ProtocolException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public final class ExecuteScriptClientCommand implements ClientCommand {

    @Override
    public String name() {
        return "execute_script";
    }

    @Override
    public void execute(String[] args, ClientCommandContext context)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        if (args.length < 2) {
            System.out.println("Использование: execute_script <путь_к_файлу_на_клиенте>");
            return;
        }
        StringBuilder pathArg = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            pathArg.append(' ').append(args[i]);
        }
        Path userPath = Path.of(pathArg.toString().trim());
        Path resolved = userPath.isAbsolute()
                ? userPath.normalize()
                : context.currentScriptBaseDirectory().resolve(userPath).normalize();
        Path absolute = resolved.toAbsolutePath();
        Path parent = absolute.getParent() != null ? absolute.getParent() : Path.of("").toAbsolutePath();

        context.pushScriptBaseDirectory(parent);
        try {
            ExecuteScriptRunner.runScriptFile(absolute, context, context.lineDispatcher());
        } finally {
            context.popScriptBaseDirectory();
        }
    }
}
