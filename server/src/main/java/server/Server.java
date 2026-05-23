package server;

import server.command.ServerCommandBootstrap;
import server.command.ServerCommandRegistry;
import server.manager.CollectionManager;
import server.database.DatabaseManager;
import server.network.RawPack;
import server.network.ChunkReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Главный класс сервера, управляющий сетевым конвейером по обновленному ТЗ.
 */
public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 65535;

    private DatabaseManager databaseManager;
    private ServerCommandRegistry commandRegistry;


    private ExecutorService readingPool;
    private ExecutorService processingPool;
    private ExecutorService sendingPool;

    private DatagramChannel channel;

    public void start() {
        try {
            logger.info("Инициализация сервера по обновленному ТЗ ЛР3...");

            this.databaseManager = new DatabaseManager();

            CollectionManager collectionManager = new CollectionManager();
            collectionManager.setCollection(databaseManager.loadCollection());

            this.commandRegistry = ServerCommandBootstrap.createRegistry(collectionManager, databaseManager);

            this.readingPool = Executors.newFixedThreadPool(10);
            this.processingPool = Executors.newFixedThreadPool(4);
            this.sendingPool = Executors.newCachedThreadPool();

            this.channel = DatagramChannel.open();
            this.channel.configureBlocking(false);
            this.channel.bind(new InetSocketAddress(PORT));
            logger.info("UDP-сервер успешно запущен и слушает порт {}", PORT);

            runMainLoop();

        } catch (IOException e) {
            logger.fatal("Критическая ошибка запуска сервера", e);
        }
    }

    private void runMainLoop() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (true) {
            try {
                buffer.clear();
                SocketAddress clientAddress = channel.receive(buffer);

                if (clientAddress != null) {
                    buffer.flip();
                    byte[] rawBytes = new byte[buffer.remaining()];
                    buffer.get(rawBytes);

                    RawPack rawPack = new RawPack(rawBytes, clientAddress);

                    readingPool.submit(new ChunkReader(
                            rawPack, databaseManager, commandRegistry, processingPool, sendingPool, channel
                    ));
                }

                Thread.sleep(5);

            } catch (Exception e) {
                logger.error("Ошибка в неблокирующем сетевом цикле сервера", e);
            }
        }
    }
}