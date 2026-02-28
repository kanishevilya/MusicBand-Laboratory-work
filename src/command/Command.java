package command;

/**
 * Интерфейс команды
 */
public interface Command {
    String getName();

    String getDescription();

    /**
     * Выполняет команду
     * 
     * @param args аргументы команды
     */
    void execute(String[] args);
}
