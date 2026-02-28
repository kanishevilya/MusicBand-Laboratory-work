package command;

import manager.CollectionManager;
import manager.CommandManager;
import model.MusicBand;
import util.MusicBandReader;

/**
 * Команда update для обновления элемента коллекции по ID
 */
public class UpdateCommand implements Command {

    private final CollectionManager collectionManager;

    private final CommandManager commandManager;

    public UpdateCommand(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "update <id> - обновить элемент коллекции по ID";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите ID. Использование: update <id>");
            return;
        }
        long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть целым числом.");
            return;
        }
        if (collectionManager.getById(id) == null) {
            System.out.println("Ошибка: элемент с ID=" + id + " не найден.");
            return;
        }

        MusicBand band = MusicBandReader.read(commandManager.getInputHandler());
        boolean updated = collectionManager.updateById(id, band);
        if (updated) {
            System.out.println("Элемент с ID=" + id + " успешно обновлён.");
        } else {
            System.out.println("Ошибка: не удалось обновить элемент.");
        }
    }
}
