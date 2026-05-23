package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Точка входа для запуска сервера ЛР3.
 */
public class ServerMain {
    private static final Logger log = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) {
        log.info("Запуск приложения MusicBand Server...");

        Server server = new Server();
        server.start();
    }
}