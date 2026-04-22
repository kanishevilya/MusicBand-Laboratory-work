package server;

import common.exception.ProtocolException;
import common.protocol.AbstractResponse;
import server.transport.ChunkedUdpSocketTransport;
import server.transport.IncomingDatagramProcessor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Транспорт: приём фрагментированных UDP-сообщений → обработка через
 * {@link IncomingDatagramProcessor} → ответ чанками.
 */
public class UdpServer {

    public static final int DEFAULT_MAX_PACKET = 65507;

    private final DatagramSocket socket;
    private final ChunkedUdpSocketTransport transport;
    private final IncomingDatagramProcessor incomingProcessor;

    public UdpServer(int port, IncomingDatagramProcessor incomingProcessor) throws SocketException {
        this(new DatagramSocket(port), incomingProcessor);
    }

    public UdpServer(DatagramSocket socket, IncomingDatagramProcessor incomingProcessor) {
        this.socket = socket;
        this.transport = new ChunkedUdpSocketTransport(socket, DEFAULT_MAX_PACKET);
        this.incomingProcessor = incomingProcessor;
    }

    public void run() throws IOException {
        while (true) {
            try {
                ChunkedUdpSocketTransport.Received incoming = transport.receiveComplete();
                System.out.println("Получено от: " + incoming.replyAddress());
                AbstractResponse response = incomingProcessor.process(incoming.payload(), incoming.replyAddress());
                transport.sendComplete(incoming.replyAddress(), response);
            } catch (ProtocolException e) {
                System.err.println("Ошибка протокола UDP: " + e.getMessage());
            }
        }
    }

    public void close() {
        socket.close();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }
}
