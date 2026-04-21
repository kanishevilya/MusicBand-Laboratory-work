package client.network;

import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.protocol.AbstractRequest;
import common.protocol.AbstractResponse;
import common.util.BinaryProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

/**
 * Отправка запроса и приём ответа по UDP через {@link DatagramChannel} в неблокирующем режиме.
 */
public class UdpClient implements AutoCloseable {

    public static final int MAX_PACKET = 65507;

    private final SocketAddress serverAddress;
    private final int timeoutMs;
    private final DatagramChannel channel;
    private final Selector selector;

    public UdpClient(String host, int port, int timeoutMs) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        this.timeoutMs = timeoutMs;
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_READ);
    }

    public AbstractResponse sendAndReceive(AbstractRequest request)
            throws IOException, TimeoutException, ProtocolException, DeserializationException {
        byte[] payload = BinaryProtocol.serialize(request);
        if (payload.length > MAX_PACKET) {
            throw new ProtocolException("Запрос слишком велик для UDP: " + payload.length + " байт");
        }

        ByteBuffer out = ByteBuffer.wrap(payload);
        while (out.hasRemaining()) {
            channel.send(out, serverAddress);
        }

        ByteBuffer in = ByteBuffer.allocate(MAX_PACKET);
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            long wait = Math.max(1L, deadline - System.currentTimeMillis());
            int ready = selector.select(wait);
            if (ready == 0) {
                continue;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isReadable()) {
                    in.clear();
                    SocketAddress from = channel.receive(in);
                    if (from != null && in.position() > 0) {
                        in.flip();
                        byte[] data = new byte[in.remaining()];
                        in.get(data);
                        return BinaryProtocol.deserialize(data, AbstractResponse.class);
                    }
                }
            }
        }
        throw new TimeoutException("Сервер недоступен или не ответил вовремя (таймаут " + timeoutMs + " мс).");
    }

    @Override
    public void close() throws IOException {
        selector.close();
        channel.close();
    }
}
