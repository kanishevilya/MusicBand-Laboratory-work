import command.*;
import manager.CollectionManager;
import manager.CommandManager;
import util.InputHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CollectionManager collectionManager = new CollectionManager();
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new InputHandler(scanner, true);


        CommandManager commandManager = new CommandManager(inputHandler);

        commandManager.register(new HelpCommand(commandManager.getCommands()));
        commandManager.register(new InfoCommand(collectionManager));
        commandManager.register(new ShowCommand(collectionManager));
        commandManager.register(new InsertCommand(collectionManager, commandManager));
        commandManager.register(new UpdateCommand(collectionManager, commandManager));
        commandManager.register(new RemoveCommand(collectionManager));
        commandManager.register(new ClearCommand(collectionManager));

        System.out.println("Введите 'help' для получения справки.");

        while (true) {
            System.out.print("\n> ");
            String line;
            try {
                if (!scanner.hasNextLine()) {
                    System.out.println("\nПоток ввода закончен. Завершение программы.");
                    break;
                }
                line = scanner.nextLine().trim();
            } catch (Exception e) {
                System.out.println("Ошибка чтения ввода: " + e.getMessage());
                break;
            }
            if (!line.isEmpty()) {
                commandManager.dispatchLine(line);
            }
        }
    }

}