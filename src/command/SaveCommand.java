package command;

import manager.CollectionManager;
import util.XmlWriter;

/**
 * Команда save для сохранения коллекции в файл
 */
public class SaveCommand implements Command {

    private final CollectionManager collectionManager;

    private final String filePath;

    /**
     * XML-писатель
     */
    private final XmlWriter xmlWriter;

    public SaveCommand(CollectionManager collectionManager, String filePath, XmlWriter xmlWriter) {
        this.collectionManager = collectionManager;
        this.filePath = filePath;
        this.xmlWriter = xmlWriter;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
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
        try {
            xmlWriter.save(filePath, collectionManager);
            System.out.println("Коллекция успешно сохранена в файл: " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }
}