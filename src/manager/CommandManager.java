package manager;

import exception.ScriptEndException;
import util.InputHandler;
import command.Command;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Управляет командами
 */
public class CommandManager {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    private InputHandler inputHandler;

    /**
     * Множество запущенных скриптов (для избежание рекурсии)
     */
    private final Set<String> runningScripts = new HashSet<>();

    public CommandManager(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Регистрирует команду
     * 
     * @param command команда
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Обрабатывает строку команды
     * 
     * @param line строка команды
     */
    public void dispatchLine(String line) {
        try {
            if (line == null || line.trim().isEmpty())
                return;
            String[] parts = line.trim().split("\\s+");
            String commandName = parts[0].toLowerCase();
            Command command = commands.get(commandName);
            if (command == null) {
                System.out.println("Неизвестная команда: '" + commandName + "'. Введите 'help' для справки.");
                return;
            }
            command.execute(parts);
        } catch (ScriptEndException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public Set<String> getRunningScripts() {
        return runningScripts;
    }
}
