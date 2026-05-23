package server.network;

import common.net.udp.SimpleUdpStreamFraming;
import common.exception.ProtocolException;
import server.command.ServerCommandRegistry;
import server.database.DatabaseManager;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Задача для FixedThreadPool чтения.
 * Собирает байты по твоему протоколу SimpleUdpStreamFraming.
 */
public final class ChunkReader implements Runnable {
    private static final Logger log = LogManager.getLogger(ChunkReader.class);

    private static final ConcurrentHashMap<SocketAddress, SimpleUdpStreamFraming.StreamReceiver> receivers = new ConcurrentHashMap<>();

    private final RawPack rawPack;
    private final DatabaseManager databaseManager;
    private final ServerCommandRegistry commandRegistry; // Заменили коллекцию на реестр команд
    private final ExecutorService processingPool;
    private final ExecutorService sendingPool;
    private final DatagramChannel channel;

    public ChunkReader(RawPack rawPack, DatabaseManager databaseManager,
                       ServerCommandRegistry commandRegistry, ExecutorService processingPool,
                       ExecutorService sendingPool, DatagramChannel channel) {
        this.rawPack = rawPack;
        this.databaseManager = databaseManager;
        this.commandRegistry = commandRegistry;
        this.processingPool = processingPool;
        this.sendingPool = sendingPool;
        this.channel = channel;
    }

    @Override
    public void run() {
        SocketAddress clientAddress = rawPack.getClientAddress();
        byte[] bytes = rawPack.getData();

        try {
            receivers.putIfAbsent(clientAddress, new SimpleUdpStreamFraming.StreamReceiver());
            SimpleUdpStreamFraming.StreamReceiver receiver = receivers.get(clientAddress);

            byte[] completeBytes = receiver.feed(bytes, bytes.length);

            if (completeBytes != null) {
                log.info("Сообщение от {} полностью собрано ({} байт). Передаем в пул обработки.",
                        clientAddress, completeBytes.length);
                receivers.remove(clientAddress);

                processingPool.submit(new RequestProcessor(
                        completeBytes, clientAddress, databaseManager, commandRegistry, sendingPool, channel
                ));
            }
        } catch (ProtocolException e) {
            log.error("Ошибка протокола фрагментации от клиента {}", clientAddress, e);
            receivers.remove(clientAddress);
        } catch (Exception e) {
            log.error("Критическая ошибка в потоке чтения чанков", e);
        }
    }
}