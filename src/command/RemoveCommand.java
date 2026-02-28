package command;

import manager.CollectionManager;

/**
 * Команда remove_key для удаления элемента коллекции по ID
 */
public class RemoveCommand implements Command {
    private final CollectionManager collectionManager;

    public RemoveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getDescription() {
        return "remove_key <key> - удалить элемент из коллекции по его ключу";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите key. Использование: remove_key <key>");
            return;
        }
        long key;
        try {
            key = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: key должен быть целым числом.");
            return;
        }
        if (collectionManager.getByKey(key) == null) {
            System.out.println("Ошибка: элемент с key=" + key + " не найден.");
            return;
        }

        if (collectionManager.removeByKey(key)) {
            System.out.println("Элемент с ключом " + key + " удалён.");
        } else {
            System.out.println("Элемент с ключом " + key + " не найден.");
        }
    }
}
