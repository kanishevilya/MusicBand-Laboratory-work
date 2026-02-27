package command;

import manager.CollectionManager;

import java.util.List;

public class PrintFieldDescendingAlbumsCountCommand implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingAlbumsCountCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "print_field_descending_albums_count";
    }

    @Override
    public String getDescription() {
        return "вывести значения albumsCount всех элементов в порядке убывания";
    }

    @Override
    public void execute(String[] args) {
        if (collectionManager.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        List<Integer> counts = collectionManager.getAlbumsCountDescending();
        System.out.println("Значения albumsCount в порядке убывания:");
        counts.forEach(c -> System.out.println("  " + c));
    }
}
