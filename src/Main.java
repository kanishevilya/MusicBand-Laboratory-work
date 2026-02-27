import command.*;
import manager.CollectionManager;
import manager.CommandManager;
import util.InputHandler;
import util.XmlParser;
import util.XmlWriter;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java -jar lab1.jar <путь_к_файлу>");
            System.exit(1);
        }

        String filePath = args[0];

        CollectionManager collectionManager = new CollectionManager();
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new InputHandler(scanner, true);
        XmlWriter xmlWriter = new XmlWriter();
        XmlParser xmlParser = new XmlParser();

        try {
            xmlParser.load(filePath, collectionManager);
        } catch (IOException e) {
            System.out.println("Предупреждение: не удалось загрузить коллекцию из файла: " + e.getMessage());
            System.out.println("Начата работа с пустой коллекцией.");
        }

        CommandManager commandManager = new CommandManager(inputHandler);

        commandManager.register(new HelpCommand(commandManager.getCommands()));
        commandManager.register(new InfoCommand(collectionManager));
        commandManager.register(new ShowCommand(collectionManager));
        commandManager.register(new InsertCommand(collectionManager, commandManager));
        commandManager.register(new UpdateCommand(collectionManager, commandManager));
        commandManager.register(new RemoveCommand(collectionManager));
        commandManager.register(new ClearCommand(collectionManager));
        commandManager.register(new ExitCommand());
        commandManager.register(new RemoveGreaterCommand(collectionManager,commandManager));
        commandManager.register(new ReplaceGreaterCommand(collectionManager,commandManager));
        commandManager.register(new ReplaceLowerCommand(collectionManager,commandManager));
        commandManager.register(new AverageOfAlbumsCountCommand(collectionManager));
        commandManager.register(new FilterByAlbumsCountCommand(collectionManager));
        commandManager.register(new PrintFieldDescendingAlbumsCountCommand(collectionManager));
        commandManager.register(new SaveCommand(collectionManager, filePath, xmlWriter));
        commandManager.register(new ExecuteScriptCommand(commandManager));

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