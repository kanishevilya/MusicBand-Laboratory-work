package server.network;

import common.protocol.AbstractResponse;
import common.util.BinaryProtocol;
import common.net.udp.SimpleUdpStreamFraming;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Задача для CachedThreadPool отправки ответов.
 * Сериализует объект ответа и отправляет его чанками по протоколу ЛР2.
 */
public final class ResponseSender implements Runnable {
    private static final Logger log = LogManager.getLogger(ResponseSender.class);

    private final AbstractResponse response;
    private final SocketAddress clientAddress;
    private final DatagramChannel channel;

    public ResponseSender(AbstractResponse response, SocketAddress clientAddress, DatagramChannel channel) {
        this.response = response;
        this.clientAddress = clientAddress;
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            byte[] serializedResponse = BinaryProtocol.serialize(response);

            List<byte[]> chunks = SimpleUdpStreamFraming.split(serializedResponse);

            log.debug("Отправка ответа клиенту {} (размер: {} байт, чанков: {})", 
                    clientAddress, serializedResponse.length, chunks.size());

            for (byte[] chunk : chunks) {
                ByteBuffer buffer = ByteBuffer.wrap(chunk);
                channel.send(buffer, clientAddress);
                
                Thread.sleep(1);
            }

            log.info("Ответ успешно отправлен клиенту {}", clientAddress);

        } catch (Exception e) {
            log.error("Ошибка при отправке ответа клиенту {}", clientAddress, e);
        }
    }
}