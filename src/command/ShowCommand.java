package command;

import manager.CollectionManager;
import model.MusicBand;

import java.util.Map;

public class ShowCommand implements Command {
    private CollectionManager collectionManager;
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции";
    }

    @Override
    public void execute(String[] args) {
        if (collectionManager.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        System.out.println("Элементы коллекции:");
        for (Map.Entry<Long, MusicBand> entry : collectionManager.getCollection().entrySet()) {
            System.out.println("  Ключ (id) " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
