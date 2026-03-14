package command;

import manager.CollectionManager;
import util.XmlWriter;

/**
 * Команда save для сохранения коллекции в файл
 */
public class SaveCommand implements Command {

    private final CollectionManager collectionManager;

    private String filePath;

    /**
     * XML-писатель
     */
    private final XmlWriter xmlWriter;

    public SaveCommand(CollectionManager collectionManager, String filePath, XmlWriter xmlWriter) {
        this.collectionManager = collectionManager;
        this.filePath = filePath;
        this.xmlWriter = xmlWriter;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
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
            String path=xmlWriter.save(filePath, collectionManager);
            setFilePath(path);
            System.out.println("Коллекция успешно сохранена в файл: " + path);
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }
}