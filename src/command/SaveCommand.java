package command;

import manager.CollectionManager;
import util.XmlWriter;

public class SaveCommand implements Command {

    private final CollectionManager collectionManager;

    private final String filePath;

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

    @Override
    public void execute(String[] args) {
        try {
            xmlWriter.save(filePath, collectionManager);
            System.out.println("Коллекция успешно сохранена в файл: " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }
}