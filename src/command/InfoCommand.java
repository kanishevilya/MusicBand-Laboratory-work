package command;

import manager.CollectionManager;

/**
 * Команда info для вывода информации о коллекции
 */
public class InfoCommand implements Command {

    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции";
    }

    /**
     * Выполняет команду
     * 
     * @param args аргументы команды
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Предупреждение: в данной команде не используются аргументы");
        }
        System.out.println("Информация о коллекции:");
        System.out.println("  Тип коллекции   : java.util.TreeMap");
        System.out.println("  Тип элементов   : MusicBand");
        System.out.println("  Дата инициализации: "
                + collectionManager.getInitializationDate().format(CollectionManager.DATE_FORMATTER));
        System.out.println("  Количество элементов: " + collectionManager.size());
    }
}
