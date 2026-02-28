package command;

/**
 * Команда exit для завершения программы
 */
public class ExitCommand implements Command {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "завершить программу (без сохранения)";
    }

    /**
     * Выполняет команду
     * 
     * @param args аргументы команды
     */
    @Override
    public void execute(String[] args) {
        System.out.println("Завершение программы.");
        System.exit(0);
    }
}
