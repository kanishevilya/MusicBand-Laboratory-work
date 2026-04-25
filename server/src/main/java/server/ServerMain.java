package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.command.ServerCommandBootstrap;
import server.command.ServerCommandRegistry;
import server.manager.CollectionManager;
import server.transport.IncomingDatagramProcessor;
import server.util.XmlParser;
import server.util.XmlWriter;

import java.io.IOException;

public class ServerMain {

    private static final Logger log = LogManager.getLogger(ServerMain.class);
    private static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Использование: java -jar musicband-server.jar <путь_к_xml> [порт]");
            System.exit(1);
        }
        String filePath = args[0];
        int port = args.length >= 2 ? parsePort(args[1]) : DEFAULT_PORT;

        CollectionManager collectionManager = new CollectionManager();
        XmlParser xmlParser = new XmlParser();
        XmlWriter xmlWriter = new XmlWriter();
        try {
            xmlParser.load(filePath, collectionManager);
        } catch (IOException e) {
            log.warn("Не удалось загрузить коллекцию: {}. Начата работа с пустой коллекцией.", e.getMessage());
        }

        ServerCommandRegistry commandRegistry = ServerCommandBootstrap.createRegistry(collectionManager);
        IncomingDatagramProcessor incomingProcessor = new IncomingDatagramProcessor(commandRegistry);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                xmlWriter.save(filePath, collectionManager);
                log.info("Коллекция сохранена в файл: {}", filePath);
            } catch (IOException e) {
                log.error("Ошибка при сохранении коллекции: {}", e.getMessage());
            }
        }));

        try {
            UdpServer server = new UdpServer(port, incomingProcessor);
            log.info("UDP-сервер слушает порт {}, файл: {}", server.getLocalPort(), filePath);
            server.run();
        } catch (Exception e) {
            log.error("Ошибка сервера: {}", e.getMessage(), e);
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
            log.error("Некорректный порт: {}", s);
            System.exit(1);
            return 0;
        }
    }
}
