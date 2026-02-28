package command;

import manager.CollectionManager;
import manager.CommandManager;
import model.MusicBand;
import util.MusicBandReader;

import java.time.ZonedDateTime;

/**
 * Команда replace_if_greater для замены элемента коллекции по ID
 */
public class ReplaceGreaterCommand implements Command {
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    public ReplaceGreaterCommand(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "replace_if_greater";
    }

    @Override
    public String getDescription() {
        return "replace_if_greater <ключ> - заменить значение, если новое больше старого";
    }

    /**
     * Выполняет команду
     * 
     * @param args аргументы команды
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Ошибка: укажите ключ. Использование: replace_if_greater <ключ>");
            return;
        }
        long key;
        try {
            key = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
            return;
        }
        if (!collectionManager.containsKey(key)) {
            System.out.println("Ошибка: элемент с ключом " + key + " не найден.");
            return;
        }
        MusicBand newBand = MusicBandReader.read(commandManager.getInputHandler());
        newBand.setId(1L);
        newBand.setCreationDate(ZonedDateTime.now());

        if (collectionManager.replaceIfGreater(key, newBand, true)) {
            System.out.println("Элемент заменён (новое значение больше старого).");
        } else {
            System.out.println("Замена не выполнена: новое значение не превышает старое.");
        }
    }
}
