package command;

import manager.CollectionManager;

/**
 * Команда clear для очистки коллекции
 */
public class ClearCommand implements Command {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }

    @Override
    public void execute(String[] args) {
        collectionManager.clear();
        System.out.println("Коллекция очищена.");
    }
}
