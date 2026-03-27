import command.*;
import manager.CollectionManager;
import manager.CommandManager;
import util.InputHandler;
import util.XmlParser;
import util.XmlWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Точка входа в приложение для управления коллекцией музыкальных групп.
 * Программа ожидает один аргумент командной строки — путь к XML-файлу,
 * из которого загружается начальное состояние коллекции.
 */
public class Main {
    public static String filePath;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Введите аргумент командной строки, использование: java -jar MusicBand.jar <путь_к_файлу>");
            System.exit(1);
        }

        filePath = args[0];

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
        commandManager.register(new RemoveGreaterCommand(collectionManager, commandManager));
        commandManager.register(new ReplaceGreaterCommand(collectionManager, commandManager));
        commandManager.register(new ReplaceLowerCommand(collectionManager, commandManager));
        commandManager.register(new AverageOfAlbumsCountCommand(collectionManager));
        commandManager.register(new FilterByAlbumsCountCommand(collectionManager));
        commandManager.register(new PrintFieldDescendingAlbumsCountCommand(collectionManager));
        commandManager.register(new SaveCommand(collectionManager, filePath, xmlWriter));
        commandManager.register(new ExecuteScriptCommand(commandManager));
        commandManager.register(new ShowPricesCommand(collectionManager));
        commandManager.register(new BenchmarkRatesCommand(collectionManager));

        System.out.println("Введите 'help' для получения справки.");

        while (true) {
            System.out.print("\n> ");
            try {
                String line = scanner.nextLine().trim();

                if (!line.isEmpty()) {
                    commandManager.dispatchLine(line);
                }

            }catch (NoSuchElementException e) {
                System.out.println("\nEOF обнаружен. Завершение программы.");
                break;
            }
            catch (Exception e) {
                System.out.println("Ошибка чтения ввода: " + e.getMessage());
            }
        }
    }

}