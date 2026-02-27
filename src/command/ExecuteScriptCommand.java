package command;

import manager.CommandManager;
import util.InputHandler;
import exception.ScriptEndException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class ExecuteScriptCommand implements Command {

    private final CommandManager commandManager;


    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "execute_script <файл> — считать и исполнить скрипт";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите имя файла. Использование: execute_script <файл>");
            return;
        }

        File file = new File(args[1]);

        if (!file.exists()) {
            System.out.println("Ошибка: файл скрипта не найден: " + args[1]);
            return;
        }
        if (!file.canRead()) {
            System.out.println("Ошибка: нет прав на чтение файла: " + args[1]);
            return;
        }

        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (Exception e) {
            System.out.println("Ошибка: не удалось определить путь файла: " + e.getMessage());
            return;
        }

        Set<String> running = commandManager.getRunningScripts();
        if (running.contains(canonicalPath)) {
            System.out.println("Ошибка: обнаружена рекурсия - файл уже выполняется: " + canonicalPath);
            return;
        }

        InputHandler savedHandler = commandManager.getInputHandler();
        running.add(canonicalPath);

        try (Scanner scriptScanner = new Scanner(file)) {
            commandManager.setInputHandler(new InputHandler(scriptScanner, false));
            System.out.println("Выполнение скрипта: " + canonicalPath);

            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                System.out.println("> " + line);
                commandManager.dispatchLine(line);
            }

            System.out.println("Скрипт выполнен: " + canonicalPath);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден: " + e.getMessage());
        } catch (ScriptEndException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
        } finally {
            commandManager.setInputHandler(savedHandler);
            running.remove(canonicalPath);
        }
    }
}
