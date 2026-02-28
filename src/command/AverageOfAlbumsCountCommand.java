package command;

import manager.CollectionManager;

/**
 * Команда average_of_albums_count для вывода среднего значения поля albumsCount
 */
public class AverageOfAlbumsCountCommand implements Command {

    private final CollectionManager collectionManager;

    public AverageOfAlbumsCountCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "average_of_albums_count";
    }

    @Override
    public String getDescription() {
        return "вывести среднее значение поля albumsCount";
    }

    @Override
    public void execute(String[] args) {
        if (collectionManager.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }
        double avg = collectionManager.averageOfAlbumsCount();
        System.out.printf("Среднее значение albumsCount: %.2f%n", avg);
    }
}
