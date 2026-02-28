package command;

import manager.CollectionManager;
import manager.CommandManager;
import model.MusicBand;
import util.MusicBandReader;

import java.time.ZonedDateTime;

/**
 * Команда remove_greater для удаления всех элементов, превышающих заданный
 */
public class RemoveGreaterCommand implements Command {

    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    public RemoveGreaterCommand(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "remove_greater — удалить все элементы, превышающие заданный";
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Введите эталонный элемент для сравнения");
        MusicBand reference = MusicBandReader.read(commandManager.getInputHandler());
        reference.setId(1L);
        reference.setCreationDate(ZonedDateTime.now());
        int removed = collectionManager.removeGreater(reference);
        System.out.println("Удалено элементов: " + removed);
    }
}
