package server;

import common.exception.ProtocolException;
import common.protocol.AbstractResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.transport.ChunkedUdpSocketTransport;
import server.transport.IncomingDatagramProcessor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer {

    private static final Logger log = LogManager.getLogger(UdpServer.class);

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
                log.info("Получено от: {}", incoming.replyAddress());
                AbstractResponse response = incomingProcessor.process(incoming.payload(), incoming.replyAddress());
                transport.sendComplete(incoming.replyAddress(), response);
                log.debug("Ответ отправлен клиенту: {}", incoming.replyAddress());
            } catch (ProtocolException e) {
                log.warn("Ошибка протокола UDP: {}", e.getMessage());
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
