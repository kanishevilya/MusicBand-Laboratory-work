package command;

import manager.CollectionManager;
import model.MusicBand;

import java.util.List;

/**
 * Команда filter_by_albums_count для вывода элементов с заданным albumsCount
 */
public class FilterByAlbumsCountCommand implements Command {
    private final CollectionManager collectionManager;

    public FilterByAlbumsCountCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_by_albums_count";
    }

    public String getDescription() {
        return "filter_by_albums_count <значение> - вывести элементы с данным albumsCount";
    }

    /**
     * Выполняет команду
     * 
     * @param args аргументы команды
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите значение. Использование: filter_by_albums_count <значение>");
            return;
        }
        int count;
        try {
            count = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: albumsCount должен быть целым числом.");
            return;
        }
        List<MusicBand> result = collectionManager.filterByAlbumsCount(count);
        if (result.isEmpty()) {
            System.out.println("Элементы с albumsCount=" + count + " не найдены.");
        } else {
            System.out.println("Элементы с albumsCount=" + count + ":");
            result.forEach(b -> System.out.println("  " + b));
        }
    }
}