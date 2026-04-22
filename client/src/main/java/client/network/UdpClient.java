package client.network;

import common.exception.DeserializationException;
import common.exception.ProtocolException;
import common.net.udp.SimpleUdpStreamFraming;
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
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Отправка запроса и приём ответа по UDP (неблокирующий канал + селектор) с простой нарезкой по
 * {@link SimpleUdpStreamFraming}.
 */
public class UdpClient implements AutoCloseable {

    public static final int MAX_PACKET = SimpleUdpStreamFraming.PACKET_SIZE;

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
        List<byte[]> chunks = SimpleUdpStreamFraming.split(payload);
        for (byte[] chunk : chunks) {
            ByteBuffer out = ByteBuffer.wrap(chunk);
            while (out.hasRemaining()) {
                channel.send(out, serverAddress);
            }
        }

        SimpleUdpStreamFraming.StreamReceiver receiver = new SimpleUdpStreamFraming.StreamReceiver();
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
                    if (from != null && in.position() > 0 && from.equals(serverAddress)) {
                        in.flip();
                        byte[] data = new byte[in.remaining()];
                        in.get(data);
                        byte[] complete = receiver.feed(data, data.length);
                        if (complete != null) {
                            return BinaryProtocol.deserialize(complete, AbstractResponse.class);
                        }
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
