package server;

import server.command.ServerCommandBootstrap;
import server.command.ServerCommandRegistry;
import server.manager.CollectionManager;
import server.transport.IncomingDatagramProcessor;
import server.util.XmlParser;
import server.util.XmlWriter;

import java.io.IOException;

/**
 * Запуск сервера: {@code <путь_к_xml> [порт]} — порт по умолчанию 5555.
 */
public class ServerMain {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Использование: java -jar musicband-server.jar <путь_к_xml> [порт]");
            System.exit(1);
        }
        String filePath = args[0];
        int port = args.length >= 2 ? parsePort(args[1]) : 5555;


        
        CollectionManager collectionManager = new CollectionManager();
        XmlParser xmlParser = new XmlParser();
        XmlWriter xmlWriter = new XmlWriter();
        try {
            xmlParser.load(filePath, collectionManager);
        } catch (IOException e) {
            System.out.println("Предупреждение: не удалось загрузить коллекцию: " + e.getMessage());
            System.out.println("Начата работа с пустой коллекцией.");
        }

        ServerCommandRegistry commandRegistry = ServerCommandBootstrap.createRegistry(collectionManager);
        IncomingDatagramProcessor incomingProcessor = new IncomingDatagramProcessor(commandRegistry);

        try {
            UdpServer server = new UdpServer(port, incomingProcessor);
            System.out.println("UDP-сервер слушает порт " + server.getLocalPort() + ", файл: " + filePath);
            server.run();
        } catch (Exception e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int parsePort(String s) {
        try {
            int p = Integer.parseInt(s);
            if (p <= 0 || p > 65535) {
                throw new NumberFormatException();
            }
            return p;
        } catch (NumberFormatException e) {
            System.err.println("Некорректный порт: " + s);
            System.exit(1);
            return 0;
        }
    }
}
