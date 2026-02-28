package command;

import manager.CollectionManager;
import manager.CommandManager;
import model.MusicBand;
import util.MusicBandReader;

import java.time.ZonedDateTime;

/**
 * Команда insert для добавления нового элемента в коллекцию
 */
public class InsertCommand implements Command {

    private final CollectionManager collectionManager;

    private final CommandManager commandManager;

    public InsertCommand(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "insert <ключ> - добавить новый элемент с заданным ключом";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите ключ. Использование: insert <ключ>");
            return;
        }
        long key;
        try {
            key = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
            return;
        }
        if (collectionManager.containsKey(key)) {
            System.out.println("Ошибка: элемент с ключом " + key + " уже существует.");
            return;
        }
        MusicBand band = MusicBandReader.read(commandManager.getInputHandler());
        band.setId(collectionManager.generateId());
        band.setCreationDate(ZonedDateTime.now());
        collectionManager.insert(key, band);
        System.out.println("Элемент успешно добавлен с ключом " + key + ".");
    }
}
